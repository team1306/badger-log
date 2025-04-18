package badgerlog.entry.configuration.handlers;

import badgerlog.entry.configuration.Configurable;

import java.lang.annotation.*;

@Configurable
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(MultiUnitConversion.class)

public @interface UnitConversion {
    String value();

    String converterId() default "";
}
