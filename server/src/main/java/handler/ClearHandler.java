package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.StringJsonHelper;
import result.ClearResult;
import service.ClearService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class ClearHandler implements HttpHandler {
    /**
     * Calls ClearService to clear the database
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            ClearService clear = new ClearService();
            ClearResult clearResult = clear.clear();
            String responseData = StringJsonHelper.serialize(clearResult);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream responseBody = exchange.getResponseBody();
            StringJsonHelper.writeString(responseData, responseBody);
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
        exchange.close();
    }
}
