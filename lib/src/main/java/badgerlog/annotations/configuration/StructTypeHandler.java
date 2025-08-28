package badgerlog.annotations.configuration;

import badgerlog.annotations.StructType;

/**
 * Internal class to handle the {@link StructType} annotation.
 */
public final class StructTypeHandler implements ConfigHandler<StructType> {
    @Override
    public void process(StructType annotation, Configuration config) {
        config.withStructOptions(annotation.value());
    }
}
