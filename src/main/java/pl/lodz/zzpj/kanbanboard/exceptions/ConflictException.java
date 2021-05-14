package pl.lodz.zzpj.kanbanboard.exceptions;

import pl.lodz.zzpj.kanbanboard.entity.Review;
import pl.lodz.zzpj.kanbanboard.entity.Task;

public class ConflictException extends BaseException {
    private static final String UNIQUE_CONFLICT_MSG = "%s could not be processed due to not unique %s = %s";

    private static final String INVALID_STATUS_CONFLICT_MSG = "Task (uuid: %s) cannot be closed because it is %s";

    private static final String UNSATISFIED_REVIEW_CONFLICT_MSG = "Task (uuid: %s) status cannot be DONE because last review (uuid: %s) reject it";

    private static final String ADD_REVIEW_CONFLICT_MSG = "Review cannot be added to Task (uuid: %s) because its status is not TO_REVIEW";

    private ConflictException(String message) {
        super(message);
    }

    public static ConflictException uniqueField(Class<?> cls, String fieldName, Object value) {
        return new ConflictException(String.format(
                UNIQUE_CONFLICT_MSG,
                cls.getSimpleName(),
                fieldName,
                value
        ));
    }

    public static ConflictException invalidTaskStatus(Task task) {
        return new ConflictException(String.format(INVALID_STATUS_CONFLICT_MSG, task.getUuid(), task.getStatus()));
    }

    public static ConflictException unsatisfiedReview(Task task, Review review) {
        return new ConflictException(String.format(UNSATISFIED_REVIEW_CONFLICT_MSG, task.getUuid(), review.getId()));
    }

    public static ConflictException cannotAddReview(Task task){
        return new ConflictException(String.format(ADD_REVIEW_CONFLICT_MSG, task.getUuid()));
    }
}
