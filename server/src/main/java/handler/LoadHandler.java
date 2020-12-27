package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.StringJsonHelper;
import request.LoadRequest;
import request.RegisterRequest;
import result.LoadResult;
import result.RegisterResult;
import service.LoadService;
import service.RegisterService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class LoadHandler implements HttpHandler {
    /**
     * Calls LoadService to load the given data into the database
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                InputStream requestBody = exchange.getRequestBody();
                String requestData = StringJsonHelper.readString(requestBody);
                LoadRequest loadRequest = StringJsonHelper.deserialize(requestData, LoadRequest.class);
                LoadService loadService = new LoadService();
                LoadResult loadResult = loadService.load(loadRequest);
                String responseData = StringJsonHelper.serialize(loadResult);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
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
