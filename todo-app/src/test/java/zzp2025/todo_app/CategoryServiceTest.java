package zzp2025.todo_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zzp2025.todo_app.entity.Category;
import zzp2025.todo_app.entity.User;
import zzp2025.todo_app.repository.CategoryRepository;
import zzp2025.todo_app.repository.UserRepository;
import zzp2025.todo_app.service.CategoryService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        userRepository = mock(UserRepository.class);
        categoryService = new CategoryService(categoryRepository, userRepository);
    }

    @Test
    void createCategory_shouldCreateCategory_whenUserExistsAndCategoryNotExists() {
        String username = "user";
        String categoryName = "Work";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByNameAndOwner(categoryName, user)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category category = categoryService.createCategory(categoryName, username);

        assertEquals(categoryName, category.getName());
        assertEquals(user, category.getOwner());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.createCategory("Test", "nonexistent"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createCategory_shouldThrowException_whenCategoryAlreadyExists() {
        String username = "User";
        String categoryName = "Work";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByNameAndOwner(categoryName, user)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.createCategory(categoryName, username));

        assertEquals("Category with this name already exists for the user", exception.getMessage());
    }

    @Test
    void getCategoryByIdAndUsername_shouldReturnCategoryIfExists() {
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryByIdAndUsername(1L, "user");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getCategoriesByUsername_shouldReturnListOfCategories() {
        List<Category> categories = List.of(new Category(), new Category());

        when(categoryRepository.findByOwner_Username("user")).thenReturn(categories);

        List<Category> result = categoryService.getCategoriesByUsername("user");

        assertEquals(2, result.size());
    }

    @Test
    void deleteCategory_shouldDeleteAndReturnTrue_whenCategoryExists() {
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(category));

        boolean result = categoryService.deleteCategory(1L, "user");

        assertTrue(result);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_shouldReturnFalse_whenCategoryDoesNotExist() {
        when(categoryRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.empty());

        boolean result = categoryService.deleteCategory(1L, "user");

        assertFalse(result);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void updateCategory_shouldUpdateName_whenCategoryExists() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Old Name");

        when(categoryRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category updated = categoryService.updateCategory(1L, "New Name", "user");

        assertEquals("New Name", updated.getName());
    }

    @Test
    void updateCategory_shouldThrow_whenCategoryNotFound() {
        when(categoryRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                categoryService.updateCategory(1L, "New Name", "user"));

        assertEquals("Category not found or not owned by the user", ex.getMessage());
    }
}
