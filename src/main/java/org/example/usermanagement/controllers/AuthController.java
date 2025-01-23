package org.example.usermanagement.controllers;

import jakarta.validation.Valid;

import org.example.usermanagement.models.ERole;
import org.example.usermanagement.models.Role;
import org.example.usermanagement.models.User;
import org.example.usermanagement.dto.request.LoginRequest;
import org.example.usermanagement.dto.request.ResetPasswordRequest;
import org.example.usermanagement.dto.request.SignupRequest;
import org.example.usermanagement.dto.response.JwtResponse;
import org.example.usermanagement.dto.response.MessageResponse;
import org.example.usermanagement.repository.RoleRepository;
import org.example.usermanagement.repository.UserRepository;
import org.example.usermanagement.security.jwt.JwtUtils;
import org.example.usermanagement.security.services.UserDetailsImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;




@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // Xác thực người dùng
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Lưu thông tin xác thực
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tạo JWT token cho người dùng sau khi xác thực thành công.
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Lấy thông tin chi tiết của người dùng từ đối tượng authentication.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Kiểm tra trạng thái hoạt động của người dùng.
        if (!userDetails.isActive()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403 Forbidden
                    .body(new MessageResponse("You are blocked from signing in."));
        }

        // Lấy danh sách vai trò của người dùng.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Trả về token JWT và thông tin người dùng
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getFullname(),
                userDetails.getGender(),
                userDetails.getPhonenumber(),
                userDetails.isActive(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Kiểm tra xem tên đăng nhập đã tồn tại chưa.
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Kiểm tra xem email đã tồn tại chưa.
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Tạo tài khoản người dùng mới.
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFullname(),
                signUpRequest.getGender(),
                signUpRequest.getPhonenumber(),
                true
        );

        // Xác định Role cho người dùng.
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            // Nếu không có vai trò được chỉ định, mặc định là ROLE_USER.
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            // Lặp qua danh sách vai trò được gửi từ client.
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        // Gán vai trò cho người dùng
        user.setRoles(roles);
        userRepository.save(user);

        // Trả về thông báo đăng ký thành công.
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        // Tìm người dùng theo email.
        User user = userRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: User with given email does not exist."));

        // Mã hóa và cập nhật mật khẩu mới.
        String encodedPassword = encoder.encode(resetPasswordRequest.getNewPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        // Trả về thông báo đặt lại mật khẩu thành công.
        return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));
    }
}
