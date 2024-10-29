package com.jorgesacristan.englishCard.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.DeckOutDto;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.models.Card;
import com.jorgesacristan.englishCard.request.CreateDeckRequest;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.Deck;
import com.jorgesacristan.englishCard.models.Language;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.rabbit.MessageSender;
import com.jorgesacristan.englishCard.repositories.DeckRepository;
import com.jorgesacristan.englishCard.response.StandardResponse;
import com.jorgesacristan.englishCard.utils.CorrelationIdUtils;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeckServiceImpl implements DeckService{

    private static Logger log = LoggerFactory.getLogger(DeckServiceImpl.class);

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CorrelationIdUtils correlationIdUtils;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<Deck> findDeck(Long id) throws BaseException{
        Optional<Deck> deck = null;
        try{
            deck =  deckRepository.findById(id);
        }catch (Exception e){
            throw new BaseException(e.getMessage(), HttpStatus.NOT_FOUND.toString());
        }
        return deck;
    }

    @Override
    public List<Deck> findAllDecks() {
        return deckRepository.findAll();
    }



    @Override
    @SneakyThrows
    public ResponseEntity<?> updateDeck(Long id, CreateDeckRequest deckRequest, User userLoged) {
        Deck deckResponse = null;
        Optional<Deck> deckToUpdate = null;

        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            deckToUpdate = deckRepository.findById(id);

            if(!deckToUpdate.isPresent())
                throw new BaseException(String.format("Deck with id %s not found", id), HttpStatus.NOT_FOUND.toString());

            if (!userLoged.getRoles().contains(UserRole.ADMIN) &&
                    !deckToUpdate.get().getUser().getUsername().equals(userLoged.getUsername()))
                throw new BaseException("You have not permissions to update the deck with id: " + id, HttpStatus.UNAUTHORIZED.toString());

            deckResponse = deckRepository.save(this.buildDeck(deckToUpdate.get(),deckRequest));

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(converToDeckOutDto(deckResponse));
    }

    private DeckOutDto converToDeckOutDto (Deck deck) {
        DeckOutDto deckOutDto = modelMapper.map(deck,DeckOutDto.class);
        deckOutDto.setUsername(deck.getUser().getUsername());
        return deckOutDto;
    }

    private Deck buildDeck(Deck deck, CreateDeckRequest deckRequest) throws BaseException{

        Optional<Language> newLanguage = languageService.findByLanguage(deckRequest.getLanguage());

        if(!newLanguage.isPresent())
            throw new BaseException(String.format("Language %s not found", deckRequest.getLanguage()), HttpStatus.NOT_FOUND.toString());

        deck.setName(deckRequest.getName());
        deck.setDescription(deckRequest.getDescription());
        deck.setLanguage(newLanguage.get());

        return deck;
    }


    public Deck findByDecknameUsername(String deckname, String username, String lenguage){
        List<Deck> decks = new ArrayList<>();
        try{
            decks = this.findAllDecks();
            for(Deck deck : decks){
                if(deck.getName().equals(deckname) &&
                        deck.getUser().getUsername().equals(username) &&
                        deck.getLanguage().equals(lenguage))
                    return deck;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    @Override
    public List<Deck> findDecksByUsernameLenguage(User user, Language language) {
        return deckRepository.findDeckByLanguageAndUser(language,user);
    }

    @Override
    public ResponseEntity<?> getAllDecksByUsernameLenguage (User user, String languageRequest) throws BaseException{
        List<Deck> result = new ArrayList<>();
        List<Language> languages = new ArrayList<>();
        List<DeckOutDto> decksResponse = new ArrayList<>();

        try{
            if(user==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            languages = languageService.findAll();
            Optional<Language> language = languages.stream().filter(l -> l.getLanguage().equals(languageRequest)).findFirst();

            if(!language.isPresent())
                throw new BaseException("Language not supported", HttpStatus.BAD_REQUEST.toString());

            if (user.getRoles().contains(UserRole.ADMIN))
                result = this.findByLanguage(language.get());
            else
                result = this.findDecksByUsernameLenguage(user, language.get());

            result.stream().forEach(deck -> decksResponse.add(converDeckToDeckOutDto(deck)));

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(decksResponse);
    }

    @Override
    public ResponseEntity getAllLanguageByUser(User userLoged) throws BaseException {
        List<String> languages = new ArrayList<>();
        try {
            if (userLoged == null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            languages = this.findLanguagesByUser(userLoged);

        }catch (BaseException e){
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(languages);
    }


    @Override
    public List<Deck> findByLanguage(Language language) {
        return deckRepository.findDeckByLanguage(language);
    }

    @SneakyThrows
    @Override
    public List<Deck> findDecksByUsername(User user) {
        return deckRepository.findByUser(user);
    }


    @Override
    public ResponseEntity<?> getAllDecksByUser(User user) throws BaseException{
        List<Deck> decksUser = new ArrayList<>();
        List<DeckOutDto> decksResponse = new ArrayList<>();
        try{
            if(user==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            decksUser = this.findDecksByUsername(user);


        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        decksUser.stream().forEach(deck -> decksResponse.add(converDeckToDeckOutDto(deck)));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(decksResponse);
    }

    @Override
    public void saveDeck(CreateDeckRequest deckRequest) throws BaseException {
        try{
            Optional<User> user = userService.findByUsername(deckRequest.getUsername());
           Optional<Language> language = languageService.findByLanguage(deckRequest.getLanguage());

            if (user.isPresent()){
                if(language.isPresent()){
                    List<Card> cards = new ArrayList<>();
                    Deck newDeck = new Deck(deckRequest.getName(),deckRequest.getDescription(),user.get(),language.get(),cards);
                    deckRepository.save(newDeck);
                }else
                    throw new BaseException("Language not found", HttpStatus.NOT_FOUND.toString());
            }
            else
                throw new BaseException("User not found", HttpStatus.NOT_FOUND.toString());

        }catch (Exception e){
            throw new BaseException("Error detected saving deck , error: " + e.getMessage() + ", uuid: " + correlationIdUtils.getActualCorrelationId(), HttpStatus.SERVICE_UNAVAILABLE.toString());
        }
    }




    @Override
    public ResponseEntity<?> getDeckById (Long id, User userLoged) throws BaseException{
        Optional<Deck> deckResponse;
        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            deckResponse = deckRepository.findById(id);

            if(!deckResponse.isPresent())
                throw new BaseException(String.format("Deck with id %s not found",id), HttpStatus.NOT_FOUND.toString());

            if (!userLoged.getRoles().contains(UserRole.ADMIN)) {
                if(!deckResponse.get().getUser().getUsername().equals(userLoged.getUsername()))
                    throw new BaseException(String.format("You have not permission to get the deck with id %s", id),HttpStatus.UNAUTHORIZED.toString());
            }

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        return ResponseEntity.ok().body(deckResponse.get());
    }



    @SneakyThrows
    public ResponseEntity<?> deleteDeck(Long id, User userLoged) throws BaseException{

        Optional<Deck> deckToUpdate = null;
        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            deckToUpdate = deckRepository.findById(id);

            if(!deckToUpdate.isPresent())
                throw new BaseException(String.format("Deck with id %s not found", id), HttpStatus.NO_CONTENT.toString());

            if (!userLoged.getRoles().contains(UserRole.ADMIN) &&
                    !deckToUpdate.get().getUser().getUsername().equals(userLoged.getUsername())) {
                throw new BaseException("You have not permissions to update the deck with id: " + id, HttpStatus.UNAUTHORIZED.toString());
            }

            deckRepository.deleteById(id);

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    @Override
    public List<String> findAllLanguage() throws Exception {
        List<String> languages = new ArrayList<>();
        try{
            List<Deck> decks = this.findAllDecks();
            if(!CollectionUtils.isEmpty(decks)){
                decks.stream().distinct().forEach(deck -> languages.add(deck.getLanguage().getLanguage()));
            }else
                throw new BaseException("Decks not found",HttpStatus.NOT_FOUND.toString());
            return languages;
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public List<String> findLanguagesByUser(User userLoged) throws Exception {
        List<String> languages = new ArrayList<>();
        try{
            List<Deck> decks = this.findAllDecks();
            if(!CollectionUtils.isEmpty(decks)){
                for(Deck deck: decks) {
                    if(deck.getUser().getUsername().equals(userLoged.getUsername()))
                        languages.add(deck.getLanguage().getLanguage());
                }
            }else{
                log.info("No decks found so the languages available are 0");
                return languages;
            }

            return languages.stream().distinct().collect(Collectors.toList());
        }catch (Exception e){
            throw e;
        }
    }

    /*@Override
    public void incrementTotalCardsOfDeck (Long id) throws BaseException,Exception{
        int totalCards=0;
        try{
            Optional<Deck> deck  = this.findDeck(id);
            if(deck.isPresent()){
                totalCards = deck.get().getNumberTotalOfCards();
                totalCards++;
                deck.get().setNumberTotalOfCards(totalCards);
                this.saveDeck(deck.get());
            }else
                throw new BaseException(String.format("Deck with id %s not found", id), HttpStatus.NOT_FOUND.toString());

        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public void decrementTotalCardsOfDeck (Long id) throws BaseException,Exception{
        int totalCards=0;
        try{
            Optional<Deck> deck  = this.findDeck(id);
            if(deck.isPresent()){
                totalCards = deck.get().getNumberTotalOfCards();
                if(totalCards>0)
                    totalCards--;
                this.saveDeck(deck.get());
            }else
                throw new BaseException(String.format("Deck with id %s not found", id), HttpStatus.NOT_FOUND.toString());

        }catch (Exception e){
            throw e;
        }
    }*/

    @Override
    public StandardResponse sendSaveDeck (CreateDeckRequest newDeck, User userLoged){
        StandardResponse standardResponse;
        Deck deckResponse = new Deck();
        List<Language> languages = new ArrayList<>();
        StandardResponse response = null;
        Message message = null;

        try {
            if (userLoged == null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            languages = languageService.findAll();
            Optional<Language> language = languages.stream().filter(l -> l.getLanguage().equals(newDeck.getLanguage())).findFirst();

            if (!language.isPresent())
                throw new BaseException("Language not supported", HttpStatus.BAD_REQUEST.toString());

            message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(newDeck))
                    .build();
            messageSender.sendSaveDeckToQueue(message);

        }
        catch (BaseException e){
            return new StandardResponse("ko",e.getMessage(),Instant.now(),e.getCode());
        }catch (Exception e){
            return new StandardResponse("ko",e.getMessage(),Instant.now(),message.getMessageProperties().getHeader(Configuration.CORRELATION_ID_HEADER_NAME));
        }

        return new StandardResponse("ok","ok",Instant.now(),message.getMessageProperties().getHeader(Configuration.CORRELATION_ID_HEADER_NAME));

    }

    private DeckOutDto converDeckToDeckOutDto (Deck deck) {
        DeckOutDto deckOutDto = modelMapper.map(deck,DeckOutDto.class);
        deckOutDto.setUsername(deck.getUser().getUsername().toString());
        return deckOutDto;
    }
}
