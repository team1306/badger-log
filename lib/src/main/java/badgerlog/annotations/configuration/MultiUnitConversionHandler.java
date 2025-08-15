package badgerlog.annotations.configuration;

import badgerlog.annotations.MultiUnitConversion;
import badgerlog.annotations.UnitConversion;

/**
 * Handles {@link MultiUnitConversion} annotations by registering multiple converters.
 * Validates that converter IDs are specified for each conversion.
 */
public final class MultiUnitConversionHandler implements ConfigHandler<MultiUnitConversion> {
    @Override
    public void process(MultiUnitConversion annotation, Configuration config) {
        for (UnitConversion unitConversion : annotation.value()) {
            if (unitConversion.converterId().isEmpty())
                throw new IllegalArgumentException("Converter ID for multi-unit conversion is empty");
            config.withConverter(unitConversion.converterId(), UnitConversionHandler.createConverter(unitConversion));
        }
    }
}
