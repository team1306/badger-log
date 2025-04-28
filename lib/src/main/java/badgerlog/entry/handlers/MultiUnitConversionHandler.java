package badgerlog.entry.handlers;

import badgerlog.entry.ConfigHandler;
import badgerlog.entry.Configuration;

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
