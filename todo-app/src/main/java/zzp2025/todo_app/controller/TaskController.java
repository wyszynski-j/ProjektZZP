package zzp2025.todo_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zzp2025.todo_app.entity.Task;
import zzp2025.todo_app.entity.dto.TaskDTO;
import zzp2025.todo_app.entity.dto.TaskResponseDTO;
import zzp2025.todo_app.service.TaskService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @RequestBody TaskDTO taskDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Task createdTask = taskService.createTask(
                taskDTO.getTitle(),
                taskDTO.getDescription(),
                taskDTO.getStatusId(),
                taskDTO.getCategoryId(),
                username
        );

        TaskResponseDTO responseDTO = mapToResponseDTO(createdTask);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(
            @RequestParam(required = false) Long statusId,
            @RequestParam(required = false) Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        List<Task> tasks;

        if (statusId != null) {
            tasks = taskService.getTasksByUsernameAndStatus(username, statusId);
        } else if (categoryId != null) {
            tasks = taskService.getTasksByUsernameAndCategory(username, categoryId);
        } else {
            tasks = taskService.getTasksByUsername(username);
        }

        List<TaskResponseDTO> responseDTO = tasks.stream()
                .map(this::mapToResponseDTO)
                .toList();

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Optional<Task> task = taskService.getTaskByIdAndUsername(id, username);

        return task
                .map(t -> ResponseEntity.ok(mapToResponseDTO(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @RequestBody TaskDTO taskDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Task updatedTask = taskService.updateTask(
                id,
                taskDTO.getTitle(),
                taskDTO.getDescription(),
                taskDTO.getStatusId(),
                taskDTO.getCategoryId(),
                username
        );

        TaskResponseDTO responseDTO = mapToResponseDTO(updatedTask);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        boolean deleted = taskService.deleteTask(id, username);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private TaskResponseDTO mapToResponseDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus() != null ? task.getStatus().getId() : null,
                task.getStatus() != null ? task.getStatus().getTitle() : null,
                task.getCategory() != null ? task.getCategory().getId() : null,
                task.getCategory() != null ? task.getCategory().getName() : null,
                task.getOwner().getId()
        );
    }
}