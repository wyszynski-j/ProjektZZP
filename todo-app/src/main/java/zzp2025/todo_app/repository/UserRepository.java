package zzp2025.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zzp2025.todo_app.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
