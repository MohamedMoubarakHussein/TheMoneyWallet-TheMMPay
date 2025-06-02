package com.themoneywallet.authenticationservice.service.implementation;

import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.entity.UserRole;
import com.themoneywallet.authenticationservice.event.Event;
import com.themoneywallet.authenticationservice.event.EventType;
import com.themoneywallet.authenticationservice.event.UserCreationEvent;
import com.themoneywallet.authenticationservice.event.UserLogInEvent;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.service.defintion.AuthServiceDefintion;
import com.themoneywallet.authenticationservice.utilities.DatabaseHelper;
import com.themoneywallet.authenticationservice.utilities.HttpHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *  Auth service is the core service of the auth
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthServiceDefintion {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtRefService jwtRefService;
    private final AuthenticationManager authenticationManager;
    private final DatabaseHelper databaseHelper;
    private final HttpHelper httpHelper;
    private final UnifiedResponse unifiedResponse;
    private final EventProducer eventProducer;
    private final Integer COOKIE_MAX_AGE_H = 7;
    private final String COOKIE_PATH = "/";
    private final boolean IS_COOKIE_SECURE = false;

    @Override
    @Transactional
    public ResponseEntity<String> signUp(SignUpRequest request) {
        Map<String, String> data = new HashMap<>();
        boolean errorFlage = false;
        if (databaseHelper.isEmailExist(request.getEmail())) {
            data.put("email", "This Email address is used.");
            errorFlage = true;
        }
        if (databaseHelper.isUserNameExist(request.getUserName())) {
            data.put("userName", "This userName is used.");
            errorFlage = true;
        }
        if (errorFlage) {
            unifiedResponse.setData(data);
            unifiedResponse.setHaveError(true);
            unifiedResponse.setHaveData(true);
            unifiedResponse.setStatusInternalCode(null);
            return new ResponseEntity<>(
                unifiedResponse.toString(),
                HttpStatus.BAD_REQUEST
            );
        }

        // first part save the userCredential
        UserCredential userCredential;
        try {
            userCredential = saveUserCredentialInAuthDb(request);
        } catch (Exception e) {
            data.put("internal", "Contact website support Auth006 happend.");
            unifiedResponse.setData(data);
            unifiedResponse.setHaveError(true);

            unifiedResponse.setStatusInternalCode("Auth006");
            return new ResponseEntity<>(
                unifiedResponse.toString(),
                HttpStatus.BAD_REQUEST
            );
        }

        //  second part save the rest of information in user managment service

        try {
            saveUserCredentialInUserMangmentService(request, userCredential);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus()
                .setRollbackOnly();
            unifiedResponse.setHaveError(true);
            data.put("internal", "Contact website support Auth007 happend.");
            unifiedResponse.setData(data);
            unifiedResponse.setStatusInternalCode("Auth007");
            return new ResponseEntity<>(
                unifiedResponse.toString(),
                HttpStatus.BAD_REQUEST
            );
        }

        return handleSuccessed(request.getEmail());
    }

    @Override
    public ResponseEntity<String> signIn(AuthRequest request) {
        Map<String, String> data = new HashMap<>();
        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                    )
                );

            return handleSuccessedLogIn(
                this.userCredentialRepository.findByEmail(
                        request.getEmail()
                    ).get()
            );
        } catch (Exception ex) {
            data.put("Internal", "Wrong userName or password.");
            unifiedResponse.setHaveData(true);
            unifiedResponse.setData(data);
            unifiedResponse.setHaveError(true);
            unifiedResponse.setStatusInternalCode("RAuth005");

            return new ResponseEntity<>(
                unifiedResponse.toString(),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    public boolean validToken(String token) {
        boolean isValid = this.jwtService.isTokenValid(token);
        return isValid;
    }

    private ResponseEntity<String> handleSuccessed(String email) {
        String accToken = this.jwtService.generateToken(email);
        String refToken = this.jwtRefService.generateToken(email);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refToken)
            .httpOnly(true)
            .secure(IS_COOKIE_SECURE) // For HTTPS only
            .sameSite("Lax")
            .maxAge(Duration.ofHours(COOKIE_MAX_AGE_H))
            .path(COOKIE_PATH)
            .build();

        UserCredential user =
            this.userCredentialRepository.findByEmail(email).get();
        user.setToken(refToken);
        user.setLastLogin(LocalDateTime.now());
        user.setIpAddress("192.168.1.1");
        user.setRevoked(false);
        this.userCredentialRepository.save(user);
        unifiedResponse.setHaveData(false);
        unifiedResponse.setStatusInternalCode(null);

        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header("Authorization", "Bearer " + accToken)
            .body(unifiedResponse.toString());
    }

    private ResponseEntity<String> handleSuccessedLogIn(UserCredential user) {
        String email = user.getEmail();
        String accToken = this.jwtService.generateToken(email);
        String refToken = this.jwtRefService.generateToken(email);
        user.setToken(refToken);
        this.userCredentialRepository.save(user);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refToken)
            .httpOnly(true)
            .secure(IS_COOKIE_SECURE) // For HTTPS only
            .sameSite("Lax")
            .maxAge(Duration.ofHours(COOKIE_MAX_AGE_H))
            .path(COOKIE_PATH)
            .build();

        user.setToken(refToken);
        user.setLastLogin(LocalDateTime.now());
        user.setIpAddress("192.168.1.1");
        user.setRevoked(false);
        this.userCredentialRepository.save(user);
        unifiedResponse.setHaveData(false);
        unifiedResponse.setStatusInternalCode(null);

        UserLogInEvent info = UserLogInEvent.builder()
            .email(user.getEmail())
            .userRole(user.getUserRole().toString())
            .id(user.getId())
            .build();

        Event event = Event.builder()
            .eventId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .userId(String.valueOf(user.getId()))
            .eventType(EventType.LOGIN_SUCCESS)
            .additionalData(Map.of("data", Map.of("usreData", info.toString())))
            .build();
        this.eventProducer.publishSigninEvent(event);

        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header("Authorization", "Bearer " + accToken)
            .body(unifiedResponse.toString());
    }

    private UserCredential saveUserCredentialInAuthDb(SignUpRequest request) {
        UserCredential credential = UserCredential.builder()
            .email(request.getEmail())
            .userName(request.getUserName())
            .password(this.passwordEncoder.encode(request.getPassword()))
            .userRole(UserRole.ROLE_USER)
            .locked(false)
            .enabled(true)
            .build();

        return this.userCredentialRepository.save(credential);
    }

    private void saveUserCredentialInUserMangmentService(
        SignUpRequest request,
        UserCredential credential
    ) {
        UserCreationEvent info = UserCreationEvent.builder()
            .email(request.getEmail())
            .password(credential.getPassword())
            .userRole(credential.getUserRole().toString())
            .userName(request.getUserName())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .id(credential.getId())
            .build();
        Event authEvent = Event.builder()
            .eventId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .userId(String.valueOf(info.getId()))
            .eventType(EventType.SIGN_UP)
            .additionalData(Map.of("data", Map.of("usreData", info.toString())))
            .build();

        eventProducer.publishSignUpEvent(authEvent);
    }

    public ResponseEntity<String> refreshToken(String refreshToken) {
        log.info("cdcd ");

        Optional<UserCredential> user =
            this.userCredentialRepository.findByToken(refreshToken);
        UserCredential userr;
        if (user.isPresent()) {
            userr = user.get();
        } else {
            return ResponseEntity.status(401).body("Invalid  Token");
        }

        /* 
        if(userr.isRevoked()){
            return ResponseEntity.status(401).body(UnifiedResponse.builder().data(Map.of("internal","Invalid  Token"))
            .haveError(true)
            .statusInternalCode("Auth004").build());
        }
            */

        // revoke the token

        // generate new access , refresh token
        String accToken = this.jwtService.generateToken(userr.getEmail());
        String refToken = this.jwtRefService.generateToken(userr.getEmail());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refToken)
            .httpOnly(true)
            //.secure(true) // For HTTPS only
            .sameSite("Lax")
            .maxAge(Duration.ofHours(7))
            .path("/")
            .build();
        user.get().setToken(refreshToken);
        this.userCredentialRepository.save(user.get());

        Map<String, String> data = new HashMap<>();
        data.put("token", accToken);

        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header("Authorization", "Bearer " + accToken)
            .body(accToken);
    }

    public ResponseEntity<UnifiedResponse> verifyEmail(String token) {
        // validate token that send to email  if vaild change user to active
        return null;
    }

    public ResponseEntity<UnifiedResponse> logout(HttpServletRequest request) {
        String cookieName = "refreshToken";
        String token = Arrays.stream(request.getCookies())
            .filter(cookie -> cookieName.equals(cookie.getName()))
            .findFirst()
            .get()
            .getValue();
        log.info("*************" + token);
        if (token != null) {
            Optional<UserCredential> user =
                userCredentialRepository.findByToken(token);

            user.ifPresent(t -> {
                user.get().setToken("");

                Event event = Event.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .userId(String.valueOf(user.get().getId()))
                    .eventType(EventType.LOGOUT)
                    .additionalData(
                        Map.of(
                            "data",
                            Map.of("usreData", user.get().toString())
                        )
                    )
                    .build();
                this.eventProducer.publishSigninEvent(event);
                userCredentialRepository.save(user.get());
            });
        }
        log.info("******************");
        ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
            .httpOnly(true)
            .secure(IS_COOKIE_SECURE)
            .sameSite("Lax")
            .maxAge(0)
            .path(COOKIE_PATH)
            .build();

        SecurityContextHolder.clearContext();

        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header("Authorization", "")
            .body(
                UnifiedResponse.builder()
                    .haveError(false)
                    .haveData(false)
                    .build()
            );
    }
}
