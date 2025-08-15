package badgerlog.annotations.configuration;

import badgerlog.annotations.Key;

/**
 * Handles {@link Key} annotations by setting the key in the configuration.
 */
public final class KeyHandler implements ConfigHandler<Key> {
    @Override
    public void process(Key annotation, Configuration config) {
        config.withKey(annotation.value());
    }
}
