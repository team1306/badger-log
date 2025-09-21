package badgerlog.processing;

import badgerlog.Dashboard;
import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.networktables.EntryFactory;
import badgerlog.networktables.MockNTEntry;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.SendableEntry;
import badgerlog.utilities.ErrorLogger;
import badgerlog.utilities.Fields;
import badgerlog.utilities.KeyParser;
import edu.wpi.first.util.sendable.Sendable;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Aspect
public class FieldAspect {
    private final Map<String, NTEntry<?>> entries = new HashMap<>();
    private static final Set<String> fullyProcessedFields = new HashSet<>();
    private final Map<String, Field> fieldMap = new HashMap<>();
    
    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog..*) && !within(java..*) && !within(javax..*)")
    public void onlyRobotCode() {}
    
    @Pointcut("execution(*.new(..))")
    public void newInitialization() {}
    
    @Pointcut("@annotation(entry) && get(* *)")
    public void entryAccess(Entry entry){}
    
    @Pointcut("@annotation(entry) && set(* *)")
    public void entryUpdate(Entry entry){}
    
    @After("onlyRobotCode() && newInitialization()")
    public void createFieldEntries(JoinPoint joinPoint){
        Object instance = joinPoint.getThis();
        Field[] fields = Fields.getFieldsWithAnnotation(instance.getClass(), Entry.class);
        Arrays.stream(fields)
                .filter(field -> !fullyProcessedFields.contains(field.getName()))
                .filter(field -> !entries.containsKey(field.getName()))
                .forEach(field -> createFieldEntry(field, instance));
    }
    
    private void createFieldEntry(Field field, Object instance){
        String name = field.getName();
        fieldMap.put(name, field);
        
        if (Fields.getFieldValue(field, instance) == null) {
            ErrorLogger.fieldError(field, "is an uninitialized field after construction");
            return;
        }
        
        Configuration config = Configuration.createConfigurationFromAnnotations(field);
        
        if(config.getKey() == null || !KeyParser.hasFieldKey(config.getKey())){
            fullyProcessedFields.add(name);
        }
        
        KeyParser.createKeyFromMember(config, field, instance);

        if (!config.isValidConfiguration()) {
            ErrorLogger.fieldError(field, "had an invalid configuration created");
            return;
        }

        NTEntry<?> entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Fields.getFieldValue(field, instance), config);
        Entry annotation = field.getAnnotation(Entry.class);
        
        switch (annotation.value()) {
            case PUBLISHER, SUBSCRIBER, INTELLIGENT -> {
                entries.put(name, entry);
                Dashboard.addNetworkTableEntry(entry.getKey(), new MockNTEntry(entry));
            }
            case SENDABLE -> Dashboard.addNetworkTableEntry(entry.getKey(), new SendableEntry(config.getKey(), (Sendable) Fields.getFieldValue(field, instance)));
        }
    }
    
    @SneakyThrows
    @Around(value = "onlyRobotCode() && entryAccess(annotation)", argNames = "pjp, annotation")
    public Object getFieldEntry(ProceedingJoinPoint pjp, Entry annotation){
        String name = pjp.getSignature().getName();
        NTEntry<?> entry = entries.get(name);
        Field targetField = fieldMap.get(name);
        EntryType entryType = annotation.value();
        
        if(entry == null || targetField == null){
            return pjp.proceed();
        }
        
        if(Fields.getFieldValue(targetField, pjp.getTarget()) == null){
            return pjp.proceed();
        }
        
        if (entryType != EntryType.SUBSCRIBER && entryType != EntryType.INTELLIGENT) {
            return pjp.proceed();
        }
        
        return entry.retrieveValue();
    }
    
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Around(value = "onlyRobotCode() && entryUpdate(annotation) && args(arg)", argNames = "pjp, arg, annotation")
    public Object setFieldEntry(ProceedingJoinPoint pjp, Object arg, Entry annotation){
        String name = pjp.getSignature().getName();
        NTEntry<Object> entry = (NTEntry<Object>) entries.get(name);
        Field targetField = fieldMap.get(name);
        EntryType entryType = annotation.value();

        if(entry == null || targetField == null){
            return pjp.proceed(pjp.getArgs());
        }
        
        if(Fields.getFieldValue(targetField, pjp.getTarget()) == null){
            return pjp.proceed(pjp.getArgs());
        }
        
        if (entryType != EntryType.PUBLISHER && entryType != EntryType.INTELLIGENT) {
            return pjp.proceed(pjp.getArgs());
        }
        entry.publishValue(arg);
        
        return pjp.proceed(new Object[]{arg});
    }
}
