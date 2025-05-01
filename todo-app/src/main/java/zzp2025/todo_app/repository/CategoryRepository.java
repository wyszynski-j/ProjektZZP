package zzp2025.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zzp2025.todo_app.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Znajdź kategorię po nazwie
    Optional<Category> findByName(String name);

    // Znajdź wszystkie kategorie dla użytkownika
    List<Category> findByOwnerId(Long userId);
}
