package com.sureprompt.security;

import com.sureprompt.entity.User;
import com.sureprompt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        String email = null;
        String username = null;
        String displayName = null;
        String avatarUrl = null;
        String oauthSubject = null;

        Map<String, Object> attributes = oauth2User.getAttributes();

        if ("google".equalsIgnoreCase(provider)) {
            email = (String) attributes.get("email");
            username = email.split("@")[0]; // Base username
            displayName = (String) attributes.get("name");
            avatarUrl = (String) attributes.get("picture");
            oauthSubject = (String) attributes.get("sub");
        } else if ("github".equalsIgnoreCase(provider)) {
            email = (String) attributes.get("email");
            if (email == null) {
                // If github email is private, fallback to login
                email = attributes.get("login") + "@github.com";
            }
            username = (String) attributes.get("login");
            displayName = (String) attributes.get("name");
            avatarUrl = (String) attributes.get("avatar_url");
            oauthSubject = String.valueOf(attributes.get("id"));
        }

        User user = userService.createOrUpdateOnLogin(email, username, displayName, avatarUrl, provider, oauthSubject);

        return new CustomOAuth2User(oauth2User, user);
    }
}
