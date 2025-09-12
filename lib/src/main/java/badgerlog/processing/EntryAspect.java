package badgerlog.processing;

import badgerlog.Dashboard;
import badgerlog.annotations.Entry;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.networktables.EntryFactory;
import badgerlog.networktables.PublisherNTUpdatable;
import badgerlog.networktables.SendableEntry;
import badgerlog.networktables.SubscriberNTUpdatable;
import badgerlog.utilities.ErrorLogger;
import badgerlog.utilities.Fields;
import badgerlog.utilities.KeyParser;
import edu.wpi.first.util.sendable.Sendable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Internal aspect that enables BadgerLog to use instance fields without any configuration in the constructor of
 * classes that have fields that use {@link Entry}.
 */
@Deprecated
public class EntryAspect {

    private boolean initialFieldPass = false;

    @After("execution(*.new(..)) && !within(edu.wpi.first..*) && !within(badgerlog.processing..*)")
    public void addAllEntryFields(JoinPoint thisJoinPoint) {
        Class<?> staticReference = thisJoinPoint.getSignature().getDeclaringType();
        Object workingClass = thisJoinPoint.getThis();

        Arrays.stream(staticReference.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Entry.class))
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .forEach(field -> handleField(field, workingClass));

        if (!initialFieldPass) {
            Arrays.stream(staticReference.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Entry.class))
                    .filter(field -> Modifier.isStatic(field.getModifiers()))
                    .forEach(field -> handleField(field, workingClass));
            initialFieldPass = true;
        }
    }

    private void handleField(Field field, Object instance) {
        if (instance == null && !Modifier.isStatic(field.getModifiers())) {
            ErrorLogger.fieldError(field, "is an instance field with no instance");
            return;
        }

        if (Fields.getFieldValue(field, instance) == null) {
            ErrorLogger.fieldError(field, "is an uninitialized field after construction");
            return;
        }

        if (Modifier.isFinal(field.getModifiers())) {
            ErrorLogger.fieldError(field, "is a final field");
            return;
        }

        Configuration config = Configuration.createConfigurationFromFieldAnnotations(field);
        boolean hasFieldValue = false; 
        KeyParser.createKeyFromField(config, field, instance);

        if (!hasFieldValue && initialFieldPass) {
            return;
        }

        if (!config.isValidConfiguration()) {
            ErrorLogger.fieldError(field, "had an invalid configuration created");
            return;
        }

        var entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Fields
                .getFieldValue(field, instance), config);
        Entry annotation = field.getAnnotation(Entry.class);

        Dashboard.addNetworkTableEntry(config.getKey(), switch (annotation.value()) {
            case PUBLISHER -> new PublisherNTUpdatable<>(entry, () -> Fields.getFieldValue(field, instance));
            case SUBSCRIBER ->
                new SubscriberNTUpdatable<>(entry, value -> Fields.setFieldValue(instance, field, value));
            case SENDABLE -> new SendableEntry(config.getKey(), (Sendable) Fields.getFieldValue(field, instance));
        });
    }
}
