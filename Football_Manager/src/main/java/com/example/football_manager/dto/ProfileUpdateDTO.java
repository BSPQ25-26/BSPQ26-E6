package com.example.football_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDTO {

    @NotBlank(message = "Username is required")
    @Size(max = 30, message = "Username must be at most 30 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String currentPassword;

    @Pattern(regexp = "^$|.{8,}$", message = "New password must be at least 8 characters")
    private String newPassword;

    private String confirmNewPassword;

    @AssertTrue(message = "New passwords do not match")
    public boolean isPasswordConfirmationValid() {
        if ((newPassword == null || newPassword.isBlank()) && (confirmNewPassword == null || confirmNewPassword.isBlank())) {
            return true;
        }

        if (newPassword == null || confirmNewPassword == null) {
            return false;
        }

        return newPassword.equals(confirmNewPassword);
    }
}




