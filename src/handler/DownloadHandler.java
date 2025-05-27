package handler;

import dispatcher.PeerMessenger;
import model.Action;
import model.Peer;
import model.PeerStatus;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Base64;

public class DownloadHandler implements MessageHandler {
    private final Peer localPeer;
    private final String sharedDir;

    public DownloadHandler(Peer peer, String sharedDir) {
        this.localPeer = peer;
        this.sharedDir = sharedDir;
    }

    @Override
    public void handle(String sender, int clock, String[] args) {
        localPeer.updateClockOnReceive(clock, true);
        localPeer.incrementClock();

        Peer senderPeer = localPeer.findPeerByAddress(sender);
        File file = findFile(args[0]);
        if (file != null) {
            String fileContent = null;
            try {
                fileContent = fileContent(file);
                PeerMessenger.sendMessageToPeer(
                        Action.FILE,
                        localPeer,
                        senderPeer,
                        fileContent, "0", "0");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private File findFile(String fileName) {
        File folder = new File(sharedDir);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(fileName)) return file;
            }
        }
        return null;
    }

    private String fileContent(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
