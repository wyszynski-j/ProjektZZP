package zzp2025.todo_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zzp2025.todo_app.entity.User;
import zzp2025.todo_app.entity.dto.UserDTO;
import zzp2025.todo_app.entity.dto.UserResponseDTO;
import zzp2025.todo_app.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserDTO request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        User savedUser = userService.save(user);

        UserResponseDTO responseDTO = new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername()
        );

        return ResponseEntity.ok(responseDTO);
    }
}