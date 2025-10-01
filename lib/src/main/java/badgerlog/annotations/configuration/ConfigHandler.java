package badgerlog.annotations.configuration;

import java.lang.annotation.Annotation;

/**
 * Handles annotations and changes the configuration according to them.
 *
 * @param <T> the type of the annotation
 */
public interface ConfigHandler<T extends Annotation> {
    /**
     * Processes a specific annotation and changes the configuration according to its value.
     *
     * @param annotation the annotation's values
     * @param config the configuration object to use
     */
    void process(T annotation, Configuration config);
}
