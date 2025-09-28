package badgerlog.annotations.configuration;

import badgerlog.annotations.Struct;

/**
 * Handles the {@link Struct} annotation.
 */
public final class StructTypeHandler implements ConfigHandler<Struct> {
    @Override
    public void process(Struct annotation, Configuration config) {
        config.withStructType(annotation.value());
    }
}
