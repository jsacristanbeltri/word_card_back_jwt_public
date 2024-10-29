package com.jorgesacristan.englishCard.dtos;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserDto {
    private String username;
    private String avatar;
    private Set<String> roles;
    private String email;
}
