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

@Aspect("pertarget(get(@badgerlog.annotations.Entry * *) || set(@badgerlog.annotations.Entry * *))")
public class FieldAspect {
    private final Map<String, EntryData> entries = new HashMap<>();
    private static final Set<String> fullyProcessedFields = new HashSet<>();
    private final Map<String, Field> fieldMap = new HashMap<>();
    
    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog..*)")
    public void onlyRobotCode() {}
    
    @Pointcut("execution(*.new(..))")
    public void newInitialization() {}
    
    @Pointcut("get(@badgerlog.annotations.Entry * *)")
    public void entryAccess(){}
    
    @Pointcut("set(@badgerlog.annotations.Entry * *)")
    public void entryUpdate(){}
    
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
        String data = field.getName();
        fieldMap.put(data, field);
        
        if (Fields.getFieldValue(field, instance) == null) {
            ErrorLogger.fieldError(field, "is an uninitialized field after construction");
            return;
        }
        
        Configuration config = Configuration.createConfigurationFromFieldAnnotations(field);
        
        if(config.getKey() == null || !KeyParser.hasFieldKey(config.getKey())){
            fullyProcessedFields.add(field.getName());
        }
        
        KeyParser.createKeyFromField(config, field, instance);

        if (!config.isValidConfiguration()) {
            ErrorLogger.fieldError(field, "had an invalid configuration created");
            return;
        }

        NTEntry<?> entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Fields.getFieldValue(field, instance), config);
        Entry annotation = field.getAnnotation(Entry.class);
        
        switch (annotation.value()) {
            case PUBLISHER, SUBSCRIBER, INTELLIGENT -> {
                entries.put(data, new EntryData(entry, annotation.value()));
                Dashboard.addNetworkTableEntry(entry.getKey(), new MockNTEntry(entry));
            }
            case SENDABLE -> Dashboard.addNetworkTableEntry(entry.getKey(), new SendableEntry(config.getKey(), (Sendable) Fields.getFieldValue(field, instance)));
        }
    }
    
    @SneakyThrows
    @Around("onlyRobotCode() && entryAccess()")
    public Object getFieldEntry(ProceedingJoinPoint pjp){
        String name = pjp.getSignature().getName();
        EntryData data = entries.get(name);
        Field targetField = fieldMap.get(name);
        
        if(data == null || Fields.getFieldValue(targetField, pjp.getTarget()) == null){
            return pjp.proceed();
        }
        
        if (data.entryType != EntryType.SUBSCRIBER && data.entryType != EntryType.INTELLIGENT) {
            return pjp.proceed();
        }
        
        NTEntry<?> entry = data.entry;
        return entry.retrieveValue();
    }
    
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Around("onlyRobotCode() && entryUpdate() && args(arg)")
    public Object setFieldEntry(ProceedingJoinPoint pjp, Object arg){
        String name = pjp.getSignature().getName();
        EntryData data = entries.get(name);
        Field targetField = fieldMap.get(name);
        
        if(data == null || Fields.getFieldValue(targetField, pjp.getTarget()) == null){
            return pjp.proceed();
        }
        
        if (data.entryType != EntryType.PUBLISHER && data.entryType != EntryType.INTELLIGENT) {
            return pjp.proceed(pjp.getArgs());
        }
        NTEntry<Object> entry = (NTEntry<Object>) data.entry;
        entry.publishValue(arg);
        
        return pjp.proceed(new Object[]{arg});
    }
    
    private record EntryData(NTEntry<?> entry, EntryType entryType){}
}
