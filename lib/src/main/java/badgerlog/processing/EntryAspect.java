package badgerlog.processing;

import badgerlog.Dashboard;
import badgerlog.annotations.Entry;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.networktables.EntryFactory;
import badgerlog.networktables.PublisherUpdater;
import badgerlog.networktables.SendableEntry;
import badgerlog.networktables.SubscriberUpdater;
import edu.wpi.first.util.sendable.Sendable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@SuppressWarnings("-javadoc")
@Aspect
public class EntryAspect {

    @Pointcut("execution(*.new(..)) && !within(edu.wpi.first..*) && !within(EntryAspect)")
    public static void constructor() {
    }

    @After("constructor()")
    public void addAllEntryFields(JoinPoint thisJoinPoint) {
        Class<?> staticReference = thisJoinPoint.getSignature().getDeclaringType();
        Object workingClass = thisJoinPoint.getThis();

        Arrays.stream(staticReference.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Entry.class)).forEach(field -> handleField(field, workingClass));
    }

    private void handleField(Field field, Object instance) {
        if (instance == null && !Modifier.isStatic(field.getModifiers())) {
            System.out.println(field.getDeclaringClass().getSimpleName() + "." + field.getName() + " is an instance field, with no instance. SKIPPING");
            return;
        }

        if (Fields.getFieldValue(field, instance) == null) {
            System.out.println(field.getDeclaringClass().getSimpleName() + "." + field.getName() + " is a uninitialized field after construction. SKIPPING");
            return;
        }

        if (Modifier.isFinal(field.getModifiers())) {
            System.out.println(field.getDeclaringClass().getSimpleName() + "." + field.getName() + " is a final field. SKIPPING");
            return;
        }

        Configuration config = Configuration.createConfigurationFromField(field);

        var entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Fields.getFieldValue(field, instance), config);
        Entry annotation = field.getAnnotation(Entry.class);
        Dashboard.addUpdatingNetworkTableEntry(switch (annotation.value()) {
            case Publisher -> new PublisherUpdater<>(entry, () -> Fields.getFieldValue(field, instance));
                    case Subscriber ->
                            new SubscriberUpdater<>(entry, value -> Fields.setFieldValue(instance, field, value));
                    case Sendable ->
                            new SendableEntry(config.getKey(), (Sendable) Fields.getFieldValue(field, instance));
                }
        );
    }
}
