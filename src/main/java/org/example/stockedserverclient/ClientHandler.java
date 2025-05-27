package org.example.stockedserverclient;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

    public static Map<String, ClientHandler> clientMap = new HashMap<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    private boolean isClosed = false;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.username = reader.readLine();
            clientMap.put(username, this);

            HelloController.addLabel("Conectado: " + username, HelloController.vboxStatic);
            HelloController.updateStatus("Conectado: " + username);
            HelloController.updateUserList(clientMap.keySet());

            sendUserListToAll(); // actualiza todos los clientes

            broadcast("Servidor: " + username + " se ha unido al chat");

            new Thread(this).start();

        } catch (IOException e) {
            closeEverything();
        }
    }

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

                // Opción adicional para detectar "chao" directo sin @usuario (opcional)
                else if (message.equalsIgnoreCase("chao")) {
                    broadcast("Servidor: " + username + " ha salido del chat");
                    closeEverything();
                    break;
                }

            }
        } catch (IOException | InterruptedException e) {
            // Silenciar desconexión inesperada
        }
    }

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

    public void closeEverything() {
        if (isClosed) return;
        isClosed = true;

        try {
            clientMap.remove(username);
            HelloController.addLabel("Desconectado: " + username, HelloController.vboxStatic);
            HelloController.updateStatus("Desconectado: " + username);
            HelloController.updateUserList(clientMap.keySet());

            sendUserListToAll(); // actualiza lista en los demás clientes

            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}