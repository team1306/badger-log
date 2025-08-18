package badgerlog.annotations.configuration;

import badgerlog.annotations.AutoGenerateStruct;

public class AutoGenerateStructHandler implements ConfigHandler<AutoGenerateStruct> {
    @Override
    public void process(AutoGenerateStruct annotation, Configuration config) {
        config.withAutoGenerateStruct(annotation.autoGenerateStruct());
    }
}
