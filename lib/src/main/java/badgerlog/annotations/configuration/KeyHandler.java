package badgerlog.annotations.configuration;

import badgerlog.annotations.Key;

/**
 * Handles the {@link Key} annotation.
 */
public final class KeyHandler implements ConfigHandler<Key> {
    @Override
    public void process(Key annotation, Configuration config) {
        config.withKey(annotation.value());
    }
}
