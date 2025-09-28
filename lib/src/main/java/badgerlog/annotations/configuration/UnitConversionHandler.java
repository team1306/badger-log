package badgerlog.annotations.configuration;

import badgerlog.annotations.UnitConversion;
import badgerlog.conversion.UnitConversions;

/**
 * Handles the {@link UnitConversion} annotation.
 */
public final class UnitConversionHandler implements ConfigHandler<UnitConversion> {
    @Override
    public void process(UnitConversion annotation, Configuration config) {
        config.withConverter(annotation.converterId(), UnitConversions.createConverter(annotation.value()));
    }
}
