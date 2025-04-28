package badgerlog.entry.handlers;

import badgerlog.entry.ConfigHandler;
import badgerlog.entry.Configuration;
import badgerlog.networktables.mappings.conversion.UnitConversions;
import badgerlog.networktables.mappings.conversion.UnitConverter;

/**
 * Handles {@link UnitConversion} annotations by creating and registering converters.
 */
public class UnitConversionHandler implements ConfigHandler<UnitConversion> {
    /**
     * Factory method to create a converter from annotation data
     *
     * @param annotation The {@link UnitConversion} annotation
     * @return Instantiated unit converter
     */
    public static UnitConverter<?> createConverter(UnitConversion annotation) {
        return UnitConversions.createConverter(annotation.value());
    }

    @Override
    public void process(UnitConversion annotation, Configuration config) {
        config.withConverter(annotation.converterId(), createConverter(annotation));
    }
}
