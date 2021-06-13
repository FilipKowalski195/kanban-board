package pl.lodz.zzpj.kanbanboard.utils;

import java.util.function.Function;

public class Composer<T> {

    private final T instance;

    private Composer(T instance) {
        this.instance = instance;
    }

    public static <T> Composer<T> startWith(T ref) {
        return new Composer<>(ref);
    }

    public <R> Composer<R> injectInto(Function<T, R> injector) {
        return new Composer<>(injector.apply(this.instance));
    }

    public T compose() {
        return instance;
    }
}
