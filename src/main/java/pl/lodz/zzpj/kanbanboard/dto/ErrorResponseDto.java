package pl.lodz.zzpj.kanbanboard.dto;

import com.google.common.collect.Iterators;
import lombok.Builder;
import lombok.Value;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class ErrorResponseDto {

    private static final String CONSTRAINT_JAVAX_MSG = "%s %s";
    private static final String CONSTRAINT_SPRING_MSG = "%s.%s %s";

    Instant timestamp;

    int status;

    String error;

    List<String> messages;

    String path;

    public static ErrorResponseDto from(
            HttpStatus status,
            HttpServletRequest request,
            Exception exception,
            Instant timestamp
    ) {
        return ErrorResponseDto.builder()
                .error(status.getReasonPhrase())
                .status(status.value())
                .messages(List.of(exception.getMessage()))
                .timestamp(timestamp)
                .path(request.getContextPath() + request.getServletPath())
                .build();
    }

    public static ErrorResponseDto fromJavaxValidation(
            HttpServletRequest request,
            ConstraintViolationException exception,
            Instant timestamp
    ) {
        var status = HttpStatus.BAD_REQUEST;
        var messages = exception
                .getConstraintViolations()
                .stream()
                .map(ErrorResponseDto::parseJavaxConstraint)
                .collect(Collectors.toList());

        return ErrorResponseDto.builder()
                .error(status.getReasonPhrase())
                .status(status.value())
                .messages(messages)
                .timestamp(timestamp)
                .path(request.getContextPath() + request.getServletPath())
                .build();
    }

    private static String parseJavaxConstraint(ConstraintViolation<?> constraintViolation) {
        var lastPathPart = Iterators
                .getLast(constraintViolation.getPropertyPath().iterator())
                .getName();

        return String.format(
                CONSTRAINT_JAVAX_MSG,
                lastPathPart,
                constraintViolation.getMessage()
        );
    }

    public static ErrorResponseDto fromSpringValidation(
            HttpServletRequest request,
            MethodArgumentNotValidException exception,
            Instant timestamp
    ) {
        var status = HttpStatus.BAD_REQUEST;
        var messages = exception
                .getAllErrors()
                .stream()
                .map(ErrorResponseDto::parseSpringConstraint)
                .collect(Collectors.toList());

        return ErrorResponseDto.builder()
                .error(status.getReasonPhrase())
                .status(status.value())
                .messages(messages)
                .timestamp(timestamp)
                .path(request.getContextPath() + request.getServletPath())
                .build();
    }

    private static String parseSpringConstraint(ObjectError objectError) {
        var args = objectError.getArguments();
        var isNameUnavailable = args == null ||
                args.length == 0 ||
                args[0] == null ||
                !(args[0] instanceof DefaultMessageSourceResolvable);

        if (isNameUnavailable) {
            return objectError.getDefaultMessage();
        }

        return String.format(
                CONSTRAINT_SPRING_MSG,
                objectError.getObjectName(),
                ((DefaultMessageSourceResolvable) args[0]).getDefaultMessage(),
                objectError.getDefaultMessage()
        );
    }
}
