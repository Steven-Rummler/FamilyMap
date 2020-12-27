package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.StringJsonHelper;
import model.AuthToken;
import request.FillRequest;
import request.RegisterRequest;
import result.EventsResult;
import result.FillResult;
import service.FillService;
import service.RegisterService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class FillHandler implements HttpHandler {
    /**
     * Calls FillService to create the persons and events for the given user up to the given number of generations
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            FillResult fillResult = new FillResult();
            boolean validRequest = false;

            String username = exchange.getRequestURI().getPath().substring(6);
            int generations = 4;
            if (username.contains("/")) {
                String[] parts = username.split("/");
                username = parts[0];
                generations = Integer.parseInt(parts[1]);
            }

            // Check if the request is valid
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                if (generations >= 0 && generations < 10) {
                    validRequest = true;
                }
            }

            // Call the service or build an error response
            if (validRequest) {
                // Call the service
                FillRequest fillRequest = new FillRequest();
                fillRequest.user = username;
                fillRequest.generations = generations;
                FillService fillService = new FillService();
                fillResult = fillService.fill(fillRequest);
                validRequest = fillResult.success;
            } else {
                fillResult.message = "error: Invalid request";
                fillResult.success = false;
            }

            // Send a response
            if (validRequest) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
            String responseData = StringJsonHelper.serialize(fillResult);
            OutputStream responseBody = exchange.getResponseBody();
            StringJsonHelper.writeString(responseData, responseBody);
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.close();
        }
    }
}
