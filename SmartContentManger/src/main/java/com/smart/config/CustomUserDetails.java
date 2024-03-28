package com.smart.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smart.entities.User;

public class CustomUserDetails implements UserDetails{

    private User user;

    // Constructor
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Returns the authorities granted to the user
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole());
        return List.of(simpleGrantedAuthority);
    }

    // Returns the password used to authenticate the user
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Returns the username used to authenticate the user
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Indicates whether the user's account has expired
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indicates whether the user is locked or unlocked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indicates whether the user's credentials (password) has expired
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indicates whether the user is enabled or disabled
    @Override
    public boolean isEnabled() {
        return true;
    }
}
