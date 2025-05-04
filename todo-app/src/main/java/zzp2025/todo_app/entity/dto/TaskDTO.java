package zzp2025.todo_app.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zzp2025.todo_app.entity.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private String title;
    private String description;
    private TaskStatus status;
    private Long categoryId;
}