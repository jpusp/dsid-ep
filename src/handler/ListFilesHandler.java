package handler;

import dispatcher.PeerMessenger;
import model.Action;
import model.Peer;
import model.PeerStatus;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ListFilesHandler implements MessageHandler {
    private final Peer localPeer;
    private final String sharedDirectory;

    public ListFilesHandler(Peer localPeer, String sharedDirectory) {
        this.localPeer = localPeer;
        this.sharedDirectory = sharedDirectory;
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
            File[] files = FileHandler.listLocalFiles(sharedDirectory);
            List<String> msgArgs = new ArrayList<>();
            for (File file : files) {
                msgArgs.add(file.getName() + ":" + file.length());
            }

            PeerMessenger.sendMessageToPeer(
                    Action.FILE_LIST,
                    localPeer,
                    senderPeer,
                    msgArgs.toArray(new String[0])
            );

        } catch (NumberFormatException ignored) {}
    }
}
