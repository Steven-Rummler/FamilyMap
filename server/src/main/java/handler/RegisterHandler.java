package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.AuthToken;
import request.RegisterRequest;
import result.EventsResult;
import result.RegisterResult;
import helper.StringJsonHelper;
import service.RegisterService;

import java.io.*;
import java.net.HttpURLConnection;

public class RegisterHandler implements HttpHandler {
    /**
     * Calls RegisterService to register the user with the provide details
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            RegisterResult registerResult = new RegisterResult();
            boolean validRequest = false;

            // Check if the request is valid
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                validRequest = true;
            }

            // Call the service or build an error response
            if (validRequest) {
                // Read the posted data
                InputStream requestBody = exchange.getRequestBody();
                String requestData = StringJsonHelper.readString(requestBody);
                RegisterRequest registerRequest = StringJsonHelper.deserialize(requestData, RegisterRequest.class);

                // Call the service
                RegisterService registerService = new RegisterService();
                registerResult = registerService.register(registerRequest);
                if (registerResult.success) {
                    exchange.getResponseHeaders().add("Authorization", registerResult.authToken);
                } else {
                    validRequest = false;
                }
            } else {
                registerResult.message = "error: Invalid request";
                registerResult.success = false;
            }

            // Send a response
            if (validRequest) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
            String responseData = StringJsonHelper.serialize(registerResult);
            OutputStream responseBody = exchange.getResponseBody();
            StringJsonHelper.writeString(responseData, responseBody);
            exchange.close();
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.close();
        }
    }
}
