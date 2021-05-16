package pl.lodz.zzpj.kanbanboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.zzpj.kanbanboard.entity.Project;
import pl.lodz.zzpj.kanbanboard.entity.Task;
import pl.lodz.zzpj.kanbanboard.entity.User;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectsRepository extends JpaRepository<Project, Long> {
    Optional<Project> findProjectByUuid(UUID uuid);
    Optional<Project> findProjectByUuidAndMembersContains(UUID uuid, User member);
    Optional<Project> findProjectByTasksContains(Task task);
}
