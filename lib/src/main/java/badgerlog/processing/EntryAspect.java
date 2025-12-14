package badgerlog.processing;

import badgerlog.BadgerLog;
import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.NoEntry;
import badgerlog.annotations.Watched;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.events.EventRegistry;
import badgerlog.networktables.EntryFactory;
import badgerlog.networktables.MockNTEntry;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.NTUpdatable;
import badgerlog.networktables.SendableEntry;
import badgerlog.processing.data.ClassData;
import badgerlog.processing.data.Entries;
import badgerlog.processing.data.InstanceData;
import badgerlog.utilities.ErrorLogger;
import badgerlog.utilities.KeyParser;
import badgerlog.utilities.Members;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Utilizes AspectJ to weave entry generation and management into target classes.
 */
@Aspect
public class EntryAspect {
    private final Entries entries = new Entries(new HashMap<>());
    private final List<Class<?>> blacklistedClasses = new ArrayList<>();

    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog..*) && !within(java..*) && !within(javax..*)")
    public void onlyRobotCode() {
    }

    @Pointcut("execution(*.new(..))")
    public void newInitialization() {
    }

    @Pointcut("@annotation(entry) && get(* *)")
    public void entryAccess(Entry entry) {
    }

    @Pointcut("@annotation(entry) && set(* *)")
    public void entryUpdate(Entry entry) {
    }

    @Pointcut("!@annotation(badgerlog.annotations.Entry) && get(!final * (@badgerlog.annotations.Entry *).*)")
    public void entryAccessInEntryClass() {
    }

    @Pointcut("!@annotation(badgerlog.annotations.Entry) && set(!final * (@badgerlog.annotations.Entry *).*)")
    public void entryUpdateInEntryClass() {
    }

    @Pointcut("@annotation(entry) && execution(* *()) && !execution(void *())")
    public void getterMethodExecution(Entry entry) {
    }

    @After("onlyRobotCode() && staticinitialization(*)")
    public void createStaticEntries(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getSignature().getDeclaringType();
        if (blacklistedClasses.contains(clazz)) {
            return;
        }
        if (!Members.hasAnyOfAnnotation(clazz, Entry.class)) {
            blacklistedClasses.add(clazz);
        }

        entries.addInstance(clazz, null);

        Members.iterateOverAnnotatedFields(clazz, Entry.class, true, field -> createFieldEntry(field, null));

        Members.iterateOverAnnotatedMethods(clazz, Entry.class, true, method -> createMethodEntry(method, null));
    }

    @After("onlyRobotCode() && newInitialization()")
    public void createInstanceEntries(JoinPoint joinPoint) {
        Object instance = joinPoint.getThis();
        Class<?> clazz = joinPoint.getSignature().getDeclaringType();

        if (blacklistedClasses.contains(clazz)) {
            return;
        }
        if (!Members.hasAnyOfAnnotation(clazz, Entry.class)) {
            blacklistedClasses.add(clazz);
        }

        entries.addInstance(clazz, instance);

        Members.iterateOverAnnotatedFields(instance
                .getClass(), Entry.class, false, field -> createFieldEntry(field, instance));

        Members.iterateOverAnnotatedMethods(instance
                .getClass(), Entry.class, false, method -> createMethodEntry(method, instance));

        if (clazz.isAnnotationPresent(Entry.class)) {
            Field[] allFields = clazz.getFields();
            Arrays.stream(allFields)
                    .filter(this::isValidForClassGeneration)
                    .forEach(field -> createFieldEntry(field, instance));
        }
    }

    private <T extends Member & AnnotatedElement> Configuration createConfigurationFromMember(T member, Object instance) {
        Class<?> clazz = member.getDeclaringClass();

        Configuration config = Configuration.createConfigurationFromAnnotations(member);

        if (Members.isMemberNonStatic(member)) {
            KeyParser.createKeyFromMember(config, member, instance, entries.getClassData(clazz).getInstanceCount());
        } else {
            KeyParser.createKeyFromStaticMember(config, member);
        }

        if (config.getKey() == null) {
            ErrorLogger.memberError(member, "has a missing key");
            return config.makeInvalid();
        }

        return config;
    }


    private void createFieldEntry(Field field, Object instance) {
        Class<?> clazz = field.getDeclaringClass();
        String name = field.getName();
        entries.getClassData(clazz).addField(field);

        if (Members.getFieldValue(field, instance) == null) {
            ErrorLogger.memberError(field, "is an uninitialized field");
            return;
        }

        Configuration config = createConfigurationFromMember(field, instance);

        if (!config.isValidConfiguration()) {
            ErrorLogger.memberError(field, "had an invalid configuration created");
            return;
        }

        if (Modifier.isStatic(field.getModifiers()) && entries.getInstanceEntries(clazz, null).hasEntry(name)) {
            return;
        }

        Entry annotation = field.getAnnotation(Entry.class);

        if (annotation == null) {
            annotation = clazz.getAnnotation(Entry.class);
        }

        if (annotation.value() == EntryType.SENDABLE) {
            BadgerLog.addNetworkTableEntry(config.getKey(), new SendableEntry(config
                    .getKey(), (Sendable) Members.getFieldValue(field, instance)));
            return;
        }

        NTEntry<?> entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Members
                .getFieldValue(field, instance), config);
        registerAnyManagedEvents(entry, field);


        switch (annotation.value()) {
            case PUBLISHER, SUBSCRIBER, INTELLIGENT -> {
                entries.getInstanceEntries(clazz, Modifier.isStatic(field.getModifiers()) ? null : instance)
                        .addEntry(name, entry);
                BadgerLog.addNetworkTableEntry(entry.getKey(), new MockNTEntry(entry));
            }
            default -> {
            }
        }
    }

    private void createMethodEntry(Method method, Object instance) {
        if (method.getAnnotation(Entry.class).value() != EntryType.PUBLISHER) {
            ErrorLogger.memberError(method, "cannot be a non-publisher entry");
            return;
        }

        Configuration config = createConfigurationFromMember(method, instance);

        if (!config.isValidConfiguration()) {
            ErrorLogger.memberError(method, "had an invalid configuration created");
            return;
        }

        NTEntry<Object> entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Members
                .invokeMethod(method, instance), config);
        registerAnyManagedEvents(entry, method);

        BadgerLog.addNetworkTableEntry(config.getKey(), (NTUpdatable) () -> entry.publishValue(Members
                .invokeMethod(method, instance)));
    }

    private void registerAnyManagedEvents(NTEntry<?> entry, AnnotatedElement member) {
        Watched watched = member.getAnnotation(Watched.class);
        if (watched == null) return;

        EventRegistry.addWatchedEntry(entry, Arrays.asList(watched.value()));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows(Throwable.class)
    @Around(value = "onlyRobotCode() && entryAccess(annotation)", argNames = "pjp, annotation")
    public Object getFieldEntry(ProceedingJoinPoint pjp, Entry annotation) {
        EntryType entryType = annotation.value();
        if (entryType != EntryType.SUBSCRIBER && entryType != EntryType.INTELLIGENT) {
            return pjp.proceed();
        }

        String name = pjp.getSignature().getName();
        Class<?> containingClass = pjp.getSignature().getDeclaringType();
        Object target = pjp.getTarget();

        FieldEntryData entryData = createFieldData(name, containingClass, target);

        if (!entryData.valid()) {
            return pjp.proceed();
        }

        NTEntry<Object> entry = (NTEntry<Object>) entryData.entry();

        Object value = entry.retrieveValue();

        Members.setFieldValue(pjp.getTarget(), entryData.targetField(), value);
        entry.publishValue(value);

        return value;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows(Throwable.class)
    @Around(value = "onlyRobotCode() && entryUpdate(annotation) && args(arg)", argNames = "pjp, arg, annotation")
    public Object setFieldEntry(ProceedingJoinPoint pjp, Object arg, Entry annotation) {
        EntryType entryType = annotation.value();
        if (entryType != EntryType.PUBLISHER && entryType != EntryType.INTELLIGENT) {
            return pjp.proceed(pjp.getArgs());
        }

        String name = pjp.getSignature().getName();
        Class<?> containingClass = pjp.getSignature().getDeclaringType();
        Object target = pjp.getTarget();

        FieldEntryData entryData = createFieldData(name, containingClass, target);

        if (!entryData.valid()) {
            return pjp.proceed(pjp.getArgs());
        }

        if (Members.getFieldValue(entryData.targetField(), target) == null) {
            ErrorLogger.customError(String.format("Field %s was null when it should not have been", pjp.getSignature()
                    .getName()));
            return pjp.proceed(pjp.getArgs());
        }

        NTEntry<Object> entry = (NTEntry<Object>) entryData.entry();

        entry.publishValue(arg);

        return pjp.proceed(new Object[] {arg});
    }

    @SuppressWarnings("unchecked")
    @Around("onlyRobotCode() && entryUpdateInEntryClass() && args(arg)")
    public Object setClassFieldEntry(ProceedingJoinPoint pjp, Object arg) {
        Entry annotation = (Entry) pjp.getSignature().getDeclaringType().getAnnotation(Entry.class);
        return setFieldEntry(pjp, arg, annotation);
    }

    @SuppressWarnings("unchecked")
    @Around("onlyRobotCode() && entryAccessInEntryClass()")
    public Object getClassFieldEntry(ProceedingJoinPoint pjp) {
        Entry annotation = (Entry) pjp.getSignature().getDeclaringType().getAnnotation(Entry.class);
        return getFieldEntry(pjp, annotation);
    }

    private FieldEntryData createFieldData(String name, Class<?> containingClass, Object target) {
        ClassData data = entries.getClassData(containingClass);
        if (data == null) {
            return new FieldEntryData(false, null, null);
        }

        InstanceData instanceData = data.instanceEntries().get(target);
        Field field = data.fieldMap().get(name);

        if (instanceData == null) {
            return new FieldEntryData(false, null, null);
        }

        if (field == null) {
            return new FieldEntryData(false, null, null);
        }

        NTEntry<?> entry = instanceData.getEntry(name);

        if (entry == null) {
            return new FieldEntryData(false, null, null);
        }

        return new FieldEntryData(true, entry, field);
    }

    private record FieldEntryData(boolean valid, NTEntry<?> entry, Field targetField) {}

    private boolean isValidForClassGeneration(Field field) {
        return Members.isMemberNonStatic(field) && !Modifier.isFinal(field.getModifiers()) && !field
                .isAnnotationPresent(NoEntry.class) && !field.isAnnotationPresent(Entry.class);
    }
}
