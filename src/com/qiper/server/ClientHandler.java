package com.qiper.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
        sendMessage("Please do authentication in " + SEC_TIMEOUT / 1000 + " sec!!!");
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
                        sendMessage(readLogHistory());
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
            loggingHistory(outboundMessage);
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
            changeNick(inboundMessage);
        }
    }

    private void changeNick (String inboundMessage) {
        // change nick "-N [nick] [pass] [new_nick]
        if (inboundMessage.startsWith("-N")) {
            String[] maybeChangeNick = inboundMessage.split("\\s");
            String maybeNick = maybeChangeNick[3];
            Optional<AuthService.Entry> maybeChangeEntry =
                    server.getAuthService()
                            .findUserByLoginAndPassword(maybeChangeNick[1], maybeChangeNick[2]);
            if (maybeChangeEntry.isPresent()) {
                AuthService.Entry user = maybeChangeEntry.get();
                if (server.isUserOccupied(user.getName())) {
                    server.getAuthService().changeNick(user, maybeNick);
                }
            }
        }
    }

    private void loggingHistory (String outboundMessage) throws IOException {
        if (name != null) {
            createLogFile();
            byte[] fileOut = outboundMessage.getBytes(StandardCharsets.UTF_8);
            byte[] n = "\n".getBytes(StandardCharsets.UTF_8);
            FileOutputStream out = new FileOutputStream(createLogFile(), true);
            out.write(fileOut);
            out.write(n);
        }
    }

    private String readLogHistory () {
        createLogFile();
        StringBuilder sb = new StringBuilder();
        try {
            RandomAccessFile raf = new RandomAccessFile(createLogFile(), "r");
            long length = createLogFile().length() - 2;
            int readLines = 0;

            raf.seek(length);

            for (long i = length; i >= 0; i--) {
                raf.seek(i);
                char ch = (char) raf.read();

                if (ch == '\n') {
                    readLines++;
                    if (readLines == 100) {
                        break;
                    }
                }
                sb.append(ch);
            }
            return sb.reverse().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createLogFile () {
        String nameLog = "history_" + name + ".txt";
        return new File
                ("/home/ivasch/IdeaProjects/git/Java/qiper/src/com/qiper/clientApp/log/" + nameLog);
    }

}