package pl.lodz.zzpj.kanbanboard.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review extends Base{

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    private User reviewer;

    @Column
    private String comment;

    @NotNull
    @Column(nullable = false, updatable = false)
    private boolean rejected;

    public Review() {
    }

    public Review(UUID uuid, Instant createdAt ,User reviewer, String comment, boolean rejected) {
        super(uuid, createdAt);
        this.reviewer = reviewer;
        this.comment = comment;
        this.rejected = rejected;
    }

    public User getReviewer() {
        return reviewer;
    }

    public String getComment() {
        return comment;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }
}
