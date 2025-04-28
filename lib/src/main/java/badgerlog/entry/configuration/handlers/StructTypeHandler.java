package badgerlog.entry.configuration.handlers;

import badgerlog.entry.configuration.ConfigHandler;
import badgerlog.entry.configuration.Configuration;

/**
 * Handles {@link StructType} annotations by applying struct options to configuration.
 */
public class StructTypeHandler implements ConfigHandler<StructType> {
    @Override
    public void process(StructType annotation, Configuration config) {
        config.withStructOptions(annotation.value());
    }
}
