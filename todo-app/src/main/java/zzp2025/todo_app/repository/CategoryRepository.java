package zzp2025.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zzp2025.todo_app.entity.Category;
import zzp2025.todo_app.entity.User;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndOwner_Username(Long id, String username);
    List<Category> findByOwner_Username(String username);
    boolean existsByNameAndOwner(String name, User owner);


}
