package pl.lodz.zzpj.kanbanboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.zzpj.kanbanboard.entity.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TasksRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByUuid(UUID uuid);
    List<Task> findAllByCreator_Email(String email);
    List<Task> findAllByAssignee_Email(String email);
}
