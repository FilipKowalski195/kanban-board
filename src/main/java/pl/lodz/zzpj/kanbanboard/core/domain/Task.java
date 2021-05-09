package pl.lodz.zzpj.kanbanboard.core.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
public class Task extends Base {

    enum Status {
        TODO, IN_PROGRESS, TO_REVIEW, DONE, CANCELED
    }

    private final User creator;

    private Status status;

    private User assignee;

    @Setter(AccessLevel.PRIVATE)
    private Instant closedAt;

    private TaskDetails details;

    public Task(UUID uuid, Instant createdAt, User creator, TaskDetails details) {
        super(uuid, createdAt);
        this.creator = creator;
        this.status = Status.TODO;
        this.details = details;
    }

    public Task(
            UUID uuid,
            Instant createdAt,
            User creator,
            Status status,
            Instant closedAt,
            TaskDetails details
    ) {
        super(uuid, createdAt);
        this.creator = creator;
        this.status = status;
        this.closedAt = closedAt;
        this.details = details;
    }
}
