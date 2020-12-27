package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.StringJsonHelper;
import request.LoginRequest;
import result.LoginResult;
import service.LoginService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class LoginHandler implements HttpHandler {
    /**
     * Calls LoginService to log in the user
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                InputStream requestBody = exchange.getRequestBody();
                String requestData = StringJsonHelper.readString(requestBody);
                LoginRequest loginRequest = StringJsonHelper.deserialize(requestData, LoginRequest.class);
                LoginService login = new LoginService();
                LoginResult loginResult = login.login(loginRequest);
                if (loginResult.success) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    responseHeaders.add("Authorization", loginResult.authToken);
                } else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }
                String responseData = StringJsonHelper.serialize(loginResult);
                OutputStream responseBody = exchange.getResponseBody();
                StringJsonHelper.writeString(responseData, responseBody);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }

        exchange.close();
    }
}
