package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.validations.ValidPassword;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserDTO implements Serializable {

    @NotNull(message = "username is mandatory, It can't be null")
    @NotBlank(message = "username is mandatory, It can't be empty")
    private String username;

    @ValidPassword
    private String password;

    @Email
    private String email;

    private String avatar;


}
