package badgerlog.annotations.configuration;

import badgerlog.annotations.StructType;

/**
 * Handles {@link StructType} annotations by applying struct options to configuration.
 */
public final class StructTypeHandler implements ConfigHandler<StructType> {
    @Override
    public void process(StructType annotation, Configuration config) {
        config.withStructOptions(annotation.value());
    }
}
