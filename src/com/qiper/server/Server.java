package com.qiper.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private ServerSocket serverSocket;
    private final List<ClientHandler> loggedUser;
    private final AuthService authService;

    public Server() {
        authService = new AuthService();
        loggedUser = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(8888);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized void subscribe(ClientHandler user) {
        loggedUser.add(user);
    }

    public synchronized void unsubscribe(ClientHandler user) {
        loggedUser.remove(user);
    }

    public synchronized boolean isNotUserOccupied(String name) {
        return !isUserOccupied(name);
    }

    public synchronized boolean isUserOccupied(String name) {
        /**
         for(ClineHandler user : loggedUser) {
         if (user.getName().equals(user)) {
         return true;
         }
         }
         return false;
         */

        /**
         loggedUser.stream()
         .filter(u -> u.getName().equals(u))
         .findFirst()
         .isPresent();
         */

        return loggedUser.stream()
                .anyMatch(u -> u.getName().equals(name));

    }

    public synchronized void broadcastMessage(String outboundMessage) {
        /**
         for (ClientHandler user: loggedUser) {
         user.sendMessage(outboundMessage);
         }
         */

        /**
         loggedUser.forEach(new Consumer<ClientHandler>() {
        @Override
        public void accept(ClientHandler clientHandler) {
        clientHandler.sendMessage(outboundMessage);
        }
        });
         */

        loggedUser.forEach(clientHandler -> clientHandler.sendMessage(outboundMessage));
    }

}