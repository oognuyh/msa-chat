package com.oognuyh.userservice.payload.request;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {
    
    @NotBlank
    private String newPassword;

    private String passwordConfirmation;

    @AssertTrue
    public Boolean isPasswordConfirmation() {
        return newPassword != null &&
            passwordConfirmation != null &&
            newPassword.equals(passwordConfirmation);
    }
}
