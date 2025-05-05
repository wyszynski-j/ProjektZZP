package zzp2025.todo_app.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusResponseDTO {
    private Long id;
    private String title;
    private Long ownerId;
}
