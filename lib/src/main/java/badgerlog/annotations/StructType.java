package badgerlog.annotations;

import badgerlog.annotations.configuration.Configurable;

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
