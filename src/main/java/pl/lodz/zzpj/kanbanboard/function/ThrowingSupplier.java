package pl.lodz.zzpj.kanbanboard.function;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get();
}
