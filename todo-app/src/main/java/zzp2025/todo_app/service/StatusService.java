package zzp2025.todo_app.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zzp2025.todo_app.entity.Status;
import zzp2025.todo_app.entity.User;
import zzp2025.todo_app.repository.StatusRepository;
import zzp2025.todo_app.repository.TaskRepository;
import zzp2025.todo_app.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Optional<Status> getStatusByIdAndUsername(Long id, String username) {
        return statusRepository.findByIdAndOwner_Username(id, username);
    }

    public List<Status> getStatusByUsername(String username) {
        return statusRepository.findByOwner_Username(username);
    }

    public Status createStatus(String title, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean statusExists = statusRepository.existsByTitleAndOwner(title, owner);

        if (statusExists) {
            throw new RuntimeException("Status with this name already exists for the user");
        }
        Status status = new Status();
        status.setTitle(title);
        status.setOwner(owner);

        return statusRepository.save(status);
    }

    @Transactional
    public Status updateStatus(Long statusId, String newTitle, User user) {
        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono statusu."));

        if (!status.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("Nie możesz edytować cudzych statusów.");
        }

        if (statusRepository.existsByTitleAndOwner(newTitle, user)) {
            throw new IllegalArgumentException("Status o takiej nazwie już istnieje.");
        }

        status.setTitle(newTitle);
        return statusRepository.save(status);
    }

    public Status updateStatus(Long id, String title, String username) {
        Status status = statusRepository.findByIdAndOwner_Username(id, username)
                .orElseThrow(() -> new RuntimeException("Status not found or not owned by the user"));

        status.setTitle(title);
        return statusRepository.save(status);
    }

    public boolean deleteStatus(Long statusId, String username) {
        Optional<Status> status = statusRepository.findByIdAndOwner_Username(statusId, username);

        if (status.isPresent()) {
            boolean isUsed = taskRepository.existsByStatus(status.get());
            if (isUsed) {
                throw new IllegalStateException("Nie można usunąć statusu, ponieważ jest powiązany z zadaniami.");
            }

            statusRepository.delete(status.get());
            return true;
        } else {
            return false;
        }
    }
}
