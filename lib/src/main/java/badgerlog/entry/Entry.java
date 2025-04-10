package badgerlog.entry;

import badgerlog.DashboardConfig;
import edu.wpi.first.util.struct.Struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for publishing or subscribing to a value from NetworkTables
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Entry {
    /**
     * The key used for the entry in NetworkTables
     *
     * @return the string used for NetworkTables. Defaults to a subtable of the base table with the simple name of the class and name of the field as the entry
     */
    String key() default "";

    /**
     * Type of NetworkTable Entry. (Publisher, Subscriber, Sendable)
     *
     * @return the entry type
     * @see EntryType
     */
    EntryType type();

    /**
     * Type of method to publish a {@link Struct} to NetowrkTables 
     * @return the method to publish
     * @see badgerlog.DashboardConfig.StructOptions
     */
    DashboardConfig.StructOptions structOptions() default DashboardConfig.StructOptions.DEFAULT;
}

