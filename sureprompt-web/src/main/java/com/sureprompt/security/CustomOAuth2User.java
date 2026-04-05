package com.sureprompt.security;

import com.sureprompt.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final User databaseUser;

    public CustomOAuth2User(OAuth2User oauth2User, User databaseUser) {
        this.oauth2User = oauth2User;
        this.databaseUser = databaseUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + databaseUser.getRole().name()));
    }

    @Override
    public String getName() {
        return databaseUser.getUsername();
    }

    public Long getUserId() {
        return databaseUser.getId();
    }

    public User getDatabaseUser() {
        return databaseUser;
    }

    public boolean isBanned() {
        return databaseUser.isBanned();
    }
}
