package model;

import dispatcher.MessageDispatcher;
import handler.MessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class PeerConnection implements Runnable {
    private final Socket socket;
    private final MessageDispatcher dispatcher;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean connected = true;

    public PeerConnection(Socket socket, MessageDispatcher dispatcher) {
        this.socket = socket;
        this.dispatcher = dispatcher;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            disconnect();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while (connected && (message = in.readLine()) != null) {
                System.out.println("Mensagem recebida: " + message);

                String[] parts = message.trim().split(" ");
                if (parts.length < 3) continue;

                String sender = parts[0];
                int clock;
                try {
                    clock = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    continue;
                }

                String actionString = parts[2];
                Action action = Action.valueOf(actionString.toUpperCase());
                String[] args = Arrays.copyOfRange(parts, 3, parts.length);

                MessageHandler handler = dispatcher.getHandler(action);
                if (handler != null) {
                    handler.handle(sender, clock, args);
                }
            }
        } catch (IOException e) {
            System.out.println("Conexão fechada inesperadamente: " + socket);
        } finally {
            disconnect();
        }
    }

    public void sendMessage(String message) {
        if (connected) {
            out.println(message);
        }
    }

    public void disconnect() {
        connected = false;
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
