package badgerlog.entry.configuration.handlers;

import badgerlog.entry.configuration.Configurable;

import java.lang.annotation.*;

@Configurable
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface MultiUnitConversion {
    UnitConversion[] value();
}
