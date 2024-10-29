package com.jorgesacristan.englishCard.models;

import com.jorgesacristan.englishCard.enums.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class User implements EnglishCardEntity, Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id",updatable = true)
    private long id;

    @Column (name = "username", unique = true, nullable = false)
    private String username;

    @Column (name = "password",  nullable = false)
    private String password;

    @Column (name = "email",unique = true, nullable = false)
    private String email;

    @Column (name = "level", nullable = false)
    private int level;

    @Column (name = "experience", nullable = false)
    private int experience;

    @Column (name = "log_streak", nullable = false)
    private int logStreak;

    @Column (name = "gems", nullable = false)
    private int gems;

    private String avatar;

    @Column (name = "isenabled")
    private boolean isEnabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.name())).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

}
