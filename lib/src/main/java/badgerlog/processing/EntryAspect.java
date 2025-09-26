package badgerlog.processing;

import badgerlog.Dashboard;
import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.networktables.EntryFactory;
import badgerlog.networktables.MockNTEntry;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.NTUpdatable;
import badgerlog.networktables.SendableEntry;
import badgerlog.utilities.ErrorLogger;
import badgerlog.utilities.Fields;
import badgerlog.utilities.KeyParser;
import badgerlog.utilities.Methods;
import edu.wpi.first.util.sendable.Sendable;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

@Aspect
public class EntryAspect {
    private final Entries entries = new Entries(new HashMap<>());
    
    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog..*) && !within(java..*) && !within(javax..*)")
    public void onlyRobotCode() {}
    
    @Pointcut("execution(*.new(..))")
    public void newInitialization() {}
    
    @Pointcut("@annotation(entry) && get(* *)")
    public void entryAccess(Entry entry){}
    
    @Pointcut("@annotation(entry) && set(* *)")
    public void entryUpdate(Entry entry){}

    @Pointcut("@annotation(entry) && execution(* *()) && !execution(void *())")
    public void getterMethodExecution(Entry entry) {} 
    
    @After("onlyRobotCode() && newInitialization()")
    public void createInstanceFieldEntries(JoinPoint joinPoint){
        Object instance = joinPoint.getThis();
        entries.addInstance(instance.getClass(), instance);
        entries.addInstance(instance.getClass(), null);
        
        Field[] fields = Fields.getFieldsWithAnnotation(instance.getClass(), Entry.class);
        Arrays.stream(fields).forEach(field -> createFieldEntry(field, instance));
        
        Method[] methods = Methods.getMethodsWithAnnotation(instance.getClass(), Entry.class);
        Arrays.stream(methods).forEach(method -> createMethodEntry(method, instance));
    }
    
    private <T extends Member & AnnotatedElement> Configuration createConfigurationFromMember(T member, Object instance) {
        Class<?> clazz = member.getDeclaringClass();

        Configuration config = Configuration.createConfigurationFromAnnotations(member);

        KeyParser.createKeyFromMember(config, member, instance, entries.getClassData(clazz).getInstanceCount());

        if(config.getKey() == null){
            ErrorLogger.memberError(member, "has a missing key");
            return config.makeInvalid();
        }
        
        return config;
    }
    
    
    private void createFieldEntry(Field field, Object instance){
        Class<?> clazz = field.getDeclaringClass();
        String name = field.getName();
        entries.getClassData(clazz).addField(field);
        
        if (Fields.getFieldValue(field, instance) == null) {
            ErrorLogger.memberError(field, "is an uninitialized field");
            return;
        }
        
        Configuration config = createConfigurationFromMember(field, instance);
        
        if (!config.isValidConfiguration()) {
            ErrorLogger.memberError(field, "had an invalid configuration created");
            return;
        }

        NTEntry<?> entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Fields.getFieldValue(field, instance), config);
        Entry annotation = field.getAnnotation(Entry.class);
        
        switch (annotation.value()) {
            case PUBLISHER, SUBSCRIBER, INTELLIGENT -> {
                entries.getInstanceEntries(clazz, Modifier.isStatic(field.getModifiers()) ? null : instance).addEntry(name, entry);
                Dashboard.addNetworkTableEntry(entry.getKey(), new MockNTEntry(entry));
            }
            case SENDABLE -> Dashboard.addNetworkTableEntry(entry.getKey(), new SendableEntry(config.getKey(), (Sendable) Fields.getFieldValue(field, instance)));
        }
    }
    
    private void createMethodEntry(Method method, Object instance){
        if (method.getAnnotation(Entry.class).value() != EntryType.PUBLISHER) {
            ErrorLogger.memberError(method, "cannot be a non-publisher entry");
            return;
        }
        
        Configuration config = createConfigurationFromMember(method, instance);

        if (!config.isValidConfiguration()) {
            ErrorLogger.memberError(method, "had an invalid configuration created");
            return;
        }
        
        NTEntry<Object> entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Methods.invokeMethod(method, instance), config);
        Dashboard.addNetworkTableEntry(config.getKey(), (NTUpdatable) () -> entry.publishValue(Methods.invokeMethod(method, instance)));
    }
    
    @SneakyThrows
    @Around(value = "onlyRobotCode() && entryAccess(annotation)", argNames = "pjp, annotation")
    public Object getFieldEntry(ProceedingJoinPoint pjp, Entry annotation){
        EntryType entryType = annotation.value();
        if (entryType != EntryType.SUBSCRIBER && entryType != EntryType.INTELLIGENT) {
            return pjp.proceed();
        }

        String name = pjp.getSignature().getName();
        Class<?> containingClass = pjp.getSignature().getDeclaringType();
        Object target = pjp.getTarget();

        FieldEntryData entryData = createFieldData(name, containingClass, target);

        if(!entryData.valid()){
            return pjp.proceed();
        }

        Object value = entryData.entry().retrieveValue();
        Fields.setFieldValue(pjp.getTarget(), entryData.targetField(), value);
        return value;
    }
    
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Around(value = "onlyRobotCode() && entryUpdate(annotation) && args(arg)", argNames = "pjp, arg, annotation")
    public Object setFieldEntry(ProceedingJoinPoint pjp, Object arg, Entry annotation){
        EntryType entryType = annotation.value();
        if (entryType != EntryType.PUBLISHER && entryType != EntryType.INTELLIGENT) {
            return pjp.proceed(pjp.getArgs());
        }

        String name = pjp.getSignature().getName();
        Class<?> containingClass = pjp.getSignature().getDeclaringType();
        Object target = pjp.getTarget();
        
        FieldEntryData entryData = createFieldData(name, containingClass, target);

        if(!entryData.valid()){
            return pjp.proceed(pjp.getArgs());
        }
        
        if(Fields.getFieldValue(entryData.targetField(), target) == null){
            ErrorLogger.customError(String.format("Field %s was null when it should not have been", pjp.getSignature().getName()));
            return pjp.proceed(pjp.getArgs());
        }

        NTEntry<Object> entry = (NTEntry<Object>) entryData.entry();
        entry.publishValue(arg);
        
        return pjp.proceed(new Object[]{arg});
    }
    
    private FieldEntryData createFieldData(String name, Class<?> containingClass, Object target) {
        ClassData data = entries.getClassData(containingClass);
        if(data == null) {
            return new FieldEntryData(false, null, null);
        }

        InstanceData instanceData = data.getInstanceEntries().get(target);
        Field field = data.getFieldMap().get(name);
        
        if (instanceData == null) {
            return new FieldEntryData(false, null, null);
        }
        
        if(field == null) {
            return new FieldEntryData(false, null, null);
        }
        
        NTEntry<?> entry = instanceData.getEntry(name);
        
        if(entry == null) {
            return new FieldEntryData(false, null, null);
        }
        
        return new FieldEntryData(true, entry, field);
    }

    private record FieldEntryData(boolean valid, NTEntry<?> entry, Field targetField) {}
}
