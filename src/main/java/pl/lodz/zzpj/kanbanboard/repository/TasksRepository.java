package pl.lodz.zzpj.kanbanboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.zzpj.kanbanboard.entity.Task;

@Repository
public interface TasksRepository extends JpaRepository<Task, Long> {
}
