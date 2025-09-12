package badgerlog.processing;

import badgerlog.Dashboard;
import badgerlog.annotations.Entry;
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
import java.util.Map;

@Aspect("pertypewithin(*)")
public class FieldAspect {
    private final Map<FieldData, NTEntry<?>> entries = new HashMap<>();
    
    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog.processing..*))")
    public void onlyRobotCode() {}
    
    @Pointcut("initialization(*.new(..))")
    public void newInitialization() {}

    @Pointcut("staticinitialization(*)")
    public void staticInitialization() {}
    
    @Pointcut("get(@badgerlog.annotations.Entry * *))")
    public void entryAccess(){}
    
    @Pointcut("set(@badgerlog.annotations.Entry * *)")
    public void entryUpdate(){}
    
    @After("onlyRobotCode() && newInitialization()")
    public void createFieldEntries(JoinPoint joinPoint){
        Object instance = joinPoint.getThis();
        Field[] fields = Fields.getFieldsWithAnnotation(instance.getClass(), Entry.class, true);
        Arrays.stream(fields).forEach(field -> createFieldEntry(field, instance));
    }
    
    @After("onlyRobotCode() && staticInitialization()")
    public void createStaticFieldEntries(JoinPoint joinPoint){
        Class<?> clazz = joinPoint.getSignature().getDeclaringType();
        Field[] fields = Fields.getFieldsWithAnnotation(clazz, Entry.class, false);
        Arrays.stream(fields).forEach(field -> createFieldEntry(field, null));
    }
    
    private void createFieldEntry(Field field, Object instance){
        if (Fields.getFieldValue(field, instance) == null) {
            ErrorLogger.fieldError(field, "is an uninitialized field after construction");
            return;
        }
        Configuration config = Configuration.createConfigurationFromFieldAnnotations(field);
        KeyParser.createKeyFromField(config, field, instance);

        if (!config.isValidConfiguration()) {
            ErrorLogger.fieldError(field, "had an invalid configuration created");
            return;
        }

        NTEntry<?> entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Fields.getFieldValue(field, instance), config);
        Entry annotation = field.getAnnotation(Entry.class);
        
        switch (annotation.value()) {
            case PUBLISHER, SUBSCRIBER -> {
                entries.put(new FieldData(field.getName(), instance), entry);
                Dashboard.addNetworkTableEntry(entry.getKey(), new MockNTEntry(entry));
            }
            case SENDABLE -> Dashboard.addNetworkTableEntry(entry.getKey(), new SendableEntry(config.getKey(), (Sendable) Fields.getFieldValue(field, instance)));
        }
    }
    
    @SneakyThrows
    @Around("onlyRobotCode() && entryAccess()")
    public Object getFieldEntry(ProceedingJoinPoint pjp){
        NTEntry<?> entry = entries.get(new FieldData(pjp.getSignature().getName(), pjp.getTarget()));
        return entry.retrieveValue();
    }
    
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Around("onlyRobotCode() && entryUpdate()")
    public Object setFieldEntry(ProceedingJoinPoint pjp){
        Object arg = pjp.getArgs()[0];
        NTEntry<Object> entry = (NTEntry<Object>) entries.get(new FieldData(pjp.getSignature().getName(), pjp.getTarget()));
        entry.publishValue(arg);
        
        return pjp.proceed(new Object[]{arg});
    }
    
    private record FieldData(String fieldName, Object instance){}
}
