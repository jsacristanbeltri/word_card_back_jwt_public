package com.jorgesacristan.englishCard.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().anyRequest();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                .csrf().disable() //?
                .exceptionHandling() //
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint) //control de excepciones
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //politica sesiones sin estado, no usar sesiones
                .and()
                .authorizeRequests()
                    //.antMatchers(HttpMethod.GET, "/deck/**").hasRole("USER")
                    .antMatchers(HttpMethod.GET, "/deck/**").hasAnyRole("USER","ADMIN")
                    .antMatchers(HttpMethod.POST, "/deck/**").hasAnyRole("USER","ADMIN")
                    .antMatchers(HttpMethod.PUT, "/deck/**").hasAnyRole("USER","ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/deck/**").hasAnyRole("USER","ADMIN")
                    .antMatchers(HttpMethod.POST, "/deck/**").hasAnyRole("USER","ADMIN")
                    .anyRequest().authenticated();

        // Añadimos el filtro (lo hacemos más adelante).
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class); //filtro coge token y si es valido


        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);
    }


    /*
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    */

}
