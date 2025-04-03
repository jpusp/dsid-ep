package handler;

import model.Peer;
import model.PeerStatus;

import java.net.InetSocketAddress;

public class ByeHandler implements MessageHandler {
    private final Peer localPeer;

    public ByeHandler(Peer localPeer) {
        this.localPeer = localPeer;
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

            Peer existingPeer = localPeer.findPeerByAddress(address);
            if (existingPeer != null) {
                existingPeer.setStatus(PeerStatus.OFFLINE);
                System.out.println("Atualizando peer " + sender + " status OFFLINE");
            }
        } catch (NumberFormatException ignored) {

        }
    }
}
