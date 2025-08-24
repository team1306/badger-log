package badgerlog.annotations.configuration;

import badgerlog.annotations.Key;

public final class KeyHandler implements ConfigHandler<Key> {
    @Override
    public void process(Key annotation, Configuration config) {
        config.withKey(annotation.value());
    }
}
