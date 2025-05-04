package zzp2025.todo_app.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private Long ownerId;
}
