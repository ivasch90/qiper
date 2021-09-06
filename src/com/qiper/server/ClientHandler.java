package com.qiper.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler {

    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private final Socket socket;
    private final int SEC_TIMEOUT = 120000;
    TaskTimer Timeout = new TaskTimer();
    Timer timer = new Timer();


    public ClientHandler(Server server, Socket socket) {

        this.socket = socket;
        try {
            this.server = server;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    doAuthentication();
                    listenMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            })
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong during client establishing...", e);
        }
    }

    private void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMessage(String.format("User[%s] is out.", name));

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    private class TaskTimer extends TimerTask {

        @Override
        public void run() {
            sendMessage("Auth timeout. Connection close...");
            closeConnection();
        }
    }

    private void doAuthentication() throws IOException {
        sendMessage("Greeting you in the Outstanding Chat.");
        sendMessage("Please do authentication in " + SEC_TIMEOUT/1000 + " sec!!!");
        sendMessage("Template is: -auth [login] [password]");
        timer.schedule(Timeout, SEC_TIMEOUT);

        while (true) {

            String maybeCredentials = in.readUTF();
            /** sample: -auth login1 password1 */
            if (maybeCredentials.startsWith("-auth")) {
                String[] credentials = maybeCredentials.split("\\s");

                Optional<AuthService.Entry> maybeUser = server.getAuthService()
                        .findUserByLoginAndPassword(credentials[1], credentials[2]);

                if (maybeUser.isPresent()) {
                    AuthService.Entry user = maybeUser.get();
                    if (server.isNotUserOccupied(user.getName())) {
                        name = user.getName();
                        sendMessage("AUTH OK.");
                        sendMessage("Welcome.");
                        server.broadcastMessage(String.format("User[%s] entered chat.", name));
                        server.subscribe(this);
                        timer.cancel();

                        return;

                    } else {

                        sendMessage("Current user is already logged in");
                    }
                } else {

                    sendMessage("Invalid credentials.");
                }

            } else {

                sendMessage("Invalid auth operation");
            }
        }
    }

    public void sendMessage(String outboundMessage) {
        try {
            out.writeUTF(outboundMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenMessages() throws IOException {
        while (true) {
            String inboundMessage = in.readUTF();
            if (inboundMessage.equals("-exit")) {
                break;
            }
            server.broadcastMessage(inboundMessage);
        }
    }

}