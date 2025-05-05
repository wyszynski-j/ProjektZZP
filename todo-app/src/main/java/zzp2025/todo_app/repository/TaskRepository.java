package zzp2025.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zzp2025.todo_app.entity.Category;
import zzp2025.todo_app.entity.Status;
import zzp2025.todo_app.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner_Username(String username);

    List<Task> findByOwner_UsernameAndStatus(String username, Status status);

    List<Task> findByOwner_UsernameAndCategory(String username, Category category);

    Optional<Task> findByIdAndOwner_Username(Long id, String username);

    boolean existsByStatus(Status status);
}