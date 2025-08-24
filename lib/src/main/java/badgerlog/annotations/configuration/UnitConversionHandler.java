package badgerlog.annotations.configuration;

import badgerlog.annotations.UnitConversion;
import badgerlog.conversion.UnitConversions;
import badgerlog.conversion.UnitConverter;

public final class UnitConversionHandler implements ConfigHandler<UnitConversion> {
    public static UnitConverter<?> createConverter(UnitConversion annotation) {
        return UnitConversions.createConverter(annotation.value());
    }

    @Override
    public void process(UnitConversion annotation, Configuration config) {
        config.withConverter(annotation.converterId(), createConverter(annotation));
    }
}
