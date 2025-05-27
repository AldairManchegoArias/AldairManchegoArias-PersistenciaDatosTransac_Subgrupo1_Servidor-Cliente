package org.example.stockedserverclient;

import java.net.ServerSocket;

public class ServerSocketLauncher {
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
