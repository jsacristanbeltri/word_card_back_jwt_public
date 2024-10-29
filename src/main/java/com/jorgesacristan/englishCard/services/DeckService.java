package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.request.CreateDeckRequest;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.Language;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.response.StandardResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface DeckService{
    Optional<Deck> findDeck(Long id) throws BaseException;
    public List<Deck> findAllDecks();
    void saveDeck(CreateDeckRequest deckRequest) throws BaseException;
    ResponseEntity<?> deleteDeck(Long id, User userLoged) throws BaseException;
    ResponseEntity<?> updateDeck(Long id, CreateDeckRequest deckRequest, User userLoged);
    List<Deck> findDecksByUsernameLenguage(User user, Language language);
    public List<Deck> findByLanguage (Language lenguage);
    List<Deck> findDecksByUsername(User username);
    //public void decrementTotalCardsOfDeck (Long id) throws BaseException,Exception;
    //public void incrementTotalCardsOfDeck (Long id) throws BaseException,Exception;
    ResponseEntity<?> getDeckById (Long id, User userLoged) throws BaseException;
    StandardResponse sendSaveDeck (CreateDeckRequest newDeck, User userLoged);
    List<String> findAllLanguage() throws Exception;
    List<String> findLanguagesByUser(User userLoged) throws Exception;

    ResponseEntity<?> getAllDecksByUser(User user) throws BaseException;

    ResponseEntity<?> getAllDecksByUsernameLenguage (User user, String languageRequest) throws BaseException;


    ResponseEntity getAllLanguageByUser(User userLoged) throws BaseException;
}
