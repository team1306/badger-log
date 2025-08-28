package badgerlog.annotations.configuration;

import badgerlog.annotations.AutoGenerateStruct;
import badgerlog.annotations.Struct;

/**
 * Internal class to handle the {@link Struct} annotation.
 */
public final class AutoGenerateStructHandler implements ConfigHandler<AutoGenerateStruct> {
    @Override
    public void process(AutoGenerateStruct annotation, Configuration config) {
        config.withAutoGenerateStruct(annotation.autoGenerateStruct());
    }
}
