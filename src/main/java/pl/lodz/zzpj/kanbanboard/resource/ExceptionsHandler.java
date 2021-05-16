package pl.lodz.zzpj.kanbanboard.resource;

import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.lodz.zzpj.kanbanboard.dto.ErrorResponseDto;
import pl.lodz.zzpj.kanbanboard.exceptions.BadOperationException;
import pl.lodz.zzpj.kanbanboard.exceptions.ConflictException;
import pl.lodz.zzpj.kanbanboard.exceptions.ForbiddenException;
import pl.lodz.zzpj.kanbanboard.exceptions.NotFoundException;
import pl.lodz.zzpj.kanbanboard.utils.DateProvider;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionsHandler {

    private final DateProvider dateProvider;

    @Autowired
    public ExceptionsHandler(DateProvider provider) {
        this.dateProvider = provider;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(HttpServletRequest req, NotFoundException exception) {
        var response = ErrorResponseDto.from(HttpStatus.NOT_FOUND, req, exception, dateProvider.now());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(HttpServletRequest req, ConflictException exception) {
        var response = ErrorResponseDto.from(HttpStatus.CONFLICT, req, exception, dateProvider.now());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> handleForbidden(HttpServletRequest req, ForbiddenException exception) {
        var response = ErrorResponseDto.from(HttpStatus.FORBIDDEN, req, exception, dateProvider.now());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(HttpServletRequest req, MethodArgumentNotValidException exception) {
        var response = ErrorResponseDto.fromSpringValidation(
                req,
                exception,
                dateProvider.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(BadOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleBadOperation(HttpServletRequest req, BadOperationException exception) {

        if (exception.getCause() instanceof ConstraintViolationException) {
            var response = ErrorResponseDto.fromJavaxValidation(
                    req,
                    (ConstraintViolationException) exception.getCause(),
                    dateProvider.now()
            );
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        var response = ErrorResponseDto.from(
                HttpStatus.BAD_REQUEST,
                req,
                exception,
                dateProvider.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);

    }

}
