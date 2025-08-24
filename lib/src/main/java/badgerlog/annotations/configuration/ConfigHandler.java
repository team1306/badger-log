package badgerlog.annotations.configuration;

import java.lang.annotation.Annotation;

public interface ConfigHandler<T extends Annotation> {
    void process(T annotation, Configuration config);
}