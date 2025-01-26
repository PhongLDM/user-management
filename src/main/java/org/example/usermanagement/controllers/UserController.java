package org.example.usermanagement.controllers;

import org.example.usermanagement.models.User;
import org.example.usermanagement.repository.RoleRepository;
import org.example.usermanagement.repository.UserRepository;
import org.example.usermanagement.security.jwt.JwtUtils;
import org.example.usermanagement.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/usermanage")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @GetMapping("/listUser")
    public ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") String token){
        String jwt = token.substring(7);
        String userName = jwtUtils.getUserNameFromJwtToken(jwt);

        if(jwtUtils.validateJwtToken(jwt)){
            List<User> users = userService.getAllUser();
            return ResponseEntity.ok(users);
        }else {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        return userService.getUserById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("User not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        try{
            userService.deleteUser(id);
            return ResponseEntity.ok("Delete successfully");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestParam String phoneNumber) {
        try {
            return ResponseEntity.ok(userService.editUser(id, phoneNumber));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id){
        try{
            User user = userService.ToggleUserActiveStatus(id);
            return ResponseEntity.ok("User is "+(user.isActive() ? "activated" : "deactivated"));
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
