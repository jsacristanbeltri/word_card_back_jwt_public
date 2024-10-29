package com.jorgesacristan.englishCard.controller;

import com.jorgesacristan.englishCard.request.CreateDeckRequest;
import com.jorgesacristan.englishCard.dtos.DeckOutDto;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.Language;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.response.StandardResponse;
import com.jorgesacristan.englishCard.services.DeckService;
import com.jorgesacristan.englishCard.services.LanguageService;
import io.swagger.models.Response;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jorgesacristan.englishCard.configuration.*;

import javax.validation.Valid;

/**
 * Controller for managing operations related to decks.
 */
@RestController
@RequestMapping(Configuration.API_V1_PREFIX + "/decks")
@CrossOrigin(origins = "*")
public class DeckController{

    private static Logger log = LoggerFactory.getLogger(DeckController.class);

    @Autowired
    private DeckService deckService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Retrieve all decks by user
     * @param userLoged
     * @return
     * @throws Exception
     */
    @GetMapping()
    public ResponseEntity<?> getAllDecksByUser (@AuthenticationPrincipal User userLoged) throws Exception{
        log.info("IN getAllDecksByUser,  decks of user: " + userLoged.getUsername());
        ResponseEntity<?> responseEntity = deckService.getAllDecksByUser(userLoged);
        log.info("OUT getAllDecksByUser, decks of user: " + userLoged.getUsername() + "decks: " + responseEntity.getBody());
        return responseEntity;

    }

    /**
     * Retrieve all language of the decks of a specific user.
     * @param userLoged
     * @param languageRequest
     * @return
     * @throws Exception
     */
    @GetMapping("/language/{languageRequest}")
    public ResponseEntity<?> getAllDecksByUsernameLenguage(
            @AuthenticationPrincipal User userLoged ,
            @PathVariable(value="languageRequest") String languageRequest)throws Exception {

        log.info("IN getAllDecksByLenguage, language: "+ languageRequest);
        ResponseEntity<?> responseEntity = deckService.getAllDecksByUsernameLenguage(userLoged,languageRequest);
        log.info("OUT getAllDecksByLenguage, language: "+ languageRequest + ", result: " + responseEntity.getBody());
        return responseEntity;
    }

    /**
     * Retrieve deck by id
     * @param userLoged
     * @param id
     * @return
     * @throws BaseException
     * @throws Exception
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDeckById (@AuthenticationPrincipal User userLoged, final @PathVariable Long id) throws BaseException,Exception{
        log.info("IN DECKS findById id: " +  id);
        ResponseEntity<?> responseEntity = deckService.getDeckById(id, userLoged);
        log.info("OUT DECKS findById id: " +  id);
        return responseEntity;

    }

    /**
     * Retrieve the languages by user
     * @param userLoged
     * @return
     * @throws Exception
     */
    @GetMapping("/languages")
    public ResponseEntity<?> getAllLanguageByUser (@AuthenticationPrincipal User userLoged) throws Exception{
        log.info("IN DECKS getAllLanguageByUser language of user: " + userLoged.getUsername());
        ResponseEntity responseEntity = deckService.getAllLanguageByUser (userLoged);
        log.info(String.format("OUT DECKS getAllLanguageByUser  language of user: %s , result: %s",userLoged.getUsername(), responseEntity.getBody()));
        return responseEntity;
    }

    /**
     * Add deck
     * @param deckRequest
     * @param userLoged
     * @return
     * @throws Exception
     */
    @PostMapping()
    public StandardResponse addDeck (
            @RequestBody @Valid CreateDeckRequest deckRequest,
            @AuthenticationPrincipal User userLoged) throws Exception{

        log.info("IN DECKS, addDeck, deckRequest: " + deckRequest);
        deckRequest.setUsername(userLoged.getUsername());
        StandardResponse response = deckService.sendSaveDeck(deckRequest, userLoged);
        log.info("OUT DECKS addDeck, response: " + response);
        return response;
    }


    /**
     * Update deck
     * @param userLoged
     * @param id
     * @param deckRequest
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal User userLoged,
                       final @PathVariable Long id,
                       final @RequestBody @Valid CreateDeckRequest deckRequest) throws Exception{

        log.info(String.format("IN DECK update id: %s, deckRequest: %s", String.valueOf(id), deckRequest));
        ResponseEntity responseEntity = deckService.updateDeck(id,deckRequest,userLoged);
        log.info("OUT DECK update id: "+ id);
        return responseEntity;
    }

    /**
     * Delete deck
     * @param userLoged
     * @param id
     * @return
     * @throws Exception
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Boolean> deleteDeck (@AuthenticationPrincipal User userLoged,
                                         final @PathVariable Long id) throws Exception {

        log.info("IN DECK delete id: " + id);
        ResponseEntity responseEntity = deckService.deleteDeck(id, userLoged);
        log.info("OUT DECK delete id: " + id);
        return responseEntity;


    }

}
