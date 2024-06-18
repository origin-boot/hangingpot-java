package com.origin.hangingpot.port;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
class PageCommand {


    private String searchText;
    private String startTime;
    private String endTime;


    @Min(0)
    private Integer page;

    @Min(1)
    private Integer size;

    public String getSearchText() {
        if (searchText == null) {
            return null;
        } else if (searchText.isEmpty()) {
            return null;
        } else {
            return "%"+searchText+"%";
        }

    }

}

@Data
@Builder
class PageInfo {
    private Long total;
}
