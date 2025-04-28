package badgerlog.entry;

import java.lang.annotation.Annotation;

/**
 * Interface for processing annotation-based configurations.
 * Implementations handle specific annotation types to modify configuration settings.
 *
 * @param <T> The annotation type this handler processes
 */
public interface ConfigHandler<T extends Annotation> {
    /**
     * Processes the annotation to apply configuration changes.
     *
     * @param annotation The annotation instance to process
     * @param config     The configuration object to modify
     */
    void process(T annotation, Configuration config);
}