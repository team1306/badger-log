package badgerlog.entry.configuration.handlers;

import badgerlog.entry.configuration.ConfigHandler;
import badgerlog.entry.configuration.Configuration;

/**
 * Handles {@link Key} annotations by setting the key in the configuration.
 */
public class KeyHandler implements ConfigHandler<Key> {
    @Override
    public void process(Key annotation, Configuration config) {
        config.withKey(annotation.value());
    }
}
