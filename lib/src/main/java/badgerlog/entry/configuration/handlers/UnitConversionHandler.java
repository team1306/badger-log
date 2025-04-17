package badgerlog.entry.configuration.handlers;

import badgerlog.entry.configuration.ConfigHandler;
import badgerlog.entry.configuration.Configuration;
import badgerlog.networktables.mappings.conversion.UnitConversions;
import badgerlog.networktables.mappings.conversion.UnitConverter;

public class UnitConversionHandler implements ConfigHandler<UnitConversion> {
    public static UnitConverter<?> createConverter(UnitConversion annotation) {
        return UnitConversions.createConverter(annotation.value());
    }

    @Override
    public void process(UnitConversion annotation, Configuration config) {
        config.withConverter(annotation.converterId(), createConverter(annotation));
    }
}
