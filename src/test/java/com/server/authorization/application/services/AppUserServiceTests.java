package com.server.authorization.application.services;

import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.domain.model.PasswordResetToken;
import com.server.authorization.application.dto.EventResponseDto;
import com.server.authorization.application.dto.MessageDto;
import com.server.authorization.application.repository.abstraction.AppUserRepository;
import com.server.authorization.application.repository.abstraction.PasswordTokenRepository;
import com.server.authorization.application.service.abstraction.MessageAdapter;
import com.server.authorization.application.service.implementation.AppUserService;
import com.server.authorization.application.service.implementation.EmailClient;
import com.server.authorization.application.viewmodel.SignUpViewModel;
import com.server.authorization.application.viewmodel.UpdateProfileViewModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppUserServiceTests {
    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final PasswordTokenRepository passwordTokenRepository = mock(PasswordTokenRepository.class);
    private final MessageAdapter emailClient = mock(EmailClient.class);
    private AppUserService appUserService;

    @BeforeAll
    void setup() {
        appUserService = new AppUserService(appUserRepository,passwordTokenRepository,emailClient,"http://localhost:8080");
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

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void loadUserByUsername_withNonExistentUserName_throwsException() {
        String nonExistentUsername = "notAUser";
        when(appUserRepository.findByUsername(nonExistentUsername)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,()->appUserService.loadUserByUsername(nonExistentUsername));
    }

    @Test
    void createUser_withNewUser_returnsSuccessfully() {
        SignUpViewModel signUpViewModel = new SignUpViewModel(){{
            setFirstName("testFirst");
            setLastName("testLast");
            setEmail("test@email.com");
            setPassword("testPass");
            setConfirmPassword("testPass");
        }};
        AppUser newUser = AppUser.createNewUser(
                signUpViewModel.getEmail(),
                signUpViewModel.getFirstName(),
                signUpViewModel.getLastName(),
                signUpViewModel.getPassword()
        );

        when(appUserRepository.findByUsername(signUpViewModel.getEmail())).thenReturn(null);
        when(appUserRepository.saveAndFlush(any(AppUser.class))).thenReturn(newUser);

        appUserService.createUser(signUpViewModel);

        verify(appUserRepository,times(1)).saveAndFlush(any(AppUser.class));
    }

    @Test
    void createUser_withExistingUser_throwsException() {
        SignUpViewModel signUpViewModel = new SignUpViewModel(){{
            setFirstName("testFirst");
            setLastName("testLast");
            setEmail("test@email.com");
            setPassword("testPass");
            setConfirmPassword("testPass");
        }};

        when(appUserRepository.findByUsername(signUpViewModel.getEmail())).thenReturn(AppUser.createNewUser(
                signUpViewModel.getEmail(),
                signUpViewModel.getFirstName(),
                signUpViewModel.getLastName(),
                signUpViewModel.getPassword()
        ));

        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()-> appUserService.createUser(signUpViewModel));

        assertEquals("Username not available.", exception.getMessage());
    }

    @Test
    void createUser_withNullUser_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()-> appUserService.createUser(null));

        assertEquals("New user is required.", exception.getMessage());
    }

    @Test
    void createPasswordResetTokenForUser_withValidUserAndToken_returnsResetToken()
    {
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","testPass");
        String token = UUID.randomUUID().toString();
        PasswordResetToken expectedToken = PasswordResetToken.generateToken(token, user);
        when(passwordTokenRepository.saveAndFlush(any(PasswordResetToken.class))).thenReturn(expectedToken);

        PasswordResetToken actualToken = appUserService.createPasswordResetTokenForUser(user,token);

        verify(passwordTokenRepository,times(1)).saveAndFlush(any(PasswordResetToken.class));
        assertEquals(expectedToken.getToken(),actualToken.getToken());
        assertEquals(expectedToken.getAppUser(),actualToken.getAppUser());
    }

    @Test
    void createPasswordResetTokenForUser_withNullUserAndValidToken_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.createPasswordResetTokenForUser(null,UUID.randomUUID().toString()));

        assertEquals("User and token are required.", exception.getMessage());
    }

    @Test
    void createPasswordResetTokenForUser_withValidUserAndNullToken_throwsException() {
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","testPass");
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.createPasswordResetTokenForUser(user,null));

        assertEquals("User and token are required.", exception.getMessage());
    }

    @Test
    void createPasswordResetTokenForUser_withValidUserAndEmptyToken_throwsException() {
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","testPass");
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.createPasswordResetTokenForUser(user,""));

        assertEquals("User and token are required.", exception.getMessage());
    }

    @Test
    void sendPasswordResetEmail_withValidUserAndToken_sendsEmail() throws MessagingException, IOException {
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","testPass");
        PasswordResetToken resetToken = PasswordResetToken.generateToken(UUID.randomUUID().toString(), user);
        doNothing().when(emailClient).sendMessage(any(MessageDto.class));

        appUserService.sendPasswordResetEmail(user,resetToken);
        verify(emailClient,times(1)).sendMessage(any(MessageDto.class));
    }

    @Test
    void sendPasswordResetEmail_withNullUserAndValidToken_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.createPasswordResetTokenForUser(null,UUID.randomUUID().toString()));

        assertEquals("User and token are required.", exception.getMessage());
    }

    @Test
    void sendPasswordResetEmail_withValidUserAndNullToken_throwsException() {
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","testPass");
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.sendPasswordResetEmail(user,null));

        assertEquals("User and token are required.", exception.getMessage());
    }

    @Test
    void validatePasswordResetToken_withExistingUserAndToken_returnsSuccessEventResponseDto() {
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","testPass");
        PasswordResetToken passwordResetToken = PasswordResetToken.generateToken(UUID.randomUUID().toString(),user);
        when(passwordTokenRepository.findByToken(any(String.class))).thenReturn(passwordResetToken);
        doNothing().when(passwordTokenRepository).delete(any(PasswordResetToken.class));
        EventResponseDto expectedResponse = EventResponseDto.createResponse(true,"Please reset your password below.");

        EventResponseDto actualResponse = appUserService.validatePasswordResetToken(user.getUserId(),passwordResetToken.getToken());

        assertEquals(expectedResponse.isSuccess(),actualResponse.isSuccess());
        assertEquals(expectedResponse.getMessage(),actualResponse.getMessage());
        verify(passwordTokenRepository,times(1)).findByToken(passwordResetToken.getToken());
        verify(passwordTokenRepository,times(1)).delete(any(PasswordResetToken.class));
    }

    @Test
    void validatePasswordResetToken_withNullUserAndToken_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.validatePasswordResetToken(null,null));

        assertEquals("UserId and Token are required.", exception.getMessage());
    }

    @Test
    void validatePasswordResetToken_withEmptyUserAndValidToken_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.validatePasswordResetToken("",UUID.randomUUID().toString()));

        assertEquals("UserId and Token are required.", exception.getMessage());
    }

    @Test
    void validatePasswordResetToken_withValidUserAndEmptyToken_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.validatePasswordResetToken(UUID.randomUUID().toString(),""));

        assertEquals("UserId and Token are required.", exception.getMessage());
    }

    @Test
    void validatePasswordResetToken_withExistingUserAndNonExistentToken_returnsFailureEventResponseDto() {
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","testPass");
        PasswordResetToken passwordResetToken = PasswordResetToken.generateToken(UUID.randomUUID().toString(),user);
        when(passwordTokenRepository.findByToken(any(String.class))).thenReturn(null);
        EventResponseDto expectedResponse = EventResponseDto.createResponse(false,"Invalid password reset token.");

        EventResponseDto actualResponse = appUserService.validatePasswordResetToken(user.getUserId(),passwordResetToken.getToken());

        assertEquals(expectedResponse.isSuccess(),actualResponse.isSuccess());
        assertEquals(expectedResponse.getMessage(),actualResponse.getMessage());
        verify(passwordTokenRepository,times(1)).findByToken(passwordResetToken.getToken());
    }

    @Test
    void validatePasswordResetToken_withNonMatchingUserAndValidToken_returnsFailureEventResponseDto() {
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","testPass");
        PasswordResetToken passwordResetToken = PasswordResetToken.generateToken(UUID.randomUUID().toString(),user);
        when(passwordTokenRepository.findByToken(any(String.class))).thenReturn(passwordResetToken);
        EventResponseDto expectedResponse = EventResponseDto.createResponse(false,"Invalid password reset token.");

        EventResponseDto actualResponse = appUserService.validatePasswordResetToken(UUID.randomUUID().toString(),passwordResetToken.getToken());

        assertEquals(expectedResponse.isSuccess(),actualResponse.isSuccess());
        assertEquals(expectedResponse.getMessage(),actualResponse.getMessage());
        verify(passwordTokenRepository,times(1)).findByToken(passwordResetToken.getToken());
    }

    @Test
    void validatePasswordResetToken_withValidUserAndExpiredToken_returnsFailureEventResponseDto() {
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","testPass");
        PasswordResetToken passwordResetToken = PasswordResetToken.generateToken(UUID.randomUUID().toString(),user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().minusDays(2));
        when(passwordTokenRepository.findByToken(any(String.class))).thenReturn(passwordResetToken);
        EventResponseDto expectedResponse = EventResponseDto.createResponse(false,"Your password reset token has expired.");

        EventResponseDto actualResponse = appUserService.validatePasswordResetToken(user.getUserId(),passwordResetToken.getToken());

        assertEquals(expectedResponse.isSuccess(),actualResponse.isSuccess());
        assertEquals(expectedResponse.getMessage(),actualResponse.getMessage());
        verify(passwordTokenRepository,times(1)).findByToken(passwordResetToken.getToken());
    }

    @Test
    void changeUserPassword_withValidUserIdAndNewPassword_changesPassword() {
        String newPassword = "changePassNew";
        AppUser user = AppUser.createNewUser("changeEmail","changeFirst","changeLast","changePass");
        when(appUserRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(user);

        appUserService.changeUserPassword(user.getUserId(),newPassword);

        verify(appUserRepository, atLeastOnce()).save(any(AppUser.class));
    }

    @Test
    void changeUserPassword_withNonExistentUserAndNewPassword_throwsException() {
        when(appUserRepository.findById(any(String.class))).thenReturn(Optional.empty());
        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class,()->
                        appUserService.changeUserPassword("badUserId","newPassword"));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {",pass","id,","id,null","null,pass"}, nullValues = "null")
    void changeUserPassword_withInvalidParameters_throwsException(String userid, String newPassword) {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.changeUserPassword(userid,newPassword));

        assertEquals("UserId and New Password are required.", exception.getMessage());
    }

    @Test
    void matchesCurrentPassword_withValidUserAndMatchingPassword_returnsTrue() {
        String currentPassword = "currentPass";
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast",currentPassword);
        when(appUserRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        assertTrue(appUserService.matchesCurrentPassword(user.getUserId(),currentPassword));
        verify(appUserRepository, times(1)).findById(user.getUserId());
    }

    @Test
    void matchesCurrentPassword_withValidUserAndNonMatchingPassword_returnsFalse() {
        String currentPassword = "currentPass";
        AppUser user = AppUser.createNewUser("testEmail","testFirst","testLast","differentPassword");
        when(appUserRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        assertFalse(appUserService.matchesCurrentPassword(user.getUserId(),currentPassword));
        verify(appUserRepository, times(1)).findById(user.getUserId());
    }

    @Test
    void matchesCurrentPassword_withNonExistentUserAndCurrentPassword_throwsException() {
        when(appUserRepository.findById(any(String.class))).thenReturn(Optional.empty());
        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class,()->
                        appUserService.matchesCurrentPassword("badUserId","newPassword"));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {",pass","id,","id,null","null,pass"}, nullValues = "null")
    void matchesCurrentPassword_withInvalidParameters_throwsException(String userid, String currentPass) {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.matchesCurrentPassword(userid,currentPass));

        assertEquals("UserId and Current Password are required.", exception.getMessage());
    }

    @Test
    void updateProfile_withValidViewModel_updatesUser() {
        AppUser user = AppUser.createNewUser("updateEmail","updateFirst","updateLast","updatePass");
        when(appUserRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(user);
        UpdateProfileViewModel updateProfileViewModel = UpdateProfileViewModel.createInstance(
                user.getUserId(),"first", "last", "email", "phone");

        appUserService.updateProfile(updateProfileViewModel);

        verify(appUserRepository,atLeastOnce()).save(any(AppUser.class));
    }

    @Test
    void updateProfile_withNonExistentUser_throwsException() {
        UpdateProfileViewModel updateProfileViewModel = UpdateProfileViewModel.createInstance(
                "id","first", "last", "email", "phone");
        when(appUserRepository.findById("id")).thenReturn(Optional.empty());
        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class,()->
                        appUserService.updateProfile(updateProfileViewModel));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void updateProfile_withInvalidViewModel_throwsException() {
        InvalidParameterException exception =
                assertThrows(InvalidParameterException.class,()->
                        appUserService.updateProfile(null));

        assertEquals("User update information is required.", exception.getMessage());
    }

}
