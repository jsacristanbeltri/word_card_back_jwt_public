package com.jorgesacristan.englishCard.security;

import java.util.Date;
import java.util.stream.Collectors;

import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.java.Log;

@Log
@Component
public class JwtTokenProvider {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";

    //palabra secreta
    @Value("${jwt.secret:EnUnLugarDeLaManchaDeCuyoNombreNoQuieroAcordarmeNoHaMuchoTiempoQueViviaUnHidalgo}")
    private String jwtSecreto;

    //Duracion en sec del token
    @Value("${jwt.token-expiration:86400}")
    private int jwtDuracionTokenEnSegundos;

    public String generateToken(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Date tokenExpirationDate = new Date(System.currentTimeMillis() + (jwtDuracionTokenEnSegundos * 1000));

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(jwtSecreto.getBytes()), SignatureAlgorithm.HS512) //Firma
                .setHeaderParam("typ", TOKEN_TYPE)  //cabecera
                .setSubject(Long.toString(user.getId())) //Sujecto
                .setIssuedAt(new Date()) //fecha de creacion del token
                .setExpiration(tokenExpirationDate) //fecha de expiracion del token
                .claim("fullname", user.getUsername())
                .claim("roles", user.getRoles().stream()
                        .map(UserRole::name)
                        .collect(Collectors.joining(", "))
                )
                .compact();

    }


    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecreto.getBytes()))
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());

    }


    public boolean validateToken(String authToken) {

        try {
            Jwts.parser().setSigningKey(jwtSecreto.getBytes()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.info("Error en la firma del token JWT: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.info("Token malformado: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.info("El token ha expirado: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.info("Token JWT no soportado: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.info("JWT claims vacío");
        }
        return false;



    }




}
