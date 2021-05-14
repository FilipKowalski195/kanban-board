package pl.lodz.zzpj.kanbanboard.service;

import org.springframework.transaction.TransactionSystemException;
import pl.lodz.zzpj.kanbanboard.exceptions.BadOperationException;
import pl.lodz.zzpj.kanbanboard.function.ThrowingSupplier;

import javax.validation.ConstraintViolationException;

public abstract class BaseService {
    public <T> T catchingValidation(ThrowingSupplier<T> supplier) throws BadOperationException {
        try {
            return supplier.get();
        } catch (TransactionSystemException e) {
            if (e.getCause().getCause() instanceof ConstraintViolationException) {
                throw BadOperationException.validationFailed(
                        (ConstraintViolationException) e.getCause().getCause()
                );
            }
            throw e;
        }
    }
}
