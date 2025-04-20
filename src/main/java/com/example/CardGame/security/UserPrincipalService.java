package com.example.CardGame.security;

import com.example.CardGame.repos.UserRepository;
import com.example.CardGame.specifications.UserSpecification;
import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.security.jwt.JwtService;
import com.example.CardGame.tables.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserPrincipalService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserPrincipal createUserPrincipalWithJwt(String jwt) {
        Integer userId = jwtService.getUserId(jwt);
        User user = userRepository.findOne(UserSpecification.builder()
                .id(userId).build()
        ).orElseThrow(() -> new BadRequestException(User.ExceptionMessages.USER_NOT_FOUND));

        UserPrincipal userPrincipal = new UserPrincipal();

        userPrincipal.setId(userId);
        userPrincipal.setUsername(user.getLogin());
        userPrincipal.setPassword(user.getPassword());

        return userPrincipal;
    }
}
