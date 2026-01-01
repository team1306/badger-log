package badgerlog.aspects;

import badgerlog.BadgerLog;
import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.SendableMarker;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.NTUpdatable;
import badgerlog.networktables.SendableEntry;
import badgerlog.processing.data.ClassData;
import badgerlog.processing.data.Entries;
import badgerlog.processing.data.InstanceData;
import badgerlog.transformations.EntryFactory;
import badgerlog.utilities.Members;
import edu.wpi.first.util.sendable.Sendable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.FieldSignature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

//todo events
@SuppressWarnings("AopLanguageInspection") 
public aspect NewEntryAspect {
    private final Entries entries = new Entries(new HashMap<>());

    public interface EntryContainer {
        
    } 
    
    declare parents: (@badgerlog.annotations.Entry * || hasfield(@badgerlog.annotations.Entry * *) || hasmethod(@badgerlog.annotations.Entry * *())) implements EntryContainer;

    pointcut newInitialization():
            execution((EntryContainer+).new(..));

    pointcut staticInitialization():
            staticinitialization(EntryContainer+);

    pointcut entryAccess(Entry entry):
            @annotation(entry) && get(* EntryContainer+.*);
    
    pointcut entryUpdate(Entry entry):
            @annotation(entry) && set(* EntryContainer+.*);
    
    pointcut entryAccessInEntryClass():
            !@annotation(badgerlog.annotations.Entry) &&
                    get(!final * EntryContainer+.*);
    
    pointcut entryUpdateInEntryClass():
            !@annotation(badgerlog.annotations.Entry) &&
                    set(!final * EntryContainer+.*);

    after(): staticInitialization(){
        //todo better logging
        Class<?> clazz = thisJoinPoint.getSignature().getDeclaringType();

        entries.addInstance(clazz, null);

        Members.iterateOverAnnotatedFields(clazz, Entry.class, true, field -> createFieldEntry(field, null));
        Members.iterateOverAnnotatedMethods(clazz, Entry.class, true, method -> createMethodEntry(method, null));
        
        Members.iterateOverAnnotatedFields(clazz, SendableMarker.class, true, field -> createSendableEntry(field, null));
    }
    
    after(): newInitialization(){
        //todo better logging
        Object instance = thisJoinPoint.getThis();
        Class<?> clazz = thisJoinPoint.getSignature().getDeclaringType();

        entries.addInstance(clazz, instance);

        Members.iterateOverAnnotatedFields(instance
                .getClass(), Entry.class, false, field -> createFieldEntry(field, instance));

        Members.iterateOverAnnotatedMethods(instance
                .getClass(), Entry.class, false, method -> createMethodEntry(method, instance));

        Members.iterateOverAnnotatedFields(clazz, SendableMarker.class, false, field -> createSendableEntry(field, instance));


        if (clazz.isAnnotationPresent(Entry.class)) {
            Field[] allFields = clazz.getFields();
            Arrays.stream(allFields)
                    .filter(ProcessingUtils::isValidForClassGeneration)
                    .forEach(field -> createFieldEntry(field, instance));
        }
    }
    
    private void createFieldEntry(Field field, Object instance){
        //todo better logging
        Class<?> clazz = field.getDeclaringClass();
        String name = field.getName();
        Object fieldValue = Members.getFieldValue(field, instance);

        if (fieldValue == null) {
            //todo better logging
            return;
        }
        
        Configuration config = Configuration.createConfigurationFromAnnotations(field);
        String key = ProcessingUtils.createKeyFromMember(config, field, instance, entries.getClassData(clazz).getInstanceCount());

        if (Modifier.isStatic(field.getModifiers()) && entries.getInstanceEntries(clazz, null).hasEntry(name)) {
            //todo actually need??
            return;
        }
        
        NTEntry<?> entry = EntryFactory.createEntry(key, fieldValue, config);
        entries.getInstanceEntries(clazz, instance).addEntry(name, entry);
    }

    private void createSendableEntry(Field field, Object instance){
        //todo better logging
        Class<?> clazz = field.getDeclaringClass();
        Class<?> type = field.getType();
        Object fieldValue = Members.getFieldValue(field, instance);

        if(!type.isAssignableFrom(Sendable.class)){
            //todo better logging
            return;
        }

        if (fieldValue == null) {
            //todo better logging
            return;
        }

        Configuration config = Configuration.createConfigurationFromAnnotations(field);
        String key = ProcessingUtils.createKeyFromMember(config, field, instance, entries.getClassData(clazz).getInstanceCount());

        Sendable sendable = (Sendable) fieldValue;

        BadgerLog.addNetworkTableEntry(key, new SendableEntry(key, sendable));
    }

    @SuppressWarnings("unchecked")
    private void createMethodEntry(Method method, Object instance) {
        //todo better logging
        Class<?> clazz = method.getDeclaringClass();
        
        if (method.getAnnotation(Entry.class).value() != EntryType.PUBLISHER) {
            //todo better logging
            return;
        }

        Configuration config = Configuration.createConfigurationFromAnnotations(method);
        String key = ProcessingUtils.createKeyFromMember(config, method, instance, entries.getClassData(clazz).getInstanceCount());
        
        Object methodValue = Members.invokeMethod(method, instance);
        
        if(methodValue == null){
            //todo better logging
            return;
        }
        
        NTEntry<Object> entry = (NTEntry<Object>) EntryFactory.createEntry(key, methodValue, config);
        
        BadgerLog.addNetworkTableEntry(key, (NTUpdatable) () -> entry.publishValue(Members.invokeMethod(method, instance)));
    }

    Object around(Entry annotation): entryAccess(annotation) {
        Object value = getFieldEntry(thisJoinPoint, annotation);

        if(value == null){
            return proceed(thisJoinPoint.getArgs());
        }
        else{
            return value;
        }
    }

    Object around(Object arg, Entry annotation): entryUpdate(annotation) && args(arg) {
        setFieldEntry(thisJoinPoint, arg, annotation);
        return proceed(thisJoinPoint.getArgs());
    }

    Object around(): entryAccessInEntryClass() {
        @SuppressWarnings("unchecked")
        Entry annotation = (Entry) thisJoinPoint.getSignature().getDeclaringType().getAnnotation(Entry.class);
        Object value = getFieldEntry(thisJoinPoint, annotation);

        if(value == null){
            return proceed(thisJoinPoint.getArgs());
        }
        else{
            return value;
        }
    }

    Object around(Object arg): entryUpdateInEntryClass() && args(arg) {
        @SuppressWarnings("unchecked")
        Entry annotation = (Entry) thisJoinPoint.getSignature().getDeclaringType().getAnnotation(Entry.class);

        setFieldEntry(thisJoinPoint, arg, annotation);
        return proceed(thisJoinPoint.getArgs());
    }

    @SuppressWarnings("unchecked")
    private Object getFieldEntry(JoinPoint pjp, Entry annotation) {
        EntryType entryType = annotation.value();
        if (entryType != EntryType.SUBSCRIBER && entryType != EntryType.INTELLIGENT) {
            return null;
        }

        FieldSignature signature = (FieldSignature) pjp.getSignature();

        String name = pjp.getSignature().getName();
        Class<?> containingClass = pjp.getSignature().getDeclaringType();
        Object target = pjp.getTarget();

        FieldEntryData entryData = createFieldData(name, containingClass, target);

        if (!entryData.valid()) {
            return null;
        }

        NTEntry<Object> entry = (NTEntry<Object>) entryData.entry();

        Object value = entry.retrieveValue();

        Members.setFieldValue(pjp.getTarget(), signature.getField(), value);
        entry.publishValue(value);

        return value;
    }

    @SuppressWarnings("unchecked")
    private void setFieldEntry(org.aspectj.lang.JoinPoint pjp, Object arg, Entry annotation) {
        EntryType entryType = annotation.value();
        if (entryType != EntryType.PUBLISHER && entryType != EntryType.INTELLIGENT) {
            return;
        }

        if (arg == null) {
            //todo better logging
            return;
        }

        String name = pjp.getSignature().getName();
        Class<?> containingClass = pjp.getSignature().getDeclaringType();
        Object target = pjp.getTarget();

        FieldEntryData entryData = createFieldData(name, containingClass, target);

        if (!entryData.valid()) {
            return;
        }

        NTEntry<Object> entry = (NTEntry<Object>) entryData.entry();

        entry.publishValue(arg);
    }

    private FieldEntryData createFieldData(String name, Class<?> containingClass, Object target) {
        ClassData data = entries.getClassData(containingClass);
        if (data == null) {
            return new FieldEntryData(false, null);
        }

        InstanceData instanceData = data.instanceEntries().get(target);

        if (instanceData == null) {
            return new FieldEntryData(false, null);
        }

        NTEntry<?> entry = instanceData.getEntry(name);

        if (entry == null) {
            return new FieldEntryData(false, null);
        }

        return new FieldEntryData(true, entry);
    }

    private record FieldEntryData(boolean valid, NTEntry<?> entry) {}

    
}
