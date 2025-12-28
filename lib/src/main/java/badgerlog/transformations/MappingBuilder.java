package badgerlog.transformations;

import java.util.function.Function;

public class MappingBuilder<S, E> {

    private final Mapping<S, E> mapping;

    private MappingBuilder(Mapping<S, E> mapping) {
        this.mapping = mapping;
    }

    public static <T> MappingBuilder<T, T> create() {
        return new MappingBuilder<>(new Mapping<>(Function.identity(), Function.identity()));
    }

    public <T> MappingBuilder<S, T> with(Mapping<E, T> nextMapping){
        return new MappingBuilder<>(mapping.andThen(nextMapping));
    }

    public E getForwards(S startValue){
        return mapping.evaluateForwards(startValue);
    }

    public S getBackwards(E finalValue){
        return mapping.evaluateBackwards(finalValue);
    }

    public Mapping<S, E> build() {
        return mapping;
    }
}
