package com.jorgesacristan.englishCard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.GetUserDto;
import com.jorgesacristan.englishCard.dtos.UserDtoConverter;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.rabbit.MessageListener;
import com.jorgesacristan.englishCard.rabbit.MessageSender;
import com.jorgesacristan.englishCard.security.JwtTokenProvider;
import com.jorgesacristan.englishCard.security.models.JwtUserResponse;
import com.jorgesacristan.englishCard.security.models.LoginRequest;
import com.jorgesacristan.englishCard.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * Controller for managing operations related to authetication user.
 */
@RestController
@RequestMapping(Configuration.API_V1_PREFIX + "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDtoConverter userDtoConverter;

    @Autowired
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * SB 1. login
     * @param loginRequest
     * @return
     * @throws JsonProcessingException
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JwtUserResponse> login (@RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        log.info("IN AUTH login username: " + loginRequest.getUsername());
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword()
                        ));

        //Añadimos el usuario logeado en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertUserEntityAndTokenToJwtUserResponse(user,jwtToken));

    }

    /**
     * SB 2. get user permisions
     * @param user
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/me")
    public GetUserDto me(@AuthenticationPrincipal User user){
        return userDtoConverter.convertUserEntityToGetUserDto(user);
    }

    private JwtUserResponse convertUserEntityAndTokenToJwtUserResponse(User user, String jwtToken) {
        return JwtUserResponse
                .jwtUserResponseBuilder()
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .roles(user.getRoles().stream().map(UserRole::name).collect(Collectors.toSet()))
                .token(jwtToken)
                .build();
    }


}


