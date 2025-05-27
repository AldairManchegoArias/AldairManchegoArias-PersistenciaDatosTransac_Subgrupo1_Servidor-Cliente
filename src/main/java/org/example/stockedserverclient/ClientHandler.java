package org.example.stockedserverclient;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles client connections to the chat server.
 * Manages communication between clients, sending private and broadcast messages
 * and maintains an updated list of connected users.
 */
public class ClientHandler implements Runnable {

    public static Map<String, ClientHandler> clientMap = new HashMap<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    private boolean isClosed = false;

    /**
     * Constructor that initializes a new client handler.
     * Sets up input/output streams, registers the client in the client map,
     * updates the user interface and starts a new thread to handle client messages.
     *
     * @param socket The connection socket with the client
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.username = reader.readLine();
            clientMap.put(username, this);

            HelloController.addLabel("Connected: " + username, HelloController.vboxStatic);
            HelloController.updateStatus("Connected: " + username);
            HelloController.updateUserList(clientMap.keySet());

            sendUserListToAll(); // updates all clients

            broadcast("Servidor: " + username + " se ha unido al chat");

            new Thread(this).start();

        } catch (IOException e) {
            closeEverything();
        }
    }

    /**
     * Implementation of the run method from the Runnable interface.
     * Handles continuous reading of client messages, processes private messages
     * and detects disconnection commands ("chao").
     */
    @Override
    public void run() {
        try {
            while (socket != null && !socket.isClosed()) {
                String message = reader.readLine();

                if (message == null) {
                    Thread.sleep(100);
                    continue;
                }

                if (message.startsWith("@")) {
                    int separator = message.indexOf(":");
                    if (separator != -1) {
                        String recipient = message.substring(1, separator);
                        String msgContent = message.substring(separator + 1).trim();

                        // Detectar desconexión con "chao"
                        if (msgContent.equalsIgnoreCase("chao")) {
                            broadcast("Servidor: " + username + " ha salido del chat");
                            closeEverything();
                            break;
                        }

                        sendPrivateMessage(recipient, username + ": " + msgContent);
                    }
                }

                // Additional option to detect direct "chao" without @user (optional)
                else if (message.equalsIgnoreCase("chao")) {
                    broadcast("Servidor: " + username + " ha salido del chat");
                    closeEverything();
                    break;
                }

            }
        } catch (IOException | InterruptedException e) {
            // Silence unexpected disconnection
        }
    }

    /**
     * Sends a private message to a specific client.
     * If the recipient doesn't exist, notifies the sender.
     *
     * @param recipient The username of the recipient
     * @param message The message to send
     */
    public void sendPrivateMessage(String recipient, String message) {
        ClientHandler client = clientMap.get(recipient);
        try {
            if (client != null) {
                client.writer.write(message);
                client.writer.newLine();
                client.writer.flush();
            } else {
                writer.write("Usuario no encontrado: " + recipient);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    /**
     * Sends a message to all connected clients except the sender.
     * Used for general announcements and notifications in the chat system.
     *
     * @param message The message to broadcast to all clients
     */
    public void broadcast(String message) {
        for (ClientHandler client : clientMap.values()) {
            try {
                if (!client.username.equals(this.username)) {
                    client.writer.write(message);
                    client.writer.newLine();
                    client.writer.flush();
                }
            } catch (IOException e) {
                client.closeEverything();
            }
        }
    }

    /**
     * Sends an updated list of all connected users to all clients.
     * Allows client interfaces to maintain an updated list of available users.
     * The message format is "USERLIST:user1,user2,user3,"
     */
    public void sendUserListToAll() {
        StringBuilder users = new StringBuilder("USERLIST:");
        for (String user : clientMap.keySet()) {
            users.append(user).append(",");
        }
        String message = users.toString();
        for (ClientHandler client : clientMap.values()) {
            try {
                client.writer.write(message);
                client.writer.newLine();
                client.writer.flush();
            } catch (IOException e) {
                client.closeEverything();
            }
        }
    }

    /**
     * Closes all connections and resources associated with this client.
     * Removes the client from the client map, updates the user interface,
     * notifies other clients about the disconnection and closes all streams and the socket.
     */
    public void closeEverything() {
        if (isClosed) return;
        isClosed = true;

        try {
            clientMap.remove(username);
            HelloController.addLabel("Desconectado: " + username, HelloController.vboxStatic);
            HelloController.updateStatus("Desconectado: " + username);
            HelloController.updateUserList(clientMap.keySet());

            sendUserListToAll(); // updates list for other clients

            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
