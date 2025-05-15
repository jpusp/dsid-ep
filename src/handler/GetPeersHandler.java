package handler;

import dispatcher.PeerMessenger;
import model.Action;
import model.Peer;
import model.PeerStatus;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class GetPeersHandler implements MessageHandler {

    private final Peer localPeer;

    public GetPeersHandler(Peer localPeer) {
        this.localPeer = localPeer;
    }

    @Override
    public void handle(String sender, int clock, String[] args) {
        localPeer.updateClockOnReceive(clock, true);
        localPeer.incrementClock();

        String[] parts = sender.split(":");
        if (parts.length != 2) {
            return; 
        }

        String ip = parts[0];
        int port;
        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return;
        }

        InetSocketAddress address = new InetSocketAddress(ip, port);
        Peer senderPeer = localPeer.findPeerByAddress(address);
        if (senderPeer == null) {
            senderPeer = new Peer(address, localPeer.getDispatcher());
            localPeer.addNeighbour(senderPeer);
            System.out.println("Adicionando novo peer " + sender + " status ONLINE");
        }

        if (clock > senderPeer.getClock()) {
            senderPeer.setStatus(PeerStatus.ONLINE);
            System.out.println("Atualizando peer " + sender + " status ONLINE");
            senderPeer.updateClockOnReceive(clock, false);
        }

        List<Peer> knownPeers = localPeer.getNeighbours();
        List<String> msgArgs = new ArrayList<>();

        int count = 0;
        for (Peer p : knownPeers) {
            if (!p.getSocketAddress().equals(address)) {
                count++;
            }
        }
        msgArgs.add(String.valueOf(count));

        for (Peer p : knownPeers) {
            if (p.getSocketAddress().equals(address)) {
                continue;
            }
            msgArgs.add(p.getAddressString() + ":" + p.getStatus() + ":" + p.getClock());
        }

        String[] finalArgs = msgArgs.toArray(new String[0]);
        PeerMessenger.sendMessageToPeer(
                Action.LIST_PEERS,
                localPeer,
                senderPeer,
                finalArgs
        );
    }
}
