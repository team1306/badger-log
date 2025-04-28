package badgerlog.entry.handlers;

import badgerlog.entry.ConfigHandler;
import badgerlog.entry.Configuration;

/**
 * Handles {@link StructType} annotations by applying struct options to configuration.
 */
public final class StructTypeHandler implements ConfigHandler<StructType> {
    @Override
    public void process(StructType annotation, Configuration config) {
        config.withStructOptions(annotation.value());
    }
}
