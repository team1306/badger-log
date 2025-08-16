package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark fields for publishing or subscribing to NetworkTables entries.
 * Specifies the interaction type (Publisher, Subscriber, or Sendable) for the annotated field.
 *
 * @see EntryType
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Entry {
    /**
     * Specifies the interaction type for the annotated field.
     *
     * @return The {@code EntryType} for this field
     */
    EntryType value();
}

