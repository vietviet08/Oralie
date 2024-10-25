package com.oralie.accounts.providers;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CustomEventListenerProvider implements EventListenerProvider {

    private KeycloakSession session;

    private static final Logger log = LoggerFactory.getLogger(CustomEventListenerProvider.class);

    public CustomEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            String userId = event.getUserId();
            String userEmail = event.getDetails().get("email");
            String firstName = event.getDetails().get("firstName");
            String lastName = event.getDetails().get("lastName");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/store/accounts/register-keycloak"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{ \"userId\": \"" + userId + "\", \"email\": \"" + userEmail + "\" + \"firstName\": \"" + firstName + "\" + \"lastName\": \"" + lastName + "\" }"))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        log.info("User added: {}", response.body());
                    })
                    .exceptionally(error -> {
                        log.error("Error adding user: {}", error.getMessage());
                        return null;
                    });
        }
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {

    }
}
