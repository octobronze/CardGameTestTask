package com.example.CardGame.services;

import com.example.CardGame.dtos.UserRegistrationRequestDto;
import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.repos.UserRepository;
import com.example.CardGame.dtos.UserLoginRequestDto;
import com.example.CardGame.dtos.UserLoginResponseDto;
import com.example.CardGame.security.UserPrincipal;
import com.example.CardGame.security.jwt.JwtService;
import com.example.CardGame.specifications.UserSpecification;
import com.example.CardGame.tables.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.CardGame.consts.ExceptionMessagesConsts.BAD_CREDENTIALS;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.duration}")
    private long tokenLifeTimeMs;

    public UserLoginResponseDto loginUser(UserLoginRequestDto requestDto) {
        User user = userRepository.findOne(
                UserSpecification.builder()
                        .login(requestDto.getLogin()).build()
                )
                .orElseThrow(() -> new BadRequestException(BAD_CREDENTIALS));
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BadRequestException(BAD_CREDENTIALS);
        }

        UserLoginResponseDto responseDto = new UserLoginResponseDto();

        responseDto.setToken(
                jwtService.generateTokenByUserPrincipal(new UserPrincipal(user))
        );
        responseDto.setLifeTimeMs(tokenLifeTimeMs);

        return responseDto;
    }

    public int registerUser(UserRegistrationRequestDto requestDto) {
        User user = new User();
        user.setName(requestDto.getName());
        user.setLogin(requestDto.getLogin());

        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        try {
            User savedUser = userRepository.save(user);
            return savedUser.getId();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(User.ExceptionMessages.USER_ALREADY_EXISTS);
        }
    }
}
