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
    private final ArrayList<Peer> neighbours;
    private final MessageDispatcher dispatcher;

    public Peer(InetSocketAddress socketAddress, MessageDispatcher dispatcher) {
        this.socketAddress = socketAddress;
        this.dispatcher = dispatcher;
        this.neighbours = new ArrayList<>();
    }

    public Peer(
            InetSocketAddress socketAddress,
            MessageDispatcher dispatcher,
            ArrayList<Peer> neighbours
    ) {
        this.socketAddress = socketAddress;
        this.dispatcher = dispatcher;
        this.neighbours = neighbours;
    }

    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(socketAddress.getAddress(), socketAddress.getPort()));


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

    public void stop() {
        running = false;
        connections.forEach(PeerConnection::disconnect);
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    public List<Peer> getNeighbours() {
        return neighbours;
    }

    public void addNeighbour(Peer peer) {
        neighbours.add(peer);
    }

    public Peer findPeerByAddress(String address) {
        String[] parts = address.split(":");
        if (parts.length != 2) {
            return null;
        }

        String ip = parts[0];
        int port;
        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }

        InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
        return findPeerByAddress(socketAddress);
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

    public synchronized int getClock() {
        return clock;
    }

    public synchronized void updateClockOnReceive(int clockReceived, boolean verbose) {
        if (clockReceived > clock) {
            clock = clockReceived;
            if (verbose) System.out.println("=> Atualizando relogio para " + clock);
        }
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

    public void shutdown() {
        for (Peer peer : neighbours) {
            PeerMessenger.sendMessageToPeer(Action.BYE, this, peer);
        }
    }

    @Override
    public String toString() {
        return getAddressString() + " " + getStatus().toString();
    }
}
