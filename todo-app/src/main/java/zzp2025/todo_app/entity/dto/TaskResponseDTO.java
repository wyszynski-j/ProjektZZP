package zzp2025.todo_app.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Long statusId;
    private String statusTitle;
    private Long categoryId;
    private String categoryName;
    private Long userId;
}