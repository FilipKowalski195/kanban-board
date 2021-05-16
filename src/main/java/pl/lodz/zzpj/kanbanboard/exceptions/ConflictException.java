package pl.lodz.zzpj.kanbanboard.exceptions;

import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.entity.Review;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.Task.Status;
import pl.lodz.zzpj.kanbanboard.entity.User;

public class ConflictException extends BaseException {
    private static final String UNIQUE_CONFLICT_MSG = "%s could not be processed due to not unique %s = %s";

    private static final String CLOSE_TASK_CONFLICT_MSG = "Task (uuid: %s) cannot be closed because it is %s";

    private static final String INVALID_TASK_STATUS_CONFLICT_MSG = "Cannot change status of Task (uuid: %s) to %s because it is TODO";

    private static final String UNSATISFIED_REVIEW_CONFLICT_MSG = "Task (uuid: %s) status cannot be DONE because last review (uuid: %s) reject it";

    private static final String NO_REVIEW_CONFLICT_MSG = "Task (uuid: %s) status cannot be DONE because it is not reviewed";

    private static final String ADD_REVIEW_CONFLICT_MSG = "Review cannot be added to Task (uuid: %s) because its status is not TO_REVIEW";

    private static final String NOT_MEMBER_OF_PROJECT_CONFLICT_MSG = "Creator %s of Task is not Project (uuid: %s) member";

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

    public static ConflictException cannotCloseTask(Task task) {
        return new ConflictException(String.format(CLOSE_TASK_CONFLICT_MSG, task.getUuid(), task.getStatus()));
    }

    public static ConflictException invalidTaskStatus(Task task, Status status){
        return new ConflictException(String.format(INVALID_TASK_STATUS_CONFLICT_MSG, task.getUuid(), status));
    }

    public static ConflictException unsatisfiedReview(Task task, Review review) {
        return new ConflictException(String.format(UNSATISFIED_REVIEW_CONFLICT_MSG, task.getUuid(), review.getUuid()));
    }

    public static ConflictException noReview(Task task){
        return new ConflictException(String.format(NO_REVIEW_CONFLICT_MSG, task.getUuid()));
    }

    public static ConflictException cannotAddReview(Task task){
        return new ConflictException(String.format(ADD_REVIEW_CONFLICT_MSG, task.getUuid()));
    }

    public static ConflictException notMemberOfProject(User user, Project project){
        return new ConflictException(String.format(NOT_MEMBER_OF_PROJECT_CONFLICT_MSG, user.getEmail(), project.getUuid()));
    }
}
