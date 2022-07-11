package com.server.authorization.application.services;

import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.repository.abstraction.AppUserRepository;
import com.server.authorization.application.service.implementation.AppUserService;
import com.server.authorization.application.viewmodel.CreateUserViewModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppUserServiceTests {
    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private AppUserService appUserService;

    @BeforeAll
    void setup() {
        appUserService = new AppUserService(appUserRepository);
    }

    @Test
    void loadUserByUsername_withExistingUserName_returnsUser() {
        AppUser expectedUser = AppUser.createNewUser(
                "test@email.com",
                "testFirst",
                "testLast",
                "testPass");

        when(appUserRepository.findByUsername(expectedUser.getUsername())).thenReturn(expectedUser);

        AppUser actualUser = (AppUser)appUserService.loadUserByUsername(expectedUser.getUsername());

        assertTrue(expectedUser.equals(actualUser));
    }

    @Test
    void loadUserByUsername_withNonExistentUserName_throwsException() {
        String nonExistentUsername = "notAUser";
        when(appUserRepository.findByUsername(nonExistentUsername)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,()->appUserService.loadUserByUsername(nonExistentUsername));
    }

    @Test
    void createUser_withNewUser_returnsSuccessfully() {
        CreateUserViewModel createUserViewModel = new CreateUserViewModel(){{
            setFirstName("testFirst");
            setLastName("testLast");
            setEmail("test@email.com");
            setPassword("testPass");
            setConfirmPassword("testPass");
        }};
        AppUser newUser = AppUser.createNewUser(
                createUserViewModel.getEmail(),
                createUserViewModel.getFirstName(),
                createUserViewModel.getLastName(),
                createUserViewModel.getPassword()
        );

        when(appUserRepository.findByUsername(createUserViewModel.getEmail())).thenReturn(null);
        when(appUserRepository.saveAndFlush(any(AppUser.class))).thenReturn(newUser);

        appUserService.createUser(createUserViewModel);

        verify(appUserRepository,times(1)).saveAndFlush(any(AppUser.class));
    }

    @Test
    void createUser_withExistingUser_throwsException() {
        CreateUserViewModel createUserViewModel = new CreateUserViewModel(){{
            setFirstName("testFirst");
            setLastName("testLast");
            setEmail("test@email.com");
            setPassword("testPass");
            setConfirmPassword("testPass");
        }};

        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()-> appUserService.createUser(createUserViewModel));

        assertEquals("Username not available.", exception.getMessage());
    }

    @Test
    void createUser_withNullUser_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()-> appUserService.createUser(null));

        assertEquals("New user is required.", exception.getMessage());
    }
}
