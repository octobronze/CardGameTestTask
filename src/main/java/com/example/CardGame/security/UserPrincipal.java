package com.example.CardGame.security;

import com.example.CardGame.tables.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class UserPrincipal implements UserDetails {
    private Integer id;
    private String password;
    private String username;
    private List<? extends GrantedAuthority> authorities = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.password = user.getPassword();
        this.username = user.getLogin();
    }
}
