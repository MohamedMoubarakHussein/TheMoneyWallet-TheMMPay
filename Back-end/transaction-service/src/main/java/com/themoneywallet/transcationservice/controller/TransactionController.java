package com.themoneywallet.transcationservice.controller;

import com.themoneywallet.transcationservice.dto.request.GetTransactionDetails;
import com.themoneywallet.transcationservice.dto.request.TransferCreationRequest;
import com.themoneywallet.transcationservice.service.TransactionService;
import com.themoneywallet.transcationservice.utilites.UnifidResponseHandler;
import com.themoneywallet.transcationservice.utilites.ValidationErrorMessageConverter;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    private final ValidationErrorMessageConverter VErrorConverter;
    private final UnifidResponseHandler uResponseHandler;

    @PostMapping("/transfer")
    public ResponseEntity<String> CreateTransferTransaction(
        @Valid @RequestBody(required = false) TransferCreationRequest transReq,
        BindingResult result,
        @RequestHeader("Authorization") String token    ) {
        if (transReq == null) return ResponseEntity.badRequest()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "error",
                            Map.of(
                                "message",
                                "Required request body is missing"
                            )
                        ),
                        true,
                        "TR003"
                    ).toString()
            );

        if (result.hasErrors()) return ResponseEntity.badRequest()
            .body(this.VErrorConverter.Convert(result));

        return this.transactionService.createTransfer(transReq);
    }

    // the differnce between pay and  transfer end points is that pay uses external api
    @PostMapping("/pay")
    public ResponseEntity<String> CreatePaymentTransaction(
        @Valid @RequestBody(required = false) TransferCreationRequest transReq,
        BindingResult result,
        @RequestHeader("Authorization") String token    ) {
        if (transReq == null) return ResponseEntity.badRequest()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "error",
                            Map.of(
                                "message",
                                "Required request body is missing"
                            )
                        ),
                        true,
                        "TR003"
                    ).toString()
            );

        if (result.hasErrors()) return ResponseEntity.badRequest()
            .body(this.VErrorConverter.Convert(result));

        return this.transactionService.createTransfer(transReq);
    }

    @GetMapping("/getall")
    public ResponseEntity<String> getAllTransactions(
        @Valid @RequestBody(required = false) GetTransactionDetails transReq,
        @RequestHeader("Authorization") String token    ) {
        return this.transactionService.getAllTransactions(transReq.getId());
    }

    @GetMapping("/get")
    public ResponseEntity<String> getTransaction(
        @Valid @RequestBody(required = false) GetTransactionDetails transReq,
        @RequestHeader("Authorization") String token,
        @RequestParam("id") String id
    ) {
        return this.transactionService.getTransaction(transReq.getId(), id);
    }
}
