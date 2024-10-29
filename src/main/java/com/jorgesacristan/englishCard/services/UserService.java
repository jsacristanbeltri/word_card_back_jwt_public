package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.dtos.CreateUserDTO;
import com.jorgesacristan.englishCard.dtos.UserInUpdateDto;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserService{
    List<User> findAllUser();
    Optional<User> findUserById(Long id);
    User createUser(CreateUserDTO newUser) throws BaseException;
    User updateUser(User userToUpdate, UserInUpdateDto userRequest) throws Exception;
    void deleteUser(Long id, User user) throws BaseException,Exception;
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email) throws Exception;
    Optional<User> findById(Long id);
    User addUserExperience(int quantity, User userLoged) throws BaseException,Exception;
    User addUserGems (int quantity, User userLoged) throws BaseException,Exception;
    void addLogStreak (User userLoged) throws BaseException,Exception;
    void resetLogStreak (User userLoged) throws BaseException,Exception;
    ResponseEntity<?> confirmEmail(String confirmationToken);

    ResponseEntity<?> sendMailChangePassword(String email) throws Exception,BaseException;

    User savePassword(String email, String password, String token) throws Exception,BaseException;
}
