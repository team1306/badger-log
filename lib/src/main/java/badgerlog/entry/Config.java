package badgerlog.entry;

import badgerlog.networktables.mappings.Mappings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration for an {@link Entry}, see specific mappings for the correct strings to use
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Config {
    /**
     * The configuration option value as a string for use in mappings
     * @return the string config option
     * @see Mappings
     */
    String value();
}
