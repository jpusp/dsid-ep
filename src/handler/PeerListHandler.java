package handler;

import model.Peer;
import model.PeerStatus;
import screens.navigation.Navigation;
import screens.navigation.Route;

import java.net.InetSocketAddress;

public class PeerListHandler implements MessageHandler {

    private final Peer localPeer;

    public PeerListHandler(Peer localPeer) {
        this.localPeer = localPeer;
    }

    @Override
    public void handle(String sender, int clock, String[] args) {
        localPeer.updateClockOnReceive(clock, true);
        localPeer.incrementClock();

        Peer senderPeer = localPeer.findPeerByAddress(socketByAddress(sender));
        senderPeer.updateClockOnReceive(clock, false);

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
            int peerClock = Integer.parseInt(parts[3]);
            PeerStatus status = PeerStatus.valueOf(statusStr);
            InetSocketAddress address = new InetSocketAddress(ip, port);

            Peer existingPeer = localPeer.findPeerByAddress(address);

            if (sender.equals(ip + ":" + port)) continue;

            if (existingPeer != null) {
                if (existingPeer.getClock() < peerClock) {
                    existingPeer.setStatus(status);
                    existingPeer.updateClockOnReceive(peerClock, false);
                    System.out.println("Atualizando peer " + ip + ":" + port + " status " + status);
                }
            } else {
                Peer newPeer = new Peer(address, localPeer.getDispatcher());
                newPeer.setStatus(status);
                newPeer.updateClockOnReceive(peerClock, false);
                localPeer.addNeighbour(newPeer);
                System.out.println("Adicionando novo peer " + ip + ":" + port + " status " + status);
            }
        }
    }

    private InetSocketAddress socketByAddress(String address) {
        String[] parts = address.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sender");
        }

        String ip = parts[0];
        int port;
        port = Integer.parseInt(parts[1]);
        return new InetSocketAddress(ip, port);
    }
}
