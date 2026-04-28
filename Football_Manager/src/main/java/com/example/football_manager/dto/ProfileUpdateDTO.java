package com.example.football_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDTO {

    @NotBlank(message = "Username is required")
    @Size(max = 30, message = "Username must be at most 30 characters")
    @Schema(description = "New username", example = "john_doe")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "New email address", example = "john_doe@example.com")
    private String email;

    @Schema(description = "Current password (required when changing password)", example = "OldP@ssw0rd")
    private String currentPassword;

    @Pattern(regexp = "^$|.{8,}$", message = "New password must be at least 8 characters")
    @Schema(description = "New password (min 8 characters)", example = "NewP@ssw0rd123")
    private String newPassword;

    @Schema(description = "Confirmation of new password", example = "NewP@ssw0rd123")
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




