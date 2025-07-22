package com.themoneywallet.transcationservice.controller;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.themoneywallet.transcationservice.jaxb.generated.DonationRequestType;
import com.themoneywallet.transcationservice.jaxb.generated.DonationResponseType;

@Endpoint
public class TransactionSoapController {


        @PayloadRoot(namespace = "http://myapp.com/", localPart = "DonationRequest")
        @ResponsePayload
        public DonationResponseType getTransaction(@RequestPayload DonationRequestType request) {

            DonationResponseType resp = new DonationResponseType();
            resp.setId(100000);
            return resp;
        }

}
