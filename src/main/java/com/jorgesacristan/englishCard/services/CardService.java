package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.dtos.CreateCardDto;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.request.CreateCardRequest;
import com.jorgesacristan.englishCard.response.StandardResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface CardService{
    void save(CreateCardRequest cardRequest) throws BaseException;
    ResponseEntity<Card> updateCard(Long id, CreateCardDto createCardDto, User userLoged) throws BaseException;
    public List<Card> findAll();
    ResponseEntity<?> getCardsByIdDeck (Long idDeck, User userLoged) throws BaseException;
    ResponseEntity<?> getCardById(Long id, User userLoged) throws BaseException;
    ResponseEntity<?> deleteCardById (Long id, User userLoged) throws BaseException;
    ResponseEntity<List<Card>> getCardsByIdDeckToStudy(Long idDeck, User userLoged) throws BaseException;
    ResponseEntity<?> saveCardResonseYes(Long id) throws BaseException;
    ResponseEntity<?> saveCardResonseNo(Long id) throws BaseException;
    StandardResponse sendSaveCard (CreateCardRequest newCard) throws Exception;

    Optional<Card> findCardById (Long id);

    StandardResponse addCard(CreateCardRequest cardRequest, User user) throws BaseException;

    List<Card> findCardPendingStudy (Deck deck) throws BaseException;
}
