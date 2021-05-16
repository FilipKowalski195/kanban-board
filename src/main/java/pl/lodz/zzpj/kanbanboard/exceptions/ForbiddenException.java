package pl.lodz.zzpj.kanbanboard.exceptions;

import pl.lodz.zzpj.kanbanboard.entity.User;

public class ForbiddenException extends BaseException {

    private static final String NOT_PROJECT_MEMBER_MSG = "User(mail: %s) is not a member of this project!";

    private ForbiddenException(String message) {
        super(message);
    }

    public static ForbiddenException notProjectMember(User user) {
        return new ForbiddenException(String.format(NOT_PROJECT_MEMBER_MSG, user.getEmail()));
    }
}
