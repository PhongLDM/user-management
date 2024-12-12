package org.example.usermanagement.dto.request;

import java.util.Set;

import jakarta.validation.constraints.*;

public class SignupRequest {
    @NotBlank(message = "Username is required and cannot be blank.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private String username;

    @NotBlank(message = "Email is required and cannot be blank.")
    @Size(max = 50, message = "Email must not exceed 50 characters.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    private Set<String> role;

    @NotBlank(message = "Password is required and cannot be blank.")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters.")
    private String password;

    @NotBlank(message = "Full name is required and cannot be blank.")
    @Size(max = 100, message = "Full name must not exceed 100 characters.")
    private String fullname;

    @NotNull(message = "Gender is required. Please select 0 for male or 1 for female.")
    private Integer gender;

    @NotBlank(message = "Phone number is required and cannot be blank.")
    @Size(max = 10, message = "Phone number must not exceed 10 characters.")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is invalid.")
    private String phonenumber;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRole() {
        return this.role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}