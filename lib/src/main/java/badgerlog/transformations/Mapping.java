package badgerlog.transformations;

import java.util.function.Function;

public record Mapping<S, E>(Function<S, E> forwards, Function<E, S> backwards) {

    public E evaluateForwards(S value){
        return forwards.apply(value);
    }

    public S evaluateBackwards(E value){
        return backwards.apply(value);
    }

    public <T> Mapping<S, T> andThen(Mapping<E, T> next) {
        return new Mapping<>(
                this.forwards.andThen(next.forwards),
                next.backwards.andThen(this.backwards)
        );
    }
    
    @SuppressWarnings("unchecked")
    public static <T, J>  Mapping<T, J> identity(){
        return new Mapping<>((Function<T, J>) Function.identity(), (Function<J, T>) Function.identity());
    }
}
