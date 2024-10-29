package com.jorgesacristan.englishCard.dtos;

import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.models.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserDtoConverter {
    public GetUserDto convertUserEntityToGetUserDto(User user){
        return GetUserDto.builder()
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .roles(user.getRoles().stream()
                        .map(UserRole::name)
                        .collect(Collectors.toSet())
                )
                .build();
    }
}
