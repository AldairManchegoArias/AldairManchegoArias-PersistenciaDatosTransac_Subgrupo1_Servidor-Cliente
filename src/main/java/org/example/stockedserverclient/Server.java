package org.example.stockedserverclient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server class that manages socket connections for the chat application.
 * Handles client connections and creates a new thread for each client.
 */
public class Server {

    private ServerSocket serverSocket;

    /**
     * Constructor that initializes the server with a server socket.
     *
     * @param serverSocket The server socket to use for accepting client connections
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Starts the server and continuously listens for client connections.
     * Creates a new ClientHandler for each connected client in a separate thread.
     */
    public void startServer() {
        System.out.println("Servidor escuchando en puerto 1234...");
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    /**
     * Closes the server socket if it exists.
     * Called when an IOException occurs or when the server needs to shut down.
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
