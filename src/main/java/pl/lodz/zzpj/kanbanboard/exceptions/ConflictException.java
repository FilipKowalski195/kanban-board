package pl.lodz.zzpj.kanbanboard.exceptions;

public class ConflictException extends BaseException {
    private static final String CONFLICT_MSG = "%s could not be processed due to not unique %s = %s";

    private ConflictException(String message) {
        super(message);
    }

    public static ConflictException uniqueField(Class<?> cls, String fieldName, Object value) {
        return new ConflictException(String.format(
                CONFLICT_MSG,
                cls.getSimpleName(),
                fieldName,
                value
        ));
    }
}
