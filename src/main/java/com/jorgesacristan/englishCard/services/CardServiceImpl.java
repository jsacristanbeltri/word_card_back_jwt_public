package com.jorgesacristan.englishCard.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.CreateCardDto;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.exceptions.BaseExceptionEnum;
import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.rabbit.MessageSender;
import com.jorgesacristan.englishCard.repositories.CardRepository;
import com.jorgesacristan.englishCard.repositories.DeckRepository;
import com.jorgesacristan.englishCard.request.CreateCardRequest;
import com.jorgesacristan.englishCard.response.StandardResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService{

    private static Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DeckServiceImpl deckService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private DeckRepository deckRepository;

    @Override
    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    @Override
    public void save(CreateCardRequest cardRequest) throws BaseException {
        try{
            Optional<Deck> deck = deckService.findDeck(cardRequest.getIdDeck());
            if(deck.isPresent()){
                Card card = new Card(cardRequest.getName1(),cardRequest.getName2(),true,null,0,deck.get());
                cardRepository.save(card);
            }
        }catch (Exception e){
            throw new BaseException("Error saving card", HttpStatus.CREATED.toString());
        }
    }

    @Override
    public ResponseEntity<Card> updateCard(Long id, CreateCardDto createCardDto, User userLoged) throws BaseException{
        Optional<Card> card;
        Card cardSaved = null;
        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            card = this.findCardById(id);
            if(card.isPresent()){
                if(!userLoged.getRoles().contains(UserRole.ADMIN)) {
                    if(!card.get().getDeck().getUser().getUsername().equals(userLoged.getUsername()))
                        throw new BaseException("You don't have permissions to update the card with id: " + id, HttpStatus.UNAUTHORIZED.toString());
                }
            }else
                throw new BaseException(String.format("Card with id %s not found ", id), HttpStatus.NOT_FOUND.toString());

            card.get().setId(id);
            card.get().setEnable(createCardDto.getEnable());
            card.get().setName1(createCardDto.getName1());
            card.get().setName2(createCardDto.getName2());
            cardRepository.save(card.get());

            cardSaved = cardRepository.save(card.get());

        }catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }
        return ResponseEntity.ok().body(cardSaved);
    }

    @Override
    public ResponseEntity<?> getCardsByIdDeck (Long idDeck, User userLoged) throws BaseException{
        List <Card> cards = null;
        Optional<Deck> deck = null;
        try {
            deck = deckRepository.findById(idDeck);
            if(deck.isPresent()){
                if (!userLoged.getRoles().contains(UserRole.ADMIN)) {
                    if (!deck.get().getUser().getUsername().equals(userLoged.getUsername()))
                        throw new BaseException("You don't have permissions to access to the deck with id: " + idDeck, HttpStatus.UNAUTHORIZED.toString());
                    else{
                        if (deck.get().getCards().isEmpty())
                            throw new BaseException(String.format("The deck with id %s have not cards",idDeck), HttpStatus.NOT_FOUND.toString());
                    }
                }
            }else
                throw new BaseException(String.format("Deck with id %s not found ", idDeck), HttpStatus.NOT_FOUND.toString());



        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(deck.get().getCards());

    }

    @Override
    public Optional<Card> findCardById (Long id) {
        return cardRepository.findById(id);
    }

    @Override
    public StandardResponse addCard(CreateCardRequest cardRequest, User user) throws BaseException {
        Card card = null;
        StandardResponse response = null;

        try{
            Optional<Deck> deck = deckService.findDeck(cardRequest.getIdDeck());
            if(deck.isPresent()){
                if (!user.getRoles().contains(UserRole.ADMIN)) {
                    if(!deck.get().getUser().getUsername().equals(user.getUsername()))
                        throw new BaseException("You don't have permissions to create inside that deck " + cardRequest.toString(), HttpStatus.UNAUTHORIZED.toString());
                }
            }else
                throw new BaseException(String.format("Deck with id %s not found ", cardRequest.getIdDeck()), HttpStatus.NOT_FOUND.toString());


                card = new Card(cardRequest.getName1(),cardRequest.getName2(),true,null,0,deck.get());
                //deck.get().getCards().add(card);
                //deckRepository.save(deck.get());
                cardRepository.save(card);


            //response = this.sendSaveCard(cardRequest);

        }
        catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return response;
    }

    @Override
    public ResponseEntity<?> getCardById(Long id, User userLoged) throws BaseException{
        Optional<Card> card=null;
        try{
            card = this.findCardById(id);
            if (card.isPresent()) {
                if (!userLoged.getRoles().contains(UserRole.ADMIN)) {
                    if (!card.get().getDeck().getUser().getUsername().equals(userLoged.getUsername()))
                        throw new BaseException("You don't have permissions to access to the card with id: " + id, HttpStatus.UNAUTHORIZED.toString());
                }
            } else
                throw new BaseException(String.format("Card with id %s not found ", id), HttpStatus.NOT_FOUND.toString());
        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity.ok().body(card.get());

    }


    @Override
    public ResponseEntity<?> deleteCardById (Long id, User userLoged) throws BaseException {
        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            Optional<Card> card = this.findCardById(id);
            if(card.isPresent()){
                if (!userLoged.getRoles().contains(UserRole.ADMIN)) {
                    if(!card.get().getDeck().getUser().getUsername().equals(userLoged.getUsername()))
                        throw new BaseException("You don't have permissions to update the card with id: " + id, HttpStatus.UNAUTHORIZED.toString());
                }
            }else
                throw new BaseException(String.format("Card with id %s not found ", id), HttpStatus.NOT_FOUND.toString());

             cardRepository.deleteById(card.get().getId());

        }catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity.ok().body(null);
    }

    @Override
    public ResponseEntity<List<Card>> getCardsByIdDeckToStudy(Long idDeck, User userLoged) throws BaseException{
        List <Card> cards = new ArrayList<>();
        try {
            Optional<Deck> deck = deckService.findDeck(idDeck);
            if (deck.isPresent()) {
                if (!userLoged.getRoles().contains(UserRole.ADMIN)) {
                    if (!deck.get().getUser().getUsername().equals(userLoged.getUsername()))
                        throw new BaseException("You don't have permissions to access to the deck with id: " + idDeck, HttpStatus.UNAUTHORIZED.toString());
                }
            } else
                throw new BaseException(String.format("Deck with id %s not found ", idDeck), HttpStatus.NOT_FOUND.toString());

            cards = this.findCardPendingStudy(deck.get());
        }
        catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(cards);




    }

    @Override
    public List<Card> findCardPendingStudy (Deck deck) throws BaseException {
        List<Card> cardsToStudy = new ArrayList<>();
        try{
            Instant today =  Instant.now();
            Long lastTrySecond;
            List<Card> cards = deck.getCards();
            for (Card card: cards){
                Long timeFromLastStudy = 0L;

                //segundos desde 1970
                Long todaySecond = today.getEpochSecond();

                if(card.getLastTry() == null)
                    lastTrySecond = todaySecond;
                else
                    lastTrySecond = card.getLastTry().getEpochSecond();

                //dias transcurridos desde la ultima vez q se estudio la tarjeta.
                timeFromLastStudy = TimeUnit.SECONDS.toDays(todaySecond-lastTrySecond);
                // TimeUnit.MILLISECONDS.toDays(today.getEpochSecond() - card.getLastTry().getEpochSecond());
                if(timeFromLastStudy >= card.getPeriodDaysReminder() )
                    cardsToStudy.add(card);

            }

        }catch (Exception e){
            throw new BaseException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        return cardsToStudy;
    }

    @Override
    public ResponseEntity<?> saveCardResonseYes(Long id) throws BaseException{
        try{
            Optional<Card> card = cardRepository.findById(id);

            if(card.isPresent()){
                //Integer actualPeriodDays = card.get().getPeriodDaysReminder();
                card.get().setLastTry(Instant.now());

                if(card.get().getPeriodDaysReminder()!=6) {
                    if(card.get().getPeriodDaysReminder()==0)
                        card.get().setPeriodDaysReminder(1);
                    else if (card.get().getPeriodDaysReminder()==1)
                        card.get().setPeriodDaysReminder(3);
                    else if (card.get().getPeriodDaysReminder()==3)
                        card.get().setPeriodDaysReminder(7);
                    else if (card.get().getPeriodDaysReminder()==7)
                        card.get().setPeriodDaysReminder(14);
                    else if (card.get().getPeriodDaysReminder()==14)
                        card.get().setPeriodDaysReminder(30);
                    else if (card.get().getPeriodDaysReminder()==30)
                        card.get().setPeriodDaysReminder(90);
                    else if (card.get().getPeriodDaysReminder()==90)
                        card.get().setEnable(false);
                }

                this.cardRepository.save(card.get());
            }else
                throw new BaseException(String.format("Card with id %s not found", id), HttpStatus.NOT_FOUND.toString());

        }
        catch (Exception e){
            throw new BaseException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return ResponseEntity.ok().body(null);
    }

    @Override
    public ResponseEntity<?> saveCardResonseNo(Long id) throws BaseException{
        try{
            Optional<Card> card = cardRepository.findById(id);
            if(card.isPresent()){
                /*
                card.get().setLastTry(Instant.now());

                if(card.get().getPeriodDaysReminder()==90)
                    card.get().setPeriodDaysReminder(30);
                else if (card.get().getPeriodDaysReminder()==30)
                    card.get().setPeriodDaysReminder(14);
                else if (card.get().getPeriodDaysReminder()==14)
                    card.get().setPeriodDaysReminder(7);
                else if (card.get().getPeriodDaysReminder()==7)
                    card.get().setPeriodDaysReminder(3);
                else if (card.get().getPeriodDaysReminder()==3)
                    card.get().setPeriodDaysReminder(1);
                else if (card.get().getPeriodDaysReminder()==1)
                    card.get().setPeriodDaysReminder(0);
                    */
                card.get().setPeriodDaysReminder(0);

                this.cardRepository.save(card.get());
            }else
                throw new BaseException("Cards not found", HttpStatus.NOT_FOUND.toString());

        }catch (Exception e){
            throw new BaseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return ResponseEntity.ok().body(null);
    }

    @Override
    public StandardResponse sendSaveCard (CreateCardRequest cardRequest) throws Exception{
        StandardResponse standardResponse;
        Message message = null;
        try{
            message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(cardRequest))
                    .build();
            messageSender.sendSaveCardToQueue(message);
        }catch (Exception e){
            return new StandardResponse("ko",e.getMessage(),Instant.now(),message.getMessageProperties().getHeader(Configuration.CORRELATION_ID_HEADER_NAME));
        }
        return new StandardResponse("ok","ok",Instant.now(),message.getMessageProperties().getHeader(Configuration.CORRELATION_ID_HEADER_NAME));

    }

}
