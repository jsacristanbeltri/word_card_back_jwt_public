package com.jorgesacristan.englishCard.security.models;

import java.util.Set;


import com.jorgesacristan.englishCard.dtos.GetUserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtUserResponse extends GetUserDto {

    private String token;

    @Builder(builderMethodName="jwtUserResponseBuilder") //Para que no haya colosion entre los Builder
    public JwtUserResponse(String username, String avatar, Set<String> roles, String token, String email) {
        super(username, avatar, roles, email);
        this.token = token;
    }



}
