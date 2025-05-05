package zzp2025.todo_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zzp2025.todo_app.entity.Status;
import zzp2025.todo_app.entity.dto.StatusDTO;
import zzp2025.todo_app.entity.dto.StatusResponseDTO;
import zzp2025.todo_app.service.StatusService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/statuses")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;
    @PostMapping
    public ResponseEntity<StatusResponseDTO> createStatus(
            @RequestBody StatusDTO statusDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Status createdStatus = statusService.createStatus(statusDTO.getTitle(), username);

        StatusResponseDTO responseDTO = new StatusResponseDTO(
                createdStatus.getId(),
                createdStatus.getTitle(),
                createdStatus.getOwner().getId()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusResponseDTO> getStatusById(@PathVariable Long id,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        Optional<Status> status = statusService.getStatusByIdAndUsername(id, username);
        return status
                .map(cat -> ResponseEntity.ok(new StatusResponseDTO(
                        cat.getId(),
                        cat.getTitle(),
                        cat.getOwner().getId()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<StatusResponseDTO>> getStatusesByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<Status> statuses = statusService.getStatusByUsername(username);

        List<StatusResponseDTO> statusDTOs = statuses.stream()
                .map(cat -> new StatusResponseDTO(
                        cat.getId(),
                        cat.getTitle(),
                        cat.getOwner().getId()
                ))
                .toList();

        return ResponseEntity.ok(statusDTOs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        boolean deleted = statusService.deleteStatus(id, username);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusDTO statusDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        Status updatedStatus = statusService.updateStatus(id, statusDTO.getTitle(), username);

        StatusResponseDTO responseDTO = new StatusResponseDTO(
                updatedStatus.getId(),
                updatedStatus.getTitle(),
                updatedStatus.getOwner().getId()
        );

        return ResponseEntity.ok(responseDTO);
    }
}
