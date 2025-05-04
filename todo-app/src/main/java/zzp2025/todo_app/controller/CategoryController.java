package zzp2025.todo_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zzp2025.todo_app.entity.Category;
import zzp2025.todo_app.entity.dto.CategoryDTO;
import zzp2025.todo_app.entity.dto.CategoryResponseDTO;
import zzp2025.todo_app.service.CategoryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @RequestBody CategoryDTO categoryDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Category createdCategory = categoryService.createCategory(categoryDTO.getName(), username);

        CategoryResponseDTO responseDTO = new CategoryResponseDTO(
                createdCategory.getId(),
                createdCategory.getName(),
                createdCategory.getOwner().getId()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        Optional<Category> category = categoryService.getCategoryByIdAndUsername(id, username);
        return category
                .map(cat -> ResponseEntity.ok(new CategoryResponseDTO(
                        cat.getId(),
                        cat.getName(),
                        cat.getOwner().getId()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<Category> categories = categoryService.getCategoriesByUsername(username);

        List<CategoryResponseDTO> categoryDTOs = categories.stream()
                .map(cat -> new CategoryResponseDTO(
                        cat.getId(),
                        cat.getName(),
                        cat.getOwner().getId()
                ))
                .toList();

        return ResponseEntity.ok(categoryDTOs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        boolean deleted = categoryService.deleteCategory(id, username);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDTO categoryDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Category updatedCategory = categoryService.updateCategory(id, categoryDTO.getName(), username);

        CategoryResponseDTO responseDTO = new CategoryResponseDTO(
                updatedCategory.getId(),
                updatedCategory.getName(),
                updatedCategory.getOwner().getId()
        );

        return ResponseEntity.ok(responseDTO);
    }
}
