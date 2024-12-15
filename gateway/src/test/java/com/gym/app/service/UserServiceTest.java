package com.gym.app.service;

import com.gym.app.model.User;
import com.gym.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findUserByUsername_UserFound() {
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Mono.just(mockUser));

        Mono<User> result = userService.findUserByUsername(username);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getUsername().equals(username))
                .verifyComplete(); // Поток завершился успешно

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findUserByUsername_UserNotFound() {
        String username = "nonexistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Mono.empty());

        Mono<User> result = userService.findUserByUsername(username);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("User not found"))
                .verify();

        verify(userRepository, times(1)).findByUsername(username);
    }

}