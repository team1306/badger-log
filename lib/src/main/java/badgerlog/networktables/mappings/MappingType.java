package badgerlog.networktables.mappings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that a field should be automatically collected and registered as {@code Mappings}.
 *
 * <p>
 * Fields marked with this annotation must be of type {@link Mapping}. This annotation
 * <p>
 * is retained at runtime and can only be applied to fields.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingType {
}
