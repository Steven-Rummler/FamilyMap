import com.sun.net.httpserver.HttpServer;
import handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class FamilyMapServer {
    /**
     * Starts a server on port 8080 or a provided custom port
     * @param args Optional, the port to use
     */
    public static void main(String[] args) {
        FamilyMapServer server = new FamilyMapServer();
        try {
            int port = 8080;
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
            server.startServer(port);
        } catch (IOException e) {
            System.out.println("Server failed to start.");
        }
    }

    /**
     * Opens a socket to listen on the provided port
     * @param port the port to run the server on
     * @throws IOException
     */
    private void startServer(int port) throws IOException {
        InetSocketAddress serverAddress = new InetSocketAddress(port);
        HttpServer server = HttpServer.create(serverAddress, 10);
        registerHandlers(server);
        server.start();
        System.out.println("FamilyMapServer listening on port " + port);
    }

    /**
     * Registers handlers for all of the available paths
     * @param server the server on which to register the handlers
     */
    private void registerHandlers(HttpServer server) {
        server.createContext("/", new FileHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill/", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/user/login", new LoginHandler());
        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/event/", new EventHandler());
        server.createContext("/event", new EventsHandler());
        server.createContext("/person/", new PersonHandler());
        server.createContext("/person", new PersonsHandler());
    }
}
