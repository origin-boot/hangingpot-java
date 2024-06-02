package com.origin.hangingpot.port;

import java.util.List;

import com.origin.hangingpot.domain.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.Data;

@Data
class LoginCommand {
    @NotBlank(message = "username is required")
    @Length(min = 4, max = 20, message = "username length must be between 4 and 20")
    private String username;

    @NotBlank(message = "password is required")
    @Length(min = 4, max = 20, message = "password length must be between 4 and 20")
    private String password;
}