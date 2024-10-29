package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.controller.UserController;
import com.jorgesacristan.englishCard.dtos.CreateUserDTO;
import com.jorgesacristan.englishCard.dtos.UserInUpdateDto;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.exceptions.NewUserWithDifferentPasswordsException;
import com.jorgesacristan.englishCard.exceptions.UserNotFoundException;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.repositories.ConfirmationTokenRepository;
import com.jorgesacristan.englishCard.repositories.UserRepository;
import com.jorgesacristan.englishCard.security.models.ConfirmationToken;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService{

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder){
       this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    EmailService emailService;

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @SneakyThrows
    @Override
    public Optional<User> findUserById(Long id){
        return this.userRepository.findById(id);
    }

    @Autowired
    Configuration configuration;

    @Override
    public User createUser(CreateUserDTO newUser) throws BaseException{

        Optional<User> userDb = this.userRepository.findByEmail(newUser.getEmail());
        if(userDb.isPresent())
            throw new BaseException("The email: " + newUser.getEmail() + "is already in use");

        User user = User.builder()
                .username(newUser.getUsername())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .email(newUser.getEmail())
                .avatar(newUser.getAvatar())
                .roles(Stream.of(UserRole.USER).collect(Collectors.toSet()))
                .level(0)
                .experience(0)
                .gems(0)
                .logStreak(0)
                .isEnabled(false)
                .build();
        try {

            this.userRepository.save(user);
            ConfirmationToken confirmationToken = getConfirmationToken(user);


            String text = "To confirm your account, please click here : "
                    +"http://"+ configuration.getUrlAws() +":80/check-token/token/"+confirmationToken.getConfirmationToken()+"/register/1";
            emailService.sendEmail(user.getEmail(),"Complete Registration!",text);

            System.out.println("Confirmation Token: " + confirmationToken.getConfirmationToken());

            return user;
        }
        catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The username already exist");
        }

    }

    @Override
    public User updateUser(User userToUpdate, UserInUpdateDto userRequest) throws Exception {
        User userResponse = null;
        try {
            userToUpdate.setUsername(userRequest.getUsername());
            userToUpdate.setAvatar(userRequest.getAvatar());
            userToUpdate.setEmail(userRequest.getEmail());
            userToUpdate.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            userToUpdate.setLevel(userRequest.getLevel());
            userToUpdate.setExperience(userRequest.getExperience());
            userToUpdate.setLogStreak(userRequest.getLogStreak());
            userToUpdate.setGems(userRequest.getGems());
            userResponse = userRepository.save(userToUpdate);
        }
        catch (Exception e){
            throw e;
        }

        return userResponse;
    }


    @Override
    public void deleteUser(Long id, User userLoged) throws BaseException,Exception{
        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            Optional<User> userToUpdate = userRepository.findById(id);

            if(!userToUpdate.isPresent())
                throw new BaseException("User not found",HttpStatus.NO_CONTENT.toString());
            if(!userLoged.getRoles().contains(UserRole.ADMIN)){
                if(!userLoged.getUsername().equals(userToUpdate.get().getUsername())){
                    throw new BaseException("You have not permission to delete the user with id " + id ,HttpStatus.UNAUTHORIZED.toString());
                }
            }

        }catch (BaseException e) {
            throw e;
        }
        catch (Exception e){
            throw e;
        }

        this.userRepository.deleteById(id);


    }

    public User findByUsernamePassword(String username, String password){
        try{
            List<User> usuarios = userRepository.findAll();
            for(int i=0;i<usuarios.size();i++){
                log.info(usuarios.get(i).getUsername());
                if(usuarios.get(i).getUsername().equals(username) &&
                        usuarios.get(i).getPassword().equals(password))
                    return usuarios.get(i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

       return null;
    }



    /*@Override
    public User findByUsername(String username) {
        List<User> usuarios = super.findAll();
        for(int i=0;i<usuarios.size();i++){
            log.info(usuarios.get(i).getUsername());
            if(usuarios.get(i).getUsername().equals(username))
                return usuarios.get(i);
        }

        return null;
    }*/

    @Override
    public Optional<User> findByUsername(String username){
        return this.userRepository.findByUsername(username);
    }




    @Override
    public Optional<User> findByEmail(String email) throws Exception
    {
        Optional<User> user = null;
        try{
            user=userRepository.findByEmail(email);
        }catch (Exception e){
            throw e;
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User addUserExperience(int quantity, User userLoged) throws BaseException,Exception{
        Optional<User> user = null;
        User userResponse = null;

        int experience = 0;
        try{
            user=userRepository.findByUsername(userLoged.getUsername());
            if(user.isPresent()){
                if(addExperience (user.get(),quantity))
                    userResponse=userRepository.save(user.get());
            }else
                throw new BaseException("User not found");
        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }

        return userResponse;
    }


    @Override
    public User addUserGems (int quantity, User userLoged) throws BaseException,Exception {
        Optional<User> user = null;
        User userResponse = null;

        try{
            user=userRepository.findByUsername(userLoged.getUsername());
            if(user.isPresent()){
                user.get().setGems(user.get().getGems()+quantity);
                userResponse=userRepository.save(user.get());
            }else
                throw new BaseException("User not found");
        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }

        return userResponse;
    }

    @Override
    public void addLogStreak (User userLoged) throws BaseException,Exception {
        Optional<User> user = null;
        try{
            user=userRepository.findByUsername(userLoged.getUsername());
            if(user.isPresent()){
                user.get().setLogStreak(user.get().getLogStreak()+1);
                addExperience(user.get(),calculateLogStreakExperience(user.get().getLogStreak()));
                userRepository.save(user.get());
            }else
                throw new BaseException("User not found");
        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }
    }

    private Boolean addExperience(User user, int quantity) {
        int experienceTemp = 0;
        int levelUp = 0;

        if(user.getLevel()<Configuration.LEVEL_MAX){
            experienceTemp = user.getExperience() + quantity;
            if(experienceTemp>Configuration.EXPERIENCE_MAX){
                while (experienceTemp-Configuration.EXPERIENCE_MAX>0){
                    experienceTemp = experienceTemp - Configuration.EXPERIENCE_MAX;
                    levelUp++;
                }
                user.setLevel(user.getLevel()+levelUp);
                if(user.getLevel()>Configuration.LEVEL_MAX)
                    user.setLevel(Configuration.LEVEL_MAX);

                user.setExperience(experienceTemp);

            }else if(experienceTemp==Configuration.EXPERIENCE_MAX){
                user.setLevel(user.getLevel()+1);
                if(user.getLevel()>Configuration.LEVEL_MAX)
                    user.setLevel(Configuration.LEVEL_MAX);
                user.setExperience(0);
            }else{
                user.setExperience(experienceTemp);
            }

            return true;
        }else
            return false;
    }

    private int calculateLogStreakExperience (int actualLogStreak){
        if(actualLogStreak==1)
            return 10;
        else if (actualLogStreak==2)
            return 20;
        else if (actualLogStreak==3)
            return 30;
        else if (actualLogStreak==4)
            return 40;
        else if (actualLogStreak==5)
            return 50;
        else if (actualLogStreak==6)
            return 60;
        else if (actualLogStreak==7)
            return 70;
        else if (actualLogStreak==8)
            return 80;
        else if (actualLogStreak==9)
            return 90;
        else
            return 100;
    }

    @Override
    public void resetLogStreak (User userLoged) throws BaseException,Exception {
        Optional<User> user = null;
        try{
            user=userRepository.findByUsername(userLoged.getUsername());
            if(user.isPresent()){
                user.get().setLogStreak(0);
                userRepository.save(user.get());
            }else
                throw new BaseException("User not found");
        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }
    }



    @Override
    public ResponseEntity<?> sendMailChangePassword(String email) throws Exception,BaseException{
        Optional<User> user = null;
        ConfirmationToken confirmationToken;
        try {
            user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                confirmationToken = getConfirmationToken(user.get());
                String text = "To confirm your account, please click here : "
                        +"http://"+configuration.getUrlAws() +":80/new-password/token/"+confirmationToken.getConfirmationToken();
//                String text = "If you have requested a password change on your word flash card account, please use the following link to confirm it:   : "
//                        + "http://localhost:8080/api/v1/users/confirm-change-password?token=" + confirmationToken.getConfirmationToken();
                emailService.sendEmail(user.get().getEmail(), "Confirm password change", text);
            } else
                throw new BaseException(String.format("Email user not found"), HttpStatus.NOT_FOUND.toString());
        }catch (BaseException e){
            log.error(e.getMessage());
            throw e;
        }catch (Exception e){
            log.error(e.getMessage());
            throw e;
        }
        return ResponseEntity.ok().body(confirmationToken);
    }

    @Override
    public ResponseEntity<?> confirmEmail(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null)
        {
            Optional<User> user = userRepository.findByUsername(token.getUser().getUsername());
            user.get().setEnabled(true);
            userRepository.save(user.get());
            return ResponseEntity.ok("Email verified successfully!");
        }
        return ResponseEntity.badRequest().body("Error: Couldn't verify email");
    }

    @Override
    public User savePassword(String username, String password, String confirmationToken) throws Exception,BaseException{
        User userResponse;
        try {
            ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

            if(token != null)
            {
                Optional<User> user = userRepository.findByUsername(username);
                if (user.isPresent()) {
                    user.get().setPassword(passwordEncoder.encode(password));
                    user.get().setEnabled(true);
                    userResponse = userRepository.save(user.get());
                } else
                    throw new BaseException(String.format("Email user not found"), HttpStatus.NOT_FOUND.toString());
            }else
                throw new BaseException("Error: Couldn't verify account", HttpStatus.UNAUTHORIZED.toString());


        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }

        return userResponse;
    }

    private ConfirmationToken getConfirmationToken(User user){
        List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findByUserEmail(user.getEmail());
        if(CollectionUtils.isEmpty(confirmationTokens)){
            ConfirmationToken confirmationToken = new ConfirmationToken(user);
            confirmationTokenRepository.save(confirmationToken);
            confirmationTokens.add(confirmationToken);
        }
        return confirmationTokens.get(0);
    }





/*
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User usr = userRepository.findByUsername(username);
        UserDetails userDetails = new UserDetails(usr.getUsername(),usr.getPassword(),roles) {
        };
    }
    */

}
