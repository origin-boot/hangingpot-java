package com.origin.hangingpot.port;

import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

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

@Data
class PageCommand {

    private String searchText;


    private Integer page;

    private Integer size;
}