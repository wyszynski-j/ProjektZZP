package zzp2025.todo_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zzp2025.todo_app.entity.Category;
import zzp2025.todo_app.entity.User;
import zzp2025.todo_app.entity.dto.CategoryDTO;
import zzp2025.todo_app.repository.CategoryRepository;
import zzp2025.todo_app.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public Category createCategory(CategoryDTO categoryDTO) {
        // Znajdź użytkownika po ID
        Optional<User> userOpt = userRepository.findById(categoryDTO.getOwnerId());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User owner = userOpt.get();

        // Tworzymy nową kategorię
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setOwner(owner);

        return categoryRepository.save(category);
    }

    public List<Category> getCategoriesByUser(Long userId) {
        return categoryRepository.findByOwnerId(userId);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
