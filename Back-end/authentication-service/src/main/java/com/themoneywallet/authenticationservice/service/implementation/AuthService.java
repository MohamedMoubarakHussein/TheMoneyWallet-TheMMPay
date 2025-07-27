package com.themoneywallet.authenticationservice.service.implementation;

import com.github.f4b6a3.uuid.UuidCreator;
import com.themoneywallet.authenticationservice.dto.request.AuthRequest;
import com.themoneywallet.authenticationservice.dto.request.ResetPassword;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.entity.UserCredential;
import com.themoneywallet.authenticationservice.repository.UserCredentialRepository;
import com.themoneywallet.authenticationservice.service.defintion.AuthServiceInterface;
import com.themoneywallet.sharedUtilities.dto.event.Event;
import com.themoneywallet.sharedUtilities.dto.event.UserCreationEvent;
import com.themoneywallet.sharedUtilities.dto.event.UserLogInEvent;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.enums.EventType;
import com.themoneywallet.sharedUtilities.enums.FixedInternalValues;
import com.themoneywallet.sharedUtilities.enums.ResponseKey;
import com.themoneywallet.sharedUtilities.enums.UserRole;
import com.themoneywallet.sharedUtilities.utilities.SerializationDeHalper;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.sharedUtilities.utilities.EventHandler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigInteger;
import java.security.SecureRandom;
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
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements  AuthServiceInterface {

    private final UserCredentialRepository  userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtRefService jwtRefService;
    private final AuthenticationManager authenticationManager;
    private final EventHandler eventHandler;
    private final UnifidResponseHandler unifidHandler;
    private final SerializationDeHalper serializationDeHelper;
    private final EventProducer eventProducer;

    private final static Integer COOKIE_MAX_AGE_H = 7;
    private final static String COOKIE_PATH = "/";
    private final static boolean IS_COOKIE_SECURE = false;
    private final static String COOKIE_SAME_SITE = "Lax";
    private final static boolean IS_COOKIE_HTTP_ONLY = true;
    private final static Integer EMAIL_TOKEN_VALID_TILL_M = 3 ;

    @Override
    @Transactional
    public ResponseEntity<UnifiedResponse> signUp(SignUpRequest signUpRequest,HttpServletRequest req) { 
        Map<String, Map<String, String>> data = new HashMap<>();
        if(isExistBeforeSignUp(data , signUpRequest)) {
            return this.unifidHandler.generateFailedResponse("error", data, "AUVD1003", "key(field name:String)->value(theError:String)" , HttpStatus.BAD_REQUEST);
        }
        UserCredential credential;
        try {
            credential = this.saveCredentialInDb(signUpRequest,req.getRemoteAddr());
        } catch (Exception e) {
            return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code:#AUDB0004. We apologize for the inconvenience.", "AUDB0004", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(!this.publishSignUpEvent(signUpRequest,credential)){
            return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return this.unifidHandler.generateSuccessResponse("data", credential, HttpStatus.CREATED, this.makeAuthHeaders(credential.getToken(), credential.getAccessToken()));
    }
    
    private UserCredential saveCredentialInDb(SignUpRequest signUpRequest,String userIdent){
        UUID userId = UuidCreator.getTimeOrderedEpoch();
        Map<String, String> tokenGenerator = this.tokenGenerator(signUpRequest.getUserName(),userId , UserRole.ROLE_USER.toString());
        UserCredential  credential = UserCredential.builder()
                        .userId(userId)
                        .email(signUpRequest.getEmail())
                        .userName(signUpRequest.getUserName())
                        .password(this.passwordEncoder.encode(signUpRequest.getPassword()))
                        .userRole(UserRole.ROLE_USER)
                        .locked(false)
                        .enabled(false)
                        .lastLogin(LocalDateTime.now())
                        .ipAddress(userIdent)
                        .emailTokenValidTill(LocalDateTime.now().plusMinutes(EMAIL_TOKEN_VALID_TILL_M))
                        .tokenValidTill(LocalDateTime.now().plusHours(COOKIE_MAX_AGE_H))
                        .revoked(false)
                        .emailVerificationToken(tokenGenerator.get("signUpToken"))
                        .token(tokenGenerator.get("refreshToken"))
                        .build();
        credential.setAccessToken(tokenGenerator.get("accessToken"));
        return this.userRepo.save(credential);    
    }

    @Override
    public boolean publishSignUpEvent(SignUpRequest request , UserCredential user){
        
        UserCreationEvent info = UserCreationEvent.builder()
                          .email(request.getEmail())
                          .userRole(user.getUserRole())
                          .userName(request.getUserName())
                          .firstName(request.getFirstName())
                          .lastName(request.getLastName())
                          .userId(user.getUserId().toString())
                          .emailVerficationCode(user.getEmailVerificationToken())
                          .build();

        String data = this.serializationDeHelper.serailization(info);
        if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
            return false;
        }
        try{
            Event event = this.eventHandler.makeEvent(EventType.AUTH_USER_SIGN_UP,info.getUserId(), "data", data);
            this.eventProducer.publishSignUpEvent(event);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    @Override
    public ResponseEntity<UnifiedResponse> signIn(AuthRequest authRequest,HttpServletRequest req){
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(),authRequest.getPassword()));
        } catch (Exception ex) {
            return this.unifidHandler.generateFailedResponse("error", "Wrong userName or password.", "AUCR1005", "string" ,  HttpStatus.BAD_REQUEST);
        }
        UserCredential user = handleLogin(authRequest , req.getRemoteAddr());
        if(!this.publishSigninEvent(user)){
            return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return this.unifidHandler.generateSuccessResponse("data", user, HttpStatus.CREATED, this.makeAuthHeaders(user.getToken(), user.getAccessToken()));
    }

    private UserCredential handleLogin(AuthRequest authRequest , String ip) {
        Optional<UserCredential> opUser = userRepo.findByUserName(authRequest.getUserName());
        UserCredential user = opUser.get();
        Map<String, String> tokenGenerator = tokenGenerator(user.getUsername(), user.getUserId(), user.getUserRole().toString());
        user.setToken(tokenGenerator.get("refreshToken"));
        user.setLastLogin(LocalDateTime.now());
        user.setIpAddress(ip);
        user.setTokenValidTill(LocalDateTime.now().plusHours(COOKIE_MAX_AGE_H));
        user.setRevoked(false);
        return this.userRepo.save(user);
    }

    @Override
    public boolean publishSigninEvent(UserCredential user){
        UserLogInEvent info = UserLogInEvent.builder()
                       .email(user.getEmail())
                       .userRole(user.getUserRole().toString())
                       .userId(user.getUserId().toString())
                       .build();
        String data = this.serializationDeHelper.serailization(info);
        if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
            return false;
        }
        try{
            Event event = this.eventHandler.makeEvent(EventType.AUTH_USER_LOGIN_SUCCESSED,info.getUserId(), "data", data);
            this.eventProducer.publishSigninEvent(event);
        }catch(Exception e){
            return false;
        }
        return true;
    }
 
    @Override
    public boolean validToken(String token) {
        return this.jwtService.isTokenValid(token);
    }

    @Override
    public ResponseEntity<UnifiedResponse> refreshToken(String refreshToken,HttpServletRequest req){
        Optional<UserCredential> opUser =this.userRepo.findByToken(refreshToken);
        UserCredential user;
        if (opUser.isPresent()) {
            user = opUser.get();
        }else{
            return this.unifidHandler.generateFailedResponse("error", "Invalid  Token", "AUTK1007", "string" ,  HttpStatus.UNAUTHORIZED);
        }
        if(user.isRevoked()){
            return this.unifidHandler.generateFailedResponse("error", "Invalid  Token", "AUTK1008", "string" ,  HttpStatus.UNAUTHORIZED);
        }
        Map<String, String> tokenGenerator = tokenGenerator(user.getUsername(), user.getUserId(), user.getUserRole().toString());
        user.setToken(tokenGenerator.get("refreshToken"));
        user.setTokenValidTill(LocalDateTime.now().plusHours(COOKIE_MAX_AGE_H));
        user.setIpAddress(req.getRemoteAddr());  
        user = this.userRepo.save(user);
        return this.unifidHandler.generateSuccessResponseNoBody("data", HttpStatus.OK, this.makeAuthHeaders(user.getToken(), tokenGenerator.get("accessToken")));
    }

    @Override
    public ResponseEntity<UnifiedResponse> logout(HttpServletRequest request){
        Optional<Cookie> cookiee = Arrays.stream(request.getCookies()).filter(cookie -> "refreshToken".equals(cookie.getName())).findFirst();
        String token;
        if(cookiee.isPresent()){
            token = cookiee.get().getValue();
        }else{
            SecurityContextHolder.clearContext();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "");
            return this.unifidHandler.generateSuccessResponseNoBody("data", HttpStatus.OK, headers);
        }
        Optional<UserCredential> user =userRepo.findByToken(token);
        if (user.isPresent()) {
            UserCredential userCredential = user.get();
            userCredential.setToken("");
            userCredential.setIpAddress(request.getRemoteAddr());
            userCredential.setLastLogin(LocalDateTime.now());
            try {
               Event  event = this.eventHandler.makeEvent(EventType.LOGOUT,userCredential.getUserId().toString(), "data", userCredential);
                this.eventProducer.publishLogOutEvent(event);
            } catch (Exception e) {   
            }
            userRepo.save(userCredential);
        }
        ResponseCookie cookie = ResponseCookie.from("refreshToken", null).httpOnly(IS_COOKIE_HTTP_ONLY).secure(IS_COOKIE_SECURE).sameSite(COOKIE_SAME_SITE).maxAge(0).path(COOKIE_PATH).build();
        SecurityContextHolder.clearContext();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return this.unifidHandler.generateSuccessResponseNoBody("data",HttpStatus.OK, headers);
    }

    @Override
    public ResponseEntity<UnifiedResponse> verfiyEmail(String code, String token) {
        UUID userId = this.jwtService.extractUserId(token);
        Optional<UserCredential> opUser = this.userRepo.findByUserId(userId);
        if(opUser.isPresent()){
            if(opUser.get().getEmailVerificationToken().equals(code) && opUser.get().getEmailTokenValidTill().isBefore(LocalDateTime.now())){
                return this.unifidHandler.generateSuccessResponseNoBody("data", HttpStatus.OK);
            }
        }
        return this.unifidHandler.generateFailedResponse("error", "Email verification faild.", "AUCR1009", "string" , HttpStatus.BAD_REQUEST);
    }

    @Override
    public boolean isExistBeforeSignUp(Map<String , Map<String,String>> mp , SignUpRequest request){
        boolean errorFlage = false;
        Map<String, String> data = new HashMap<>();
        Optional<UserCredential> userBeforeSignUp = this.userRepo.findByEmailOrUserName(request.getEmail() , request.getUserName());
        if(userBeforeSignUp.isPresent()){
            if (userBeforeSignUp.get().getEmail().equals(request.getEmail())) {
                data.put("email", "This Email address is used.");
                errorFlage = true;
            }
            if (userBeforeSignUp.get().getUsername().equals(request.getUserName())) {
                data.put("userName", "This userName is used.");
                errorFlage = true;
            }
            mp.put(ResponseKey.ERROR.toString(), data);
            mp.put(ResponseKey.INFO.toString(), Map.of("structure" , "This is an error that stored as  map (field) -> (validation error)"));
        }
        return errorFlage;
    }


    @Override
    public void revokeToken(String token){
        Optional<UserCredential> opUser = this.userRepo.findByToken(token);
        if(opUser.isPresent()){
            UserCredential userCredential = opUser.get();
            userCredential.setToken("");
            userCredential.setTokenValidTill(LocalDateTime.now());
            this.userRepo.save(userCredential);
        }
    }


    @Override
    public ResponseEntity<UnifiedResponse> resendEmailToken(String token) {
        Optional<UserCredential> opUser = this.userRepo.findByToken(token);
        if(opUser.isPresent()){
            UserCredential userCredential = opUser.get();
            String signUpToken =  tokenGenerator(userCredential.getUsername(), userCredential.getUserId(), userCredential.getUserRole().toString()).get("singUpToken");
            userCredential.setEmailVerificationToken(signUpToken);
            userCredential.setEmailTokenValidTill(LocalDateTime.now().plusMinutes(EMAIL_TOKEN_VALID_TILL_M));
            this.userRepo.save(userCredential);
            return this.unifidHandler.generateSuccessResponseNoBody("data", HttpStatus.OK);

        }
        return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUTK0010. We apologize for the inconvenience.", "AUTK0010", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<UnifiedResponse> resendForgetPasswordToken(String token) {
       Optional<UserCredential> opUser = this.userRepo.findByToken(token);
        if(opUser.isPresent()){
            UserCredential userCredential = opUser.get();
            String forgetPassword = tokenGenerator(userCredential.getUsername(), userCredential.getUserId(), userCredential.getUserRole().toString()).get("forgetPassword");
            userCredential.setPasswordResetToken(forgetPassword);
            this.userRepo.save(userCredential);
            return this.unifidHandler.generateSuccessResponseNoBody("data", HttpStatus.OK);
        }
        return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUTK0011. We apologize for the inconvenience.", "AUTK0011", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Override
    public ResponseEntity<UnifiedResponse> restPassword(String token, ResetPassword request) {
      Optional<UserCredential> opUser = this.userRepo.findByToken(token);
        if(opUser.isPresent()){
            UserCredential userCredential = opUser.get();
            if(request.getForgetPasswordToken().equals(userCredential.getPasswordResetToken())){
            userCredential.setPassword(passwordEncoder.encode(request.getPassword()));
            this.userRepo.save(userCredential);
            return this.unifidHandler.generateSuccessResponseNoBody("data", HttpStatus.OK);
            }
        }
        return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUTK0012. We apologize for the inconvenience.", "AUTK0012", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Map<String, String> tokenGenerator(String userName , UUID userId , String userRole){
        String accToken = this.jwtService.generateToken(userName ,userId, userRole);
        String refToken = this.jwtRefService.generateToken(userName, userId);
        String signUpToken =  new BigInteger(30, new SecureRandom()).toString(32).toUpperCase();
        String cookie = ResponseCookie.from("refToken", refToken).httpOnly(IS_COOKIE_HTTP_ONLY).secure(IS_COOKIE_SECURE).sameSite(COOKIE_SAME_SITE).maxAge(Duration.ofHours(COOKIE_MAX_AGE_H)).path(COOKIE_PATH).build().toString();
        return Map.of("accessToken" , accToken , "refreshToken" , refToken , "signUpToken" , signUpToken , "cookie" , cookie);
    }

    @Override
    public HttpHeaders  makeAuthHeaders(String cookie , String accToken){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie);
        headers.add("Authorization",  accToken);
        return headers;
    }
}
