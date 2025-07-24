package com.themoneywallet.transcationservice.controller;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.themoneywallet.transcationservice.utilites.DateTimeUtil;

import generated.ChargeRequest;
import generated.ChargeResponse;
import generated.ObjectFactory;

 
@Endpoint
public class ChargeTransactionEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/transactions";
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ChargeRequest")
    @ResponsePayload
    public ChargeResponse handleCharge(@RequestPayload ChargeRequest request) {
        // Process business logic
        ChargeResponse response = new ObjectFactory().createChargeResponse();

        response.setTransactionId("txn_" + System.currentTimeMillis());
        response.setStatus("succeeded");
        response.setAmount(request.getAmount());
        response.setCurrency(request.getCurrency());
       response.setChargedAt(DateTimeUtil.nowAsXmlGregorianCalendar());
        response.setReceiptUrl("https://example.com/receipts/" + response.getTransactionId());

        return response;
    }
}
