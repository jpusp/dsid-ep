package handler;

import model.Peer;
import model.PeerStatus;
import screens.navigation.Navigation;
import screens.navigation.Route;

import java.net.InetSocketAddress;

public class HelloHandler implements MessageHandler {
    private final Peer localPeer;

    public HelloHandler(Peer peer) {
        this.localPeer = peer;
    }

    @Override
    public void handle(String sender, int clock, String[] args) {
        localPeer.updateClockOnReceive(clock, true);
        localPeer.incrementClock();

        String[] parts = sender.split(":");
        if (parts.length != 2) return;

        try {
            String ip = parts[0];
            int port = Integer.parseInt(parts[1]);
            InetSocketAddress address = new InetSocketAddress(ip, port);

            Peer senderPeer = localPeer.findPeerByAddress(address);
            if (senderPeer != null ) {
                if (clock > senderPeer.getClock()) {
                    senderPeer.updateClockOnReceive(clock, false);
                    senderPeer.setStatus(PeerStatus.ONLINE);
                    System.out.println("Atualizando peer " + sender + " status ONLINE");
                }
            } else {
                Peer newPeer = new Peer(address, localPeer.getDispatcher());
                newPeer.updateClockOnReceive(clock, false);
                newPeer.setStatus(PeerStatus.ONLINE);
                localPeer.addNeighbour(newPeer);
                System.out.println("Adicionando novo peer " + sender + " status ONLINE");
            }

        } catch (NumberFormatException ignored) {}
    }
}
