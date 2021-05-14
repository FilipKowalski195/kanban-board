package pl.lodz.zzpj.kanbanboard.exceptions;

import javax.validation.ConstraintViolationException;

public class BadOperationException extends BaseException {
    private BadOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BadOperationException validationFailed(ConstraintViolationException e) {
        return new BadOperationException("Data validation failed!", e);
    }
}
