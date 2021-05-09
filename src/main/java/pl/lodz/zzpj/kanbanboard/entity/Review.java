package pl.lodz.zzpj.kanbanboard.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.Instant;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    private User reviewer;

    @Column
    private String comment;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private Instant publishTime;

    @NotNull
    @Column(nullable = false)
    private boolean rejected;

    public Review() {
    }

    public Review(@NotNull User reviewer, String comment, @NotNull @PastOrPresent Instant publishTime, @NotNull boolean rejected) {
        this.reviewer = reviewer;
        this.comment = comment;
        this.publishTime = publishTime;
        this.rejected = rejected;
    }

    public User getReviewer() {
        return reviewer;
    }

    public String getComment() {
        return comment;
    }

    public Instant getPublishTime() {
        return publishTime;
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
