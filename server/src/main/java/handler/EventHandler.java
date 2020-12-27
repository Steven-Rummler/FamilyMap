package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.StringJsonHelper;
import model.AuthToken;
import result.EventResult;
import service.EventService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class EventHandler extends AuthenticatedHandler implements HttpHandler {
    /**
     * Calls EventService to retrieve the requested Event
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            EventResult eventResult = new EventResult();
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
                String eventID = exchange.getRequestURI().getPath().substring(7);
                EventService eventService = new EventService();
                eventResult = eventService.event(eventID, authToken);
                if (eventResult.success) {
                    exchange.getResponseHeaders().add("Authorization", token);
                } else {
                    validRequest = false;
                }
            } else {
                eventResult.message = "error: Invalid request";
                eventResult.success = false;
            }

            // Send a response
            if (validRequest) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
            String responseData = StringJsonHelper.serialize(eventResult);
            OutputStream responseBody = exchange.getResponseBody();
            StringJsonHelper.writeString(responseData, responseBody);
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.close();
        }
    }
}