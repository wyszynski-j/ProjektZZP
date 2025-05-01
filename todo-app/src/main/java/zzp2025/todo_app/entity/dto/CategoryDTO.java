package zzp2025.todo_app.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {

    private String name;
    private Long ownerId;  // ID użytkownika, który jest właścicielem kategorii
}