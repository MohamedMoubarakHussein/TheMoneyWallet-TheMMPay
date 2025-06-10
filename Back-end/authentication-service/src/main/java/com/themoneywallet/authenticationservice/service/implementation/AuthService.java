package com.themoneywallet.authenticationservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.dto.response.UnifiedResponse;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.entity.fixed.FixedInternalValues;
import com.themoneywallet.authenticationservice.entity.fixed.ResponseKey;
import com.themoneywallet.authenticationservice.entity.fixed.UserRole;
import com.themoneywallet.authenticationservice.event.Event;
import com.themoneywallet.authenticationservice.event.EventType;
import com.themoneywallet.authenticationservice.event.UserCreationEvent;
import com.themoneywallet.authenticationservice.event.UserLogInEvent;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.service.defintion.AuthServiceDefintion;
import com.themoneywallet.authenticationservice.utilities.DatabaseHelper;
import com.themoneywallet.authenticationservice.utilities.Pair;
import com.themoneywallet.authenticationservice.utilities.SerializationDeHalper;
import com.themoneywallet.authenticationservice.utilities.UnifidResponseHandler;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
    private final EventHandler eventHandler;
    private final DatabaseHelper databaseHelper;
    private final ObjectMapper objectMapper;
    private final UnifidResponseHandler unifidHandler;
    private final UnifiedResponse unifiedResponse;
    private final SerializationDeHalper serializationDeHelper;
    private final EventProducer eventProducer;
    private final Integer COOKIE_MAX_AGE_H = 7;
    private final String COOKIE_PATH = "/";
    private final boolean IS_COOKIE_SECURE = false;
    private final String COOKIE_SAME_SITE = "Lax";

    @Override
    @Transactional
    public ResponseEntity<UnifiedResponse> signUp(
        SignUpRequest request,
        ServletRequest req
    ) {
        String userIdent = getIden(req);
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
            return new ResponseEntity<>(
                this.unifidHandler.makResponse(
                        true,
                        this.unifidHandler.makeRespoData(
                                ResponseKey.ERROR,
                                data
                            ),
                        true,
                        "AUVD11002"
                    ),
                HttpStatus.BAD_REQUEST
            );
        }

        log.info("first part done");
        // first part save the userCredential
        UserCredential userCredential;
        try {
            userCredential = saveUserCredentialInAuthDb(request, userIdent);
        } catch (Exception e) {
            data.put(
                "internal",
                "Contact website support error code #AUDB10001."
            );

            unifiedResponse.setStatusInternalCode("AUDB10001");
            return new ResponseEntity<>(
                this.unifidHandler.makResponse(
                        true,
                        this.unifidHandler.makeRespoData(
                                ResponseKey.INTERNAL_ERROR,
                                data
                            ),
                        true,
                        "AUDB10001"
                    ),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        //  second part save the rest of information in user managment service
        log.info("second part done");
        int done = 0;
        try {
            done = saveUserCredentialInUserMangmentService(
                request,
                userCredential
            );
            if (done == 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus()
                .setRollbackOnly();
            data.put(
                "internal",
                "Contact website support error code #AUUC10001."
            );

            return new ResponseEntity<>(
                this.unifidHandler.makResponse(
                        true,
                        this.unifidHandler.makeRespoData(
                                ResponseKey.ERROR,
                                data
                            ),
                        true,
                        "AUUC10001"
                    ),
                (done == 1)
                    ? HttpStatus.INTERNAL_SERVER_ERROR
                    : HttpStatus.BAD_REQUEST
            );
        }
        return handleSuccessed(request.getEmail(), userIdent, "signup");
    }

    @Override
    public ResponseEntity<UnifiedResponse> signIn(
        AuthRequest request,
        ServletRequest req
    ) {
        String userIdent = getIden(req);
        Map<String, String> data = new HashMap<>();
        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                    )
                );
            return handleSuccessed(request.getEmail(), userIdent, "signin");
        } catch (Exception ex) {
            data.put("Internal", "Wrong userName or password.");

            return new ResponseEntity<>(
                this.unifidHandler.makResponse(
                        true,
                        this.unifidHandler.makeRespoData(
                                ResponseKey.ERROR,
                                data
                            ),
                        true,
                        "AUCR11001"
                    ),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    public boolean validToken(String token) {
        boolean isValid = this.jwtService.isTokenValid(token);
        return isValid;
    }

    private ResponseEntity<UnifiedResponse> handleSuccessed(
        String email,
        String userIdent,
        String status
    ) {
        String accToken = this.jwtService.generateToken(email);
        String refToken = this.jwtRefService.generateToken(email);
        String signUpToken = UUID.randomUUID().toString();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refToken)
            .httpOnly(true)
            .secure(IS_COOKIE_SECURE)
            .sameSite(COOKIE_SAME_SITE)
            .maxAge(Duration.ofHours(COOKIE_MAX_AGE_H))
            .path(COOKIE_PATH)
            .build();

        UserCredential user =
            this.userCredentialRepository.findByEmail(email).get();
        user.setToken(refToken);
        user.setLastLogin(LocalDateTime.now());
        user.setIpAddress(userIdent);
        user.setValidTill(LocalDateTime.now().plusHours(COOKIE_MAX_AGE_H));
        user.setRevoked(false);
        user.setEmailVerificationToken(signUpToken);
        this.userCredentialRepository.save(user);
        if (status.equals("signin")) {
            UserLogInEvent info = UserLogInEvent.builder()
                .email(user.getEmail())
                .userRole(user.getUserRole().toString())
                .id(user.getId())
                .build();

            String data = this.serializationDeHelper.serailization(info);
            if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
                //TODO
            }

            Event event =
                this.eventHandler.makeEvent(
                        EventType.LOGIN_SUCCESS,
                        user.getId(),
                        this.unifidHandler.makeRespoData(
                                ResponseKey.DATA,
                                Map.of("usreData", data)
                            )
                    );

            this.eventProducer.publishSigninEvent(event);
        }
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header("Authorization", "Bearer " + accToken)
            .body(this.unifidHandler.makResponse(false, null, false, null));
    }

    private UserCredential saveUserCredentialInAuthDb(
        SignUpRequest request,
        String ident
    ) {
        UserCredential credential = UserCredential.builder()
            .email(request.getEmail())
            .userName(request.getUserName())
            .password(this.passwordEncoder.encode(request.getPassword()))
            .userRole(UserRole.ROLE_USER)
            .locked(false)
            .enabled(true)
            .lastLogin(LocalDateTime.now())
            .validTill(LocalDateTime.now().plusHours(COOKIE_MAX_AGE_H))
            .ipAddress(ident)
            .build();

        return this.userCredentialRepository.save(credential);
    }

    // 0 not saved  1 saved
    @Transactional(propagation = Propagation.REQUIRED)
    private Integer saveUserCredentialInUserMangmentService(
        SignUpRequest request,
        UserCredential credential
    ) {
        UserCreationEvent info = UserCreationEvent.builder()
            .email(request.getEmail())
            .userRole(credential.getUserRole().toString())
            .userName(request.getUserName())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .id(credential.getId())
            .build();

        String data = this.serializationDeHelper.serailization(info);
        if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
            return 0;
        }
        Event event =
            this.eventHandler.makeEvent(
                    EventType.SIGN_UP,
                    info.getId(),
                    Map.of(
                        ResponseKey.DATA.toString(),
                        Map.of("usreData", data)
                    )
                );

        eventProducer.publishSignUpEvent(event);
        return 1;
    }

    public ResponseEntity<String> refreshToken(
        String refreshToken,
        ServletRequest req
    ) throws JsonProcessingException {
        String userIdent = getIden(req);

        Optional<UserCredential> user =
            this.userCredentialRepository.findByToken(refreshToken);
        UserCredential userr;
        if (user.isPresent()) {
            userr = user.get();
        } else {
            return new ResponseEntity<>(
                this.objectMapper.writeValueAsString(
                        this.unifidHandler.makResponse(
                                true,
                                this.unifidHandler.makeRespoData(
                                        ResponseKey.ERROR,
                                        Map.of("token", "Invalid  Token")
                                    ),
                                true,
                                "AUTK11001"
                            )
                    ),
                HttpStatus.valueOf(401)
            );
        }

        String accToken = this.jwtService.generateToken(userr.getEmail());
        String refToken = this.jwtRefService.generateToken(userr.getEmail());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refToken)
            .httpOnly(true)
            .secure(IS_COOKIE_SECURE)
            .sameSite(COOKIE_SAME_SITE)
            .maxAge(Duration.ofHours(COOKIE_MAX_AGE_H))
            .path(COOKIE_PATH)
            .build();
        user.get().setToken(refreshToken);
        user.get().setIpAddress(userIdent);
        user
            .get()
            .setValidTill(LocalDateTime.now().plusHours(COOKIE_MAX_AGE_H));
        this.userCredentialRepository.save(user.get());

        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header("Authorization", "Bearer " + accToken)
            .body(
                this.objectMapper.writeValueAsString(
                        this.unifidHandler.makResponse(false, null, false, null)
                    )
            );
    }

    public ResponseEntity<String> logout(HttpServletRequest request)
        throws JsonProcessingException {
        String cookieName = "refreshToken";
        String token = Arrays.stream(request.getCookies())
            .filter(cookie -> cookieName.equals(cookie.getName()))
            .findFirst()
            .get()
            .getValue();

        if (token != null) {
            Optional<UserCredential> user =
                userCredentialRepository.findByToken(token);
            if (user.isPresent()) {
                UserCredential userCredential = user.get();
                userCredential.setToken("");
                userCredential.setIpAddress(getIden(request));
                userCredential.setLastLogin(LocalDateTime.now());

                Event event = Event.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .userId(user.get().getId())
                    .eventType(EventType.LOGOUT)
                    .additionalData(
                        Map.of(
                            ResponseKey.DATA.toString(),
                            Map.of(
                                "usreData",
                                this.objectMapper.writeValueAsString(user.get())
                            )
                        )
                    )
                    .build();

                this.eventProducer.publishLogOutEvent(event);
                userCredentialRepository.save(user.get());
            }
        }

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
                this.objectMapper.writeValueAsString(
                        this.unifidHandler.makResponse(false, null, false, null)
                    )
            );
    }

    private String getIden(ServletRequest req) {
        return new StringBuilder("The user hostName : ")
            .append(req.getRemoteHost())
            .append(" The user ip address : ")
            .append(req.getRemoteAddr())
            .toString();
    }

    public UserCredential createOrUpdateOAuth2User(
        String email,
        String name,
        String providerId
    ) {
        Optional<UserCredential> existingUser =
            this.userCredentialRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // Update existing user with OAuth info if needed
            UserCredential user = existingUser.get();
            user.setOauth2ProviderId(providerId);
            return this.userCredentialRepository.save(user);
        } else {
            // Create new user from OAuth data
            UserCredential newUser = new UserCredential();
            newUser.setEmail(email);
            newUser.setOauth2ProviderId(providerId);
            newUser.setEnabled(true); // OAuth users are pre-verified
            return userCredentialRepository.save(newUser);
        }
    }
}
