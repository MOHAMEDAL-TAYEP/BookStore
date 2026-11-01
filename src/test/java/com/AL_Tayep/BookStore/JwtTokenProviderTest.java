package com.AL_Tayep.BookStore;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.AL_Tayep.BookStore.Security.JwtTokenProvider;
import com.AL_Tayep.BookStore.models.User;
import com.AL_Tayep.BookStore.models.Enums.Role;
import com.AL_Tayep.BookStore.models.Enums.UserStatus;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;
    private User user;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider("test-secret-key-that-is-at-least-32-chars-long");
        user = newUser("ali@example.com");
    }

    private User newUser(String email) {
        User u = new User();
        u.setEmail(email);
        u.setRole(Role.USER);
        u.setStatus(UserStatus.Active);
        return u;
    }

    @Test
    void generatedToken_containsEmailAsSubject() {
        String token = provider.generateToken(user);

        assertEquals("ali@example.com", provider.extractUsername(token));
    }

    @Test
    void generatedToken_isValidForSameUser() {
        String token = provider.generateToken(user);

        assertTrue(provider.isTokenValid(token, user));
    }

    @Test
    void token_isNotValidForDifferentUser() {
        String token = provider.generateToken(user);

        assertFalse(provider.isTokenValid(token, newUser("other@example.com")));
    }

    @Test
    void tamperedToken_isRejected() {
        String token = provider.generateToken(user);
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThrows(Exception.class, () -> provider.extractUsername(tampered));
    }

    @Test
    void garbageToken_isRejected() {
        assertThrows(Exception.class, () -> provider.extractUsername("not.a.jwt"));
    }
}
