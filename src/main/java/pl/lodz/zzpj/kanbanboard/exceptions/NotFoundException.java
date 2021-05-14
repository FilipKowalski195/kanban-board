package pl.lodz.zzpj.kanbanboard.exceptions;

public class NotFoundException extends BaseException {
    private static final String NOT_FOUND_MSG = "%s could not be found by %s = %s";

    private NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException notFound(Class<?> cls, String fieldName, Object value) {
        return new NotFoundException(String.format(NOT_FOUND_MSG, cls.getSimpleName(), fieldName, value));
    }
}
