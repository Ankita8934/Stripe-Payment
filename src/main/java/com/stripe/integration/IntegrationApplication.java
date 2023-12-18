package com.stripe.integration;
import com.stripe.integration.controller.Webhook;
import com.stripe.integration.service.PaymentService;
import com.sun.net.httpserver.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetSocketAddress;

import static spark.route.HttpMethod.post;


@SpringBootApplication(scanBasePackages = "com.stripe.integration")
public class IntegrationApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(IntegrationApplication.class, args);
		int port = 8097;  // Set the port for your server

		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/webhookHandle", new Webhook());
		server.setExecutor(null); // creates a default executor

		System.out.println("Server is running on port " + port);
		server.start();

}}
