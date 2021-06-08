package pl.lodz.zzpj.kanbanboard.entity;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TaskDetails {

    @Id
    @GeneratedValue
    private Long id;

    // LOW ~1h, MEDIUM ~4h, HIGH ~8h
    public enum Difficulty {
        LOW, MEDIUM, HIGH
    }

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column
    private String description;

    @FutureOrPresent
    @NotNull
    @Column(nullable = false)
    private Instant deadLine;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Review> reviews;

    public TaskDetails() {
    }

    public TaskDetails(
            String name,
            String description,
            Instant deadLine,
            Difficulty difficulty
    ) {
        this.name = name;
        this.description = description;
        this.deadLine = deadLine;
        this.difficulty = difficulty;
        this.reviews = new ArrayList<>();
    }

    public TaskDetails(
            String name,
            String description,
            Instant deadLine,
            Difficulty difficulty,
            List<Review> reviews
    ) {
        this.name = name;
        this.description = description;
        this.deadLine = deadLine;
        this.difficulty = difficulty;
        this.reviews = reviews;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Instant deadLine) {
        this.deadLine = deadLine;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}
