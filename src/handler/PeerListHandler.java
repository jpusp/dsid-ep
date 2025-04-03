package handler;

import model.Peer;
import model.PeerStatus;

import java.net.InetSocketAddress;

public class PeerListHandler implements MessageHandler {

    private final Peer localPeer;

    public PeerListHandler(Peer localPeer) {
        this.localPeer = localPeer;
    }

    @Override
    public void handle(String sender, int clock, String[] args) {
        localPeer.updateClockOnReceive();

        if (args.length < 1) return;

        int peerCount;
        try {
            peerCount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return;
        }

        for (int i = 1; i <= peerCount; i++) {
            if (i >= args.length) break;

            String[] parts = args[i].split(":");
            if (parts.length != 4) continue;

            String ip = parts[0];
            int port;
            try {
                port = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                continue;
            }

            String statusStr = parts[2];
            PeerStatus status = PeerStatus.valueOf(statusStr);
            InetSocketAddress address = new InetSocketAddress(ip, port);

            if (sender.equals(ip + ":" + port)) continue;

            Peer existingPeer = localPeer.findPeerByAddress(address);
            if (existingPeer != null) {
                existingPeer.setStatus(status);
                System.out.println("Atualizando peer " + ip + ":" + port + " status " + status);
            } else {
                Peer newPeer = new Peer(address);
                newPeer.setStatus(status);
                localPeer.addNeighbour(newPeer);
                System.out.println("Adicionando novo peer " + ip + ":" + port + " status " + status);
            }
        }
    }
}
