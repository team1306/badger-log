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
import java.util.Map;

@Aspect("pertarget(get(@badgerlog.annotations.Entry * *) || set(@badgerlog.annotations.Entry * *))")
public class FieldAspect {
    @Deprecated
    private final Map<FieldData, EntryData> entries = new HashMap<>();
    
    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog..*)")
    public void onlyRobotCode() {}
    
    @Deprecated
    @Pointcut("execution(*.new(..))")
    public void newInitialization() {}

    @Deprecated
    @Pointcut("staticinitialization(*)")
    public void staticInitialization() {}
    
    @Pointcut("get(@badgerlog.annotations.Entry * *)")
    public void entryAccess(){}
    
    @Pointcut("set(@badgerlog.annotations.Entry * *)")
    public void entryUpdate(){}
    
    @Deprecated
    @After("onlyRobotCode() && newInitialization()")
    public void createFieldEntries(JoinPoint joinPoint){
        Object instance = joinPoint.getThis();
        Field[] fields = Fields.getFieldsWithAnnotation(instance.getClass(), Entry.class);
        Arrays.stream(fields).forEach(field -> createFieldEntry(field, instance));
    }
    
    private void createFieldEntry(Field field, Object instance){
        @Deprecated
        FieldData data = new FieldData(field.getName(), instance);
        if(entries.containsKey(data)){
            return;
        }
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
        EntryData data = entries.get(new FieldData(pjp.getSignature().getName(), pjp.getTarget()));
        if (data == null || (data.entryType != EntryType.SUBSCRIBER && data.entryType != EntryType.INTELLIGENT)) {
            return pjp.proceed();
        }
        NTEntry<?> entry = data.entry;
        return entry.retrieveValue();
    }
    
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Around("onlyRobotCode() && entryUpdate()")
    public Object setFieldEntry(ProceedingJoinPoint pjp){
        Object arg = pjp.getArgs()[0];
        EntryData data = entries.get(new FieldData(pjp.getSignature().getName(), pjp.getTarget()));
        if (data == null || (data.entryType != EntryType.SUBSCRIBER && data.entryType != EntryType.INTELLIGENT)) {
            return pjp.proceed(pjp.getArgs());
        }
        NTEntry<Object> entry = (NTEntry<Object>) data.entry;
        entry.publishValue(arg);
        
        return pjp.proceed(new Object[]{arg});
    }
    
    private record FieldData(String fieldName, Object instance){}
    private record EntryData(NTEntry<?> entry, EntryType entryType){}
}
