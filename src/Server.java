import handler.ClientHandler;
import listener.ClientListener;
import model.ActionType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static model.ActionType.*;

public class Server implements ClientListener {
    private final int port;
    private int minValue = 0;
    Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
    boolean isOnline = true;

    public Server(int port, int minValue) {
        this.port = port;
        this.minValue = minValue;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (isOnline) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                clientHandlers.add(handler);
                new Thread(handler).start();
            }

            clientHandlers.forEach(ClientHandler::disconnect);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void broadcastMessage(Properties message) {
        cleanClosedSockets();
        clientHandlers.forEach(handler -> {
            handler.sendMessage(message);
        });
    }

    private void cleanClosedSockets() {
        List<ClientHandler> handlersToBeRemoved = new ArrayList<>();
        clientHandlers.stream()
                .filter(ClientHandler::isClosed)
                .forEach(handlersToBeRemoved::add);

        handlersToBeRemoved.forEach(clientHandler -> clientHandlers.remove(clientHandler));
    }

    private Properties createResponse(ActionType action, int value) {
        Properties response = new Properties();
        return response;
    }
}