package com.themoneywallet.transactionservice.controller;

import com.themoneywallet.transactionservice.Exception.InvalidTransactionException;
import com.themoneywallet.transactionservice.Exception.TransactionNotFoundException;
import com.themoneywallet.transactionservice.dto.request.TransactionRequest;
import com.themoneywallet.transactionservice.dto.response.TransactionResponse;
import com.themoneywallet.transactionservice.dto.status.TransactionStatusResponse;
import com.themoneywallet.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction", description = "Transaction management APIs")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Initiate a new transaction",
               description = "Initiates a financial transaction (e.g., transfer, deposit, withdrawal) and publishes an event for asynchronous processing.",
               requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Transaction initiation request",
                                                                                required = true,
                                                                                content = @Content(mediaType = "application/json",
                                                                                                    schema = @Schema(implementation = TransactionRequest.class))),
               responses = {
                   @ApiResponse(responseCode = "202", description = "Transaction initiated successfully",
                                content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = TransactionResponse.class))),
                   @ApiResponse(responseCode = "400", description = "Invalid transaction request",
                                content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = Map.class))),
                   @ApiResponse(responseCode = "500", description = "Internal server error",
                                content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = Map.class)))
               })
    @PostMapping("/initiate")
    public ResponseEntity<Object> initiateTransaction(@Valid @RequestBody TransactionRequest request) {
        log.info("Received request to initiate transaction: {}", request);
        try {
            TransactionResponse response = transactionService.initiateTransaction(request);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (InvalidTransactionException e) {
            log.error("Invalid transaction request: {}", e.getMessage());
            Map<String, String> errorResponse = Collections.singletonMap("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error initiating transaction: {}", e.getMessage(), e);
            Map<String, String> errorResponse = Collections.singletonMap("message", "Internal server error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get transaction status by ID",
               description = "Retrieves the current status and details of a specific transaction.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Transaction status retrieved successfully",
                                content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = TransactionStatusResponse.class))),
                   @ApiResponse(responseCode = "404", description = "Transaction not found",
                                content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = Map.class))),
                   @ApiResponse(responseCode = "500", description = "Internal server error",
                                content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = Map.class)))
               })
    @GetMapping("/{transactionId}/status")
    public ResponseEntity<Object> getTransactionStatus(@Parameter(description = "ID of the transaction to retrieve status for", required = true) @PathVariable String transactionId) {
        log.info("Received request to get status for transaction ID: {}", transactionId);
        try {
            TransactionStatusResponse response = transactionService.getTransactionStatus(transactionId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TransactionNotFoundException e) {
            log.error("Transaction not found: {}", e.getMessage());
            Map<String, String> errorResponse = Collections.singletonMap("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error retrieving transaction status: {}", e.getMessage(), e);
            Map<String, String> errorResponse = Collections.singletonMap("message", "Internal server error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
