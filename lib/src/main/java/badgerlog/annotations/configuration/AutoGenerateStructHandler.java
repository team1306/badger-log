package badgerlog.annotations.configuration;

import badgerlog.annotations.AutoGenerateStruct;
import badgerlog.annotations.StructType;

/**
 * Internal class to handle the {@link StructType} annotation.
 */
public final class AutoGenerateStructHandler implements ConfigHandler<AutoGenerateStruct> {
    @Override
    public void process(AutoGenerateStruct annotation, Configuration config) {
        config.withAutoGenerateStruct(annotation.autoGenerateStruct());
    }
}
