package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.StringJsonHelper;
import model.AuthToken;
import result.EventsResult;
import service.EventsService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class EventsHandler extends AuthenticatedHandler implements HttpHandler {
    /**
     * Calls the EventsService to retrieve all events for the current user
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            EventsResult eventsResult = new EventsResult();
            boolean validRequest = false;
            String token = null;
            AuthToken authToken = null;

            // Check if the request and auth token are valid
            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                Headers requestHeaders = exchange.getRequestHeaders();
                if (requestHeaders.containsKey("Authorization")) {
                    token = requestHeaders.get("Authorization").get(0);
                    authToken = authenticate(token);
                    if (authToken != null) {
                        validRequest = true;
                    }
                }
            }

            // Call the service or build an error response
            if (validRequest) {
                EventsService eventsService = new EventsService();
                eventsResult = eventsService.events(authToken);
                if (eventsResult.success) {
                    exchange.getResponseHeaders().add("Authorization", token);
                } else {
                    validRequest = false;
                }
            } else {
                eventsResult.message = "error: Invalid request";
                eventsResult.success = false;
            }

            // Send a response
            if (validRequest) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
            String responseData = StringJsonHelper.serialize(eventsResult);
            OutputStream responseBody = exchange.getResponseBody();
            StringJsonHelper.writeString(responseData, responseBody);
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.close();
        }
    }
}