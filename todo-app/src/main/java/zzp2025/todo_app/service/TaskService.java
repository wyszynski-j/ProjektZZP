package zzp2025.todo_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zzp2025.todo_app.entity.*;
import zzp2025.todo_app.repository.CategoryRepository;
import zzp2025.todo_app.repository.StatusRepository;
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
    private final StatusRepository statusRepository;

    public Task createTask(String title, String description, Long statusId, Long categoryId, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findByIdAndOwner_Username(categoryId, username)
                    .orElseThrow(() -> new RuntimeException("Category not found or not owned by the user"));
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setCategory(category);
        task.setOwner(owner);

        return taskRepository.save(task);
    }

    public List<Task> getTasksByUsername(String username) {
        return taskRepository.findByOwner_Username(username);
    }

    public List<Task> getTasksByUsernameAndCategory(String username, Long categoryId) {
        Optional<Category> category = categoryRepository.findByIdAndOwner_Username(categoryId, username);
        return category.map(value -> taskRepository.findByOwner_UsernameAndCategory(username, value))
                .orElseThrow(() -> new RuntimeException("Category not found or not owned by the user"));
    }

    public List<Task> getTasksByUsernameAndStatus(String username, Long statusId) {
        Optional<Status> status = statusRepository.findByIdAndOwner_Username(statusId, username);
        return status.map(value -> taskRepository.findByOwner_UsernameAndStatus(username, value))
                .orElseThrow(() -> new RuntimeException("Category not found or not owned by the user"));
    }

    public Optional<Task> getTaskByIdAndUsername(Long id, String username) {
        return taskRepository.findByIdAndOwner_Username(id, username);
    }

    public Task updateTask(Long id, String title, String description, Long statusId, Long categoryId, String username) {
        Task task = taskRepository.findByIdAndOwner_Username(id, username)
                .orElseThrow(() -> new RuntimeException("Task not found or not owned by the user"));

        if (title != null) {
            task.setTitle(title);
        }

        if (description != null) {
            task.setDescription(description);
        }

        if (statusId != null) {
            Status status = statusRepository.findById(statusId)
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            task.setStatus(status);
        }

        if (categoryId != null) {
            Category category = categoryRepository.findByIdAndOwner_Username(categoryId, username)
                    .orElseThrow(() -> new RuntimeException("Category not found or not owned by the user"));
            task.setCategory(category);
        }

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