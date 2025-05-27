package org.example.stockedserverclient;

import java.net.ServerSocket;

/**
 * Utility class that encapsulates the server startup process.
 * Provides a static method to create and start the chat server.
 */
public class ServerSocketLauncher {
    /**
     * Creates a server socket on port 1234, initializes a Server instance,
     * and starts the server to listen for client connections.
     */
    public static void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
