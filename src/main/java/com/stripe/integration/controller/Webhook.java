package com.stripe.integration.controller;

import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.integration.entity.Numbers;
import com.stripe.integration.repository.NumberRep;
import com.stripe.integration.service.PaymentService;
import com.stripe.model.Event;
import com.sun.net.httpserver.HttpExchange;
import org.eclipse.jetty.http.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import spark.Request;
import spark.Response;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Webhook implements HttpHandler{
    @Autowired
    NumberRep numberRep;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("POST".equals(method)) {
            InputStream requestBody = exchange.getRequestBody();
            String payload = convertInputStreamToString(requestBody);

            String sigHeader = exchange.getRequestHeaders().getFirst("Stripe-Signature");

            // Your existing webhook handling logic...
            // Make sure to replace the following with your actual logic
            System.out.println("Received payload: " + payload);
            System.out.println("Stripe-Signature: " + sigHeader);

            // You may want to validate the signature and handle the payload here

            // Send a response
            String response = "Webhook received successfully";
            exchange.sendResponseHeaders(200, response.length());
            Numbers entity=new Numbers();
            entity.setAmount(entity.getAmount());
            entity.setAmount_captured(entity.getAmount_captured());
            entity.setDescription(entity.getDescription());
            entity.setType(entity.getDescription());
            numberRep.save(entity);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(405, 0); // Method Not Allowed
        }

}

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.append(new String(buffer, 0, length));
        }
        return result.toString();
    }


}
