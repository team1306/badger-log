package badgerlog.annotations.configuration;

import badgerlog.annotations.MultiUnitConversion;
import badgerlog.annotations.UnitConversion;
import badgerlog.conversion.UnitConversions;

/**
 * Internal class to handle the {@link MultiUnitConversion} annotation.
 */
public final class MultiUnitConversionHandler implements ConfigHandler<MultiUnitConversion> {
    @Override
    public void process(MultiUnitConversion annotation, Configuration config) {
        for (UnitConversion unitConversion : annotation.value()) {
            if (unitConversion.converterId().isEmpty()) {
                throw new IllegalArgumentException("Converter ID for multi-unit conversion is empty");
            }
            config.withConverter(unitConversion.converterId(), UnitConversions.createConverter(unitConversion.value()));
        }
    }
}
