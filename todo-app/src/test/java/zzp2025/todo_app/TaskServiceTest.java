package zzp2025.todo_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zzp2025.todo_app.entity.*;
import zzp2025.todo_app.repository.CategoryRepository;
import zzp2025.todo_app.repository.TaskRepository;
import zzp2025.todo_app.repository.UserRepository;
import zzp2025.todo_app.service.TaskService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        taskService = new TaskService(taskRepository, userRepository, categoryRepository);
    }

    @Test
    void createTask_shouldCreateTaskWithoutCategory() {
        String username = "user";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task task = taskService.createTask("Test", "Desc", TaskStatus.NEW, null, username);

        assertEquals("Test", task.getTitle());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(user, task.getOwner());
        assertNull(task.getCategory());
    }

    @Test
    void createTask_shouldThrow_whenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.createTask("Test", "Desc", TaskStatus.NEW, null, "ghost"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void createTask_shouldThrow_whenCategoryNotFound() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.createTask("Test", "Desc", TaskStatus.NEW, 1L, "user"));

        assertEquals("Category not found or not owned by the user", ex.getMessage());
    }

    @Test
    void getTasksByUsername_shouldReturnTasks() {
        List<Task> tasks = List.of(new Task(), new Task());
        when(taskRepository.findByOwner_Username("user")).thenReturn(tasks);

        List<Task> result = taskService.getTasksByUsername("user");

        assertEquals(2, result.size());
    }

    @Test
    void getTasksByUsernameAndStatus_shouldReturnTasks() {
        List<Task> tasks = List.of(new Task());
        when(taskRepository.findByOwner_UsernameAndStatus("user", TaskStatus.NEW)).thenReturn(tasks);

        List<Task> result = taskService.getTasksByUsernameAndStatus("user", TaskStatus.NEW);

        assertEquals(1, result.size());
    }

    @Test
    void getTasksByUsernameAndCategory_shouldReturnTasks() {
        Category category = new Category();
        List<Task> tasks = List.of(new Task());
        when(categoryRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(category));
        when(taskRepository.findByOwner_UsernameAndCategory("user", category)).thenReturn(tasks);

        List<Task> result = taskService.getTasksByUsernameAndCategory("user", 1L);

        assertEquals(1, result.size());
    }

    @Test
    void getTasksByUsernameAndCategory_shouldThrow_whenCategoryNotFound() {
        when(categoryRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.getTasksByUsernameAndCategory("user", 1L));

        assertEquals("Category not found or not owned by the user", ex.getMessage());
    }

    @Test
    void getTaskByIdAndUsername_shouldReturnTaskIfExists() {
        Task task = new Task();
        when(taskRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getTaskByIdAndUsername(1L, "user");

        assertTrue(result.isPresent());
    }

    @Test
    void updateTask_shouldUpdateFieldsCorrectly() {
        Task task = new Task();
        when(taskRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category category = new Category();
        when(categoryRepository.findByIdAndOwner_Username(2L, "user")).thenReturn(Optional.of(category));

        Task updated = taskService.updateTask(1L, "New", "Updated", TaskStatus.COMPLETED, 2L, "user");

        assertEquals("New", updated.getTitle());
        assertEquals("Updated", updated.getDescription());
        assertEquals(TaskStatus.COMPLETED, updated.getStatus());
        assertEquals(category, updated.getCategory());
    }

    @Test
    void updateTask_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.updateTask(1L, "title", null, null, null, "user"));

        assertEquals("Task not found or not owned by the user", ex.getMessage());
    }

    @Test
    void updateTask_shouldThrow_whenCategoryNotFound() {
        Task task = new Task();
        when(taskRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(task));
        when(categoryRepository.findByIdAndOwner_Username(9L, "user")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.updateTask(1L, null, null, null, 9L, "user"));

        assertEquals("Category not found or not owned by the user", ex.getMessage());
    }

    @Test
    void updateTaskStatus_shouldUpdateStatus() {
        Task task = new Task();
        when(taskRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTaskStatus(1L, TaskStatus.COMPLETED, "user");

        assertEquals(TaskStatus.COMPLETED, result.getStatus());
    }

    @Test
    void updateTaskStatus_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.updateTaskStatus(1L, TaskStatus.COMPLETED, "user"));

        assertEquals("Task not found or not owned by the user", ex.getMessage());
    }

    @Test
    void deleteTask_shouldDelete_whenExists() {
        Task task = new Task();
        when(taskRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(task));

        boolean result = taskService.deleteTask(1L, "user");

        assertTrue(result);
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_shouldReturnFalse_whenNotExists() {
        when(taskRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.empty());

        boolean result = taskService.deleteTask(1L, "user");

        assertFalse(result);
        verify(taskRepository, never()).delete(any());
    }
}
