package handler;

import model.Peer;
import model.PeerStatus;

import java.net.InetSocketAddress;

public class HelloHandler implements MessageHandler {
    private final Peer localPeer;

    public HelloHandler(Peer peer) {
        this.localPeer = peer;
    }

    @Override
    public void handle(String sender, int clock, String[] args) {
        localPeer.updateClockOnReceive();

        String[] parts = sender.split(":");
        if (parts.length != 2) return;

        try {
            String ip = parts[0];
            int port = Integer.parseInt(parts[1]);
            InetSocketAddress address = new InetSocketAddress(ip, port);

            Peer existing = localPeer.findPeerByAddress(address);
            if (existing != null) {
                existing.setStatus(PeerStatus.ONLINE);
                System.out.println("Atualizando peer " + sender + " status ONLINE");
            } else {
                Peer newPeer = new Peer(address);
                newPeer.setStatus(PeerStatus.ONLINE);
                localPeer.addNeighbour(newPeer);
                System.out.println("Adicionando novo peer " + sender + " status ONLINE");
            }

        } catch (NumberFormatException ignored) {}
    }
}
