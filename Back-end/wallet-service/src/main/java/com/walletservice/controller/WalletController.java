package com.walletservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletservice.dto.request.WalletChangeFundReq;
import com.walletservice.dto.request.WalletCreationRequest;
import com.walletservice.dto.response.UnifiedResponse;
import com.walletservice.entity.WalletLimits;
import com.walletservice.entity.fixed.ResponseKey;
import com.walletservice.service.WalletService;
import com.walletservice.utilites.UnifidResponseHandler;
import com.walletservice.utilites.ValidtionRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;
    private final ValidtionRequestHandler VErrorConverter;
    private final UnifidResponseHandler uResponseHandler;
    private final ObjectMapper objectMapper;

  
    @PostMapping("/create")
    public ResponseEntity<UnifiedResponse> createWallet(
        @Valid @RequestBody WalletCreationRequest wallet,
        BindingResult result,
        @RequestHeader("Authorization") String token
    )  {
         if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.VErrorConverter.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.walletService.createWallet(wallet, token);
    }

    @GetMapping("/wallets")
    public ResponseEntity<String> getAllWallets(
        @RequestHeader("Authorization") String token
    ) throws JsonProcessingException {
        return this.walletService.getAllWallets(
                this.walletService.getUserId(token)
            );
    }

    @GetMapping("/get")
    public ResponseEntity<String> getWallet(
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) throws JsonProcessingException {
        return this.walletService.getWallet(
                this.walletService.getUserId(token),
                id
            );
    }

    @GetMapping("/status")
    public ResponseEntity<String> walletStatus(
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) throws JsonProcessingException {
        return this.walletService.getWalletStaus(
                this.walletService.getUserId(token),
                id
            );
    }

    @PostMapping("/status")
    public ResponseEntity<String> walletStatusChange(
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) throws JsonProcessingException {
        return this.walletService.setWalletStaus(
                this.walletService.getUserId(token),
                id
            );
    }

    @GetMapping("/balance")
    public ResponseEntity<String> getWalletBlance(
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) throws JsonProcessingException {
        return this.walletService.getWalletBalance(
                this.walletService.getUserId(token),
                id
            );
    }

    @GetMapping("/limits")
    public ResponseEntity<String> getWalletLimits(
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) throws JsonProcessingException {
        return this.walletService.getWalletLimits(
                this.walletService.getUserId(token),
                id
            );
    }

    @PatchMapping("/limits")
    @PreAuthorize("@mySecurity.checkCustomAccess()")
    public ResponseEntity<String> updateWalletLimits(
        @Valid @RequestBody(required = false) WalletLimits wallet,
        BindingResult result,
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) throws JsonProcessingException {
        if (wallet == null) return ResponseEntity.badRequest()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                true,
                                Map.of(
                                    ResponseKey.ERROR.toString(),
                                    Map.of(
                                        "message",
                                        "Required request body is missing"
                                    )
                                ),
                                true,
                                "WA003"
                            )
                    )
            );

        if (result.hasErrors()) return ResponseEntity.badRequest()
            .body(
                this.objectMapper.writeValueAsString(
                        this.VErrorConverter.handle(result)
                    )
            );

        String userId;
        String refCookie;
        try {
            userId = this.walletService.getUserId(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(
                    this.objectMapper.writeValueAsString(
                            this.uResponseHandler.makResponse(
                                    true,
                                    Map.of(
                                        ResponseKey.ERROR.toString(),
                                        Map.of(
                                            "message",
                                            "Required request body is missing"
                                        )
                                    ),
                                    true,
                                    "WA003"
                                )
                        )
                );
        }

        return this.walletService.UpdateWalletLimits(wallet, userId, id);
    }

    @PatchMapping("/addfund")
    public ResponseEntity<String> addWalletFunds(
        @Valid @RequestBody(required = false) WalletChangeFundReq req,
        BindingResult result,
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) throws JsonProcessingException {
        if (req == null) return ResponseEntity.badRequest()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                true,
                                Map.of(
                                    ResponseKey.ERROR.toString(),
                                    Map.of(
                                        "message",
                                        "Required request body is missing"
                                    )
                                ),
                                true,
                                "WA003"
                            )
                    )
            );

        if (result.hasErrors()) return ResponseEntity.badRequest()
            .body(
                this.objectMapper.writeValueAsString(
                        this.VErrorConverter.handle(result)
                    )
            );

        String userId;
        try {
            userId = this.walletService.getUserId(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(
                    this.objectMapper.writeValueAsString(
                            this.uResponseHandler.makResponse(
                                    true,
                                    Map.of(
                                        ResponseKey.ERROR.toString(),
                                        Map.of(
                                            "message",
                                            "Required request body is missing"
                                        )
                                    ),
                                    true,
                                    "WA003"
                                )
                        )
                );
        }

        return this.walletService.addfund(req, userId, id);
    }

    @PatchMapping("/rmfund")
    public ResponseEntity<String> rmWalletFunds(
        @Valid @RequestBody(required = false) WalletChangeFundReq req,
        BindingResult result,
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) throws JsonProcessingException {
        if (req == null) return ResponseEntity.badRequest()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                true,
                                Map.of(
                                    ResponseKey.ERROR.toString(),
                                    Map.of(
                                        "message",
                                        "Required request body is missing"
                                    )
                                ),
                                true,
                                "WA003"
                            )
                    )
            );

        if (result.hasErrors()) return ResponseEntity.badRequest()
            .body(
                this.objectMapper.writeValueAsString(
                        this.VErrorConverter.handle(result)
                    )
            );

        String userId;
        try {
            userId = this.walletService.getUserId(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(
                    this.objectMapper.writeValueAsString(
                            this.uResponseHandler.makResponse(
                                    true,
                                    Map.of(
                                        ResponseKey.ERROR.toString(),
                                        Map.of(
                                            "message",
                                            "Required request body is missing"
                                        )
                                    ),
                                    true,
                                    "WA003"
                                )
                        )
                );
        }

        return this.walletService.rmfund(req, userId, id);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> walletDelete(
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) throws JsonProcessingException {
        return this.walletService.Delete(
                this.walletService.getUserId(token),
                id
            );
    }
}
