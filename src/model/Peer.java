package model;

import dispatcher.MessageDispatcher;
import dispatcher.PeerMessenger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Peer {
    private final InetSocketAddress socketAddress;
    private ServerSocket serverSocket;
    private final Set<PeerConnection> connections = ConcurrentHashMap.newKeySet();
    private volatile boolean running = true;
    private PeerStatus status = PeerStatus.OFFLINE;
    private int clock = 0;
    private final List<Peer> neighbours = new ArrayList<>();
    private MessageDispatcher dispatcher;
    private PeerMessenger messenger;


    public Peer(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(socketAddress.getPort(), 50, socketAddress.getAddress());
                System.out.println("Peer listening at " + socketAddress.getHostString() + ":" + socketAddress.getPort());

                while (running) {
                    Socket socket = serverSocket.accept();
                    PeerConnection connection = new PeerConnection(socket, dispatcher);
                    connections.add(connection);
                    new Thread(connection).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void connectToPeer(String peerAddress, int peerPort) {
        try {
            Socket socket = new Socket(peerAddress, peerPort);
            PeerConnection connection = new PeerConnection(socket, dispatcher);
            connections.add(connection);
            new Thread(connection).start();
        } catch (IOException e) {
            System.out.println("Falha ao conectar ao peer: " + peerAddress + ":" + peerPort);
        }
    }

    public void broadcastMessage(String message) {
        connections.forEach(connection -> connection.sendMessage(message));
    }

    public void stop() {
        running = false;
        connections.forEach(PeerConnection::disconnect);
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNeighbours(List<Peer> neighboursList) {
        neighbours.clear();
        neighbours.addAll(neighboursList);
    }

    public void addNeighbour(Peer peer) {
        neighbours.add(peer);
    }

    public void setDispatcher(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setMessenger(PeerMessenger messenger) {
        this.messenger = messenger;
    }

    public Peer findPeerByAddress(InetSocketAddress address) {
        for (Peer p : neighbours) {
            if (p.getSocketAddress().equals(address)) {
                return p;
            }
        }
        return null;
    }

    public synchronized int incrementClock() {
        clock++;
        System.out.println("=> Atualizando relogio para " + clock);
        return clock;
    }

    public synchronized void updateClockOnReceive() {
        clock++;
        System.out.println("=> Atualizando relogio para " + clock);
    }

    public void sendHelloTo(Peer targetPeer) {
        messenger.sendMessageToPeer(Action.HELLO, targetPeer);
    }

    public PeerStatus getStatus() {
        return status;
    }

    public void setStatus(PeerStatus status) {
        this.status = status;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public String getAddressString() {
        return socketAddress.getHostString() + ":" + socketAddress.getPort();
    }

    @Override
    public String toString() {
        return getAddressString() + " " + getStatus().toString();
    }
}
