package badgerlog.annotations.configuration;

import badgerlog.annotations.AutoGenerateStruct;

public final class AutoGenerateStructHandler implements ConfigHandler<AutoGenerateStruct> {
    @Override
    public void process(AutoGenerateStruct annotation, Configuration config) {
        config.withAutoGenerateStruct(annotation.autoGenerateStruct());
    }
}
