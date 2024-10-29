package com.jorgesacristan.englishCard.controller;

import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.CreateCardDto;
import com.jorgesacristan.englishCard.exceptions.*;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.request.CreateCardRequest;
import com.jorgesacristan.englishCard.response.StandardResponse;
import com.jorgesacristan.englishCard.services.CardService;
import com.jorgesacristan.englishCard.services.DeckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller for managing operations related to cards.
 */

@RestController
@RequestMapping(Configuration.API_V1_PREFIX + "/cards")
@CrossOrigin(origins = "*")
public class CardController{

    private static Logger log = LoggerFactory.getLogger(CardController.class);

    @Autowired
    CardService cardService;

    @Autowired
    DeckService deckService;


    /**
     * Retrieves a card by its ID.
     * @param id
     * @param user
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCardById (@PathVariable Long id, @AuthenticationPrincipal User user) throws Exception{
        log.info("IN CARD findById id: " + id);
        ResponseEntity<?> responseEntity = cardService.getCardById(id, user);
        log.info("OUT CARD, getCardById, card: " + responseEntity.getBody());
        return responseEntity;
    }

    /**
     * Retrieves a card by its ID DECK.
     * @param idDeck
     * @param user
     * @return
     * @throws Exception
     */
    @GetMapping("/deck/{idDeck}")
    public ResponseEntity<?> getCardsByIdDeck(@PathVariable Long idDeck, @AuthenticationPrincipal User user) throws Exception{
        log.info("IN CARD getCardsByIdDeck idDeck: " + idDeck);
        ResponseEntity responseEntity = cardService.getCardsByIdDeck ( idDeck, user);
        log.info("OUT CARD getCardsByIdDeck, cards response: " + responseEntity.getBody());
        return responseEntity;

    }

    /**
     * Retrieves the cards pending to study.
     * @param idDeck
     * @param user
     * @return
     * @throws Exception
     */
    @GetMapping("/pending/deck/{idDeck}")
    public ResponseEntity<?> getCardsByIdDeckToStudy(@PathVariable Long idDeck,
                                                              @AuthenticationPrincipal User user) throws Exception{
        log.info("IN CARD getCardsByIdDeckToStudy idDeck: " + idDeck);
        ResponseEntity responseEntity = cardService.getCardsByIdDeckToStudy(idDeck, user);
        log.info("OUT CARD getCardsByIdDeckToStudy, Cards response: " + idDeck);
        return responseEntity;


    }

    /**
     * Add card to deck.
     * @param cardRequest
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/deck")
    public StandardResponse addCard (@RequestBody @Valid CreateCardRequest cardRequest,
                                        @AuthenticationPrincipal User user) throws Exception{
        log.info("IN CARD, addCard cardName: " + cardRequest.getName1());
        StandardResponse standardResponse = cardService.addCard(cardRequest,user);
        log.info("OUT CARD, addCard, card added: " + standardResponse);
        return standardResponse;
    }

    /**
     * Update card
     * @param userLoged
     * @param id
     * @param cardRequest
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(@AuthenticationPrincipal User userLoged,
                                       final @PathVariable Long id,
                                       final @RequestBody @Valid CreateCardDto cardRequest) throws Exception{
        log.info("IN CARD updateCard id: " + id);
        ResponseEntity<?> responseEntity = cardService.updateCard(id, cardRequest, userLoged);
        log.info("OUT CARD updateCard, card updated: " + responseEntity.getBody());
        return responseEntity;
    }

    /**
     * Delete card
     * @param id
     * @param userLoged
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard (@PathVariable Long id, @AuthenticationPrincipal User userLoged) throws Exception{
        log.info("IN CARD deleteCard id: " + id);
        ResponseEntity<?> responseEntity = this.cardService.deleteCardById(id, userLoged);
        log.info("OUT CARD, card deleted: " + responseEntity.getBody());
        return responseEntity;
    }

    /**
     * Update reminder day of card by id
     * @param id
     * @return
     * @throws BaseException
     */
    @GetMapping("/responseCardYes/{id}")
    public ResponseEntity saveCardResponseYes (@PathVariable Long id) throws BaseException{
        log.info("IN CARD saveCardResponseYes id: " + id);
        ResponseEntity responseEntity = this.cardService.saveCardResonseYes(id);
        log.info("OUT CARD saveCardResponseYes id: " + id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Update reminder day of card by id
     * @param id
     * @return
     * @throws BaseException
     */
    @GetMapping("/responseCardNo/{id}")
    public ResponseEntity saveCardResponseNo (@PathVariable Long id) throws BaseException{
        log.info("IN CARD saveCardResponseNo id: " +  id);
        ResponseEntity responseEntity = this.cardService.saveCardResonseNo(id);
        log.info("OUT CARD saveCardResponseNo id: ", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
