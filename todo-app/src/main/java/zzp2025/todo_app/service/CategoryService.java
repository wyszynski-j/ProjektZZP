package zzp2025.todo_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zzp2025.todo_app.entity.Category;
import zzp2025.todo_app.entity.User;
import zzp2025.todo_app.repository.CategoryRepository;
import zzp2025.todo_app.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public Category createCategory(String name, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean categoryExists = categoryRepository.existsByNameAndOwner(name, owner);

        if (categoryExists) {
            throw new RuntimeException("Category with this name already exists for the user");
        }
        Category category = new Category();
        category.setName(name);
        category.setOwner(owner);

        return categoryRepository.save(category);
    }



    public Optional<Category> getCategoryByIdAndUsername(Long id, String username) {
        return categoryRepository.findByIdAndOwner_Username(id, username);
    }

    public List<Category> getCategoriesByUsername(String username) {
        return categoryRepository.findByOwner_Username(username);
    }
    public boolean deleteCategory(Long id, String username) {
        Optional<Category> category = categoryRepository.findByIdAndOwner_Username(id, username);

        if (category.isPresent()) {
            categoryRepository.delete(category.get());
            return true;
        } else {
            return false;
        }
    }

    public Category updateCategory(Long id, String name, String username) {
        Category category = categoryRepository.findByIdAndOwner_Username(id, username)
                .orElseThrow(() -> new RuntimeException("Category not found or not owned by the user"));

        category.setName(name);
        return categoryRepository.save(category);
    }
}
