package badgerlog.entry.configuration.handlers;

import badgerlog.entry.configuration.ConfigHandler;
import badgerlog.entry.configuration.Configuration;

public class UseTypeHandler implements ConfigHandler<UseType> {
    @Override
    public void process(UseType annotation, Configuration config) {
        config.withUseType(annotation.value());
    }
}
