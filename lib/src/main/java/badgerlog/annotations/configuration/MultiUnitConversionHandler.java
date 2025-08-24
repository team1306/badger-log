package badgerlog.annotations.configuration;

import badgerlog.annotations.MultiUnitConversion;
import badgerlog.annotations.UnitConversion;

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
