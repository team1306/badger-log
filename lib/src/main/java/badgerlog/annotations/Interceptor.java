package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Interceptor {
    Class<?> type();

    String name() default "";

    EventType eventType() default EventType.ALL;

    String[] keys() default {};

    int priority() default 0;
}
