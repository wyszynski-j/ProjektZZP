package zzp2025.todo_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zzp2025.todo_app.entity.Category;
import zzp2025.todo_app.entity.Task;
import zzp2025.todo_app.entity.TaskStatus;
import zzp2025.todo_app.entity.User;
import zzp2025.todo_app.repository.CategoryRepository;
import zzp2025.todo_app.repository.TaskRepository;
import zzp2025.todo_app.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Task createTask(String title, String description, TaskStatus status, Long categoryId, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findByIdAndOwner_Username(categoryId, username)
                    .orElseThrow(() -> new RuntimeException("Category not found or not owned by the user"));
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status != null ? status : TaskStatus.NEW);
        task.setCategory(category);
        task.setOwner(owner);

        return taskRepository.save(task);
    }

    public List<Task> getTasksByUsername(String username) {
        return taskRepository.findByOwner_Username(username);
    }

    public List<Task> getTasksByUsernameAndStatus(String username, TaskStatus status) {
        return taskRepository.findByOwner_UsernameAndStatus(username, status);
    }

    public List<Task> getTasksByUsernameAndCategory(String username, Long categoryId) {
        Optional<Category> category = categoryRepository.findByIdAndOwner_Username(categoryId, username);
        return category.map(value -> taskRepository.findByOwner_UsernameAndCategory(username, value))
                .orElseThrow(() -> new RuntimeException("Category not found or not owned by the user"));
    }

    public Optional<Task> getTaskByIdAndUsername(Long id, String username) {
        return taskRepository.findByIdAndOwner_Username(id, username);
    }

    public Task updateTask(Long id, String title, String description, TaskStatus status, Long categoryId, String username) {
        Task task = taskRepository.findByIdAndOwner_Username(id, username)
                .orElseThrow(() -> new RuntimeException("Task not found or not owned by the user"));

        if (title != null) {
            task.setTitle(title);
        }

        if (description != null) {
            task.setDescription(description);
        }

        if (status != null) {
            task.setStatus(status);
        }

        if (categoryId != null) {
            Category category = categoryRepository.findByIdAndOwner_Username(categoryId, username)
                    .orElseThrow(() -> new RuntimeException("Category not found or not owned by the user"));
            task.setCategory(category);
        }

        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Long id, TaskStatus status, String username) {
        Task task = taskRepository.findByIdAndOwner_Username(id, username)
                .orElseThrow(() -> new RuntimeException("Task not found or not owned by the user"));

        task.setStatus(status);
        return taskRepository.save(task);
    }

    public boolean deleteTask(Long id, String username) {
        Optional<Task> task = taskRepository.findByIdAndOwner_Username(id, username);

        if (task.isPresent()) {
            taskRepository.delete(task.get());
            return true;
        } else {
            return false;
        }
    }
}