package zzp2025.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zzp2025.todo_app.entity.Status;
import zzp2025.todo_app.entity.User;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    boolean existsByTitleAndOwner(String title, User owner);
    Optional<Status> findByIdAndOwner_Username(Long id, String username);
    List<Status> findByOwner_Username(String username);
}
