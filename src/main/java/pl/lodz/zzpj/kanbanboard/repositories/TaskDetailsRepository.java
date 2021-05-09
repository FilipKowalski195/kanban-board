package pl.lodz.zzpj.kanbanboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.zzpj.kanbanboard.entity.TaskDetails;

@Repository
public interface TaskDetailsRepository extends JpaRepository<TaskDetails, Long>  {
}
