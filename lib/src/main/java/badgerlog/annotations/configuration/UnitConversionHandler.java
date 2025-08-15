package badgerlog.annotations.configuration;

import badgerlog.annotations.UnitConversion;
import badgerlog.networktables.mappings.UnitConversions;
import badgerlog.networktables.mappings.UnitConverter;

/**
 * Handles {@link UnitConversion} annotations by creating and registering converters.
 */
public final class UnitConversionHandler implements ConfigHandler<UnitConversion> {
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
