package badgerlog.entry.configuration.handlers;

import badgerlog.StructOptions;
import badgerlog.entry.configuration.Configurable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Configurable
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StructType {
    StructOptions value();
}
