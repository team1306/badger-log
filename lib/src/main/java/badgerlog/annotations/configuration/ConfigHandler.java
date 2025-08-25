package badgerlog.annotations.configuration;

import java.lang.annotation.Annotation;

/**
 * Processing for all registered annotations.
 *
 * @param <T> the type of the annotation
 */
public interface ConfigHandler<T extends Annotation> {
    /**
     * Processes a specific annotation and changes the configuration according to it.
     * @param annotation the annotation's values
     * @param config the configuration object to use
     */
    void process(T annotation, Configuration config);
}