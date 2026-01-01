package badgerlog.annotations.configuration;

import badgerlog.annotations.Convert;

/**
 * Handles the {@link Convert} annotation.
 */
public final class ConvertHandler implements ConfigHandler<Convert> {
    @Override
    public void process(Convert annotation, Configuration config) {
        config.withUnitConversion(annotation.value());
    }
}
