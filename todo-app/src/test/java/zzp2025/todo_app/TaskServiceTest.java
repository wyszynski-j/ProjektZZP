package zzp2025.todo_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zzp2025.todo_app.entity.*;
import zzp2025.todo_app.repository.CategoryRepository;
import zzp2025.todo_app.repository.StatusRepository;
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
    private StatusRepository statusRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        statusRepository = mock(StatusRepository.class);
        taskService = new TaskService(taskRepository, userRepository, categoryRepository, statusRepository);
    }

    @Test
    void createTask_shouldCreateTaskWithoutCategory() {
        String username = "user";
        User user = new User();
        user.setUsername(username);
        Status status = new Status();
        status.setId(1L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task task = taskService.createTask("Test", "Desc", 1L, null, username);

        assertEquals("Test", task.getTitle());
        assertEquals("Desc", task.getDescription());
        assertEquals(status, task.getStatus());
        assertEquals(user, task.getOwner());
        assertNull(task.getCategory());
    }

    @Test
    void createTask_shouldThrow_whenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.createTask("Test", "Desc", 1L, null, "ghost"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void createTask_shouldThrow_whenStatusNotFound() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));
        when(statusRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.createTask("Test", "Desc", 99L, null, "user"));

        assertEquals("Status not found", ex.getMessage());
    }

    @Test
    void createTask_shouldThrow_whenCategoryNotFound() {
        User user = new User();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(statusRepository.findById(1L)).thenReturn(Optional.of(new Status()));
        when(categoryRepository.findByIdAndOwner_Username(10L, "user")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.createTask("Test", "Desc", 1L, 10L, "user"));

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
    void getTasksByUsernameAndStatus_shouldReturnTasks() {
        Status status = new Status();
        List<Task> tasks = List.of(new Task());
        when(statusRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(status));
        when(taskRepository.findByOwner_UsernameAndStatus("user", status)).thenReturn(tasks);

        List<Task> result = taskService.getTasksByUsernameAndStatus("user", 1L);

        assertEquals(1, result.size());
    }

    @Test
    void getTasksByUsernameAndStatus_shouldThrow_whenStatusNotFound() {
        when(statusRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.getTasksByUsernameAndStatus("user", 1L));

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
        Status status = new Status();

        when(statusRepository.findById(2L)).thenReturn(Optional.of(status));
        when(categoryRepository.findByIdAndOwner_Username(3L, "user")).thenReturn(Optional.of(category));

        Task updated = taskService.updateTask(1L, "New", "Updated", 2L, 3L, "user");

        assertEquals("New", updated.getTitle());
        assertEquals("Updated", updated.getDescription());
        assertEquals(status, updated.getStatus());
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
    void updateTask_shouldThrow_whenStatusNotFound() {
        Task task = new Task();
        when(taskRepository.findByIdAndOwner_Username(1L, "user")).thenReturn(Optional.of(task));
        when(statusRepository.findById(5L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                taskService.updateTask(1L, null, null, 5L, null, "user"));

        assertEquals("Status not found", ex.getMessage());
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
