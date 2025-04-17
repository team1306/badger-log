package badgerlog.entry.configuration.handlers;

import badgerlog.entry.configuration.ConfigHandler;
import badgerlog.entry.configuration.Configuration;

public class MultiUnitConversionHandler implements ConfigHandler<MultiUnitConversion> {
    @Override
    public void process(MultiUnitConversion annotation, Configuration config) {
        for(UnitConversion unitConversion : annotation.value()) {
            if(unitConversion.converterId().isEmpty()) throw new IllegalArgumentException("Converter ID for multi-unit conversion is empty");
            config.withConverter(unitConversion.converterId(), UnitConversionHandler.createConverter(unitConversion));
        }
    }
}
