package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;

public class FileHandler implements HttpHandler {
    /**
     * Standard file handler for web pages
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().toUpperCase().equals("GET")) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
        } else {
            String urlPath = exchange.getRequestURI().toString();
            if (urlPath == null || urlPath.equals("/")) {
                urlPath = "/index.html";
            }
            String filePath = "web" + urlPath;
            File file = new File(filePath);
            if (file.exists()) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream responseBody = exchange.getResponseBody();
                Files.copy(file.toPath(), responseBody);
                responseBody.close();
            } else {
                file = new File("web/HTML/404.html");
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                OutputStream responseBody = exchange.getResponseBody();
                Files.copy(file.toPath(), responseBody);
                responseBody.close();
            }
        }
    }
}
