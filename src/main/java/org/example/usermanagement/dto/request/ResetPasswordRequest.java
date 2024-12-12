package org.example.usermanagement.dto.request;

import jakarta.validation.constraints.*;

public class ResetPasswordRequest {
    @NotBlank(message = "Email is required and cannot be blank.")
    @Size(max = 50, message = "Email must not exceed 50 characters.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotBlank(message = "Password is required and cannot be blank.")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters.")
    private String newPassword;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
