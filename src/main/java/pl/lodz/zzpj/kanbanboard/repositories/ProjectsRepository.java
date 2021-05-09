package pl.lodz.zzpj.kanbanboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.zzpj.kanbanboard.entity.Project;

@Repository
public interface ProjectsRepository extends JpaRepository<Project, Long> {
}
