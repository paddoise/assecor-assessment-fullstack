package org.example.API;

import com.sun.net.httpserver.HttpServer;
import org.example.DataService.DataService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class API {
    private static final Logger LOGGER = Logger.getLogger(API.class.getName());
    private final DataService dataService;

    public API (DataService dataService) {
        this.dataService = dataService;

        this.startServer();
    }

    private void startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new RestHandler(this.dataService));
            server.setExecutor(null);
            server.start();
            LOGGER.log(Level.INFO, "\u001B[32m" + "Server started on port 8080" + "\u001B[0m");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not start server.", e);
        }
    }
}
