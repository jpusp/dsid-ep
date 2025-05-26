package handler;

import model.FileChunk;
import model.Peer;
import model.SharedDownloadManager;
import model.SharedFileListManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class FileReceiveHandler implements MessageHandler {
    private final Peer localPeer;
    private final String sharedDir;
    private final SharedFileListManager sharedFileListManager;
    private final SharedDownloadManager sharedDownloadManager;

    public FileReceiveHandler(Peer peer, String sharedDir, SharedFileListManager sharedFileListManager, SharedDownloadManager sharedDownloadManager) {
        this.localPeer = peer;
        this.sharedDir = sharedDir;
        this.sharedFileListManager = sharedFileListManager;
        this.sharedDownloadManager = sharedDownloadManager;
    }

    @Override
    public void handle(String sender, int clock, String[] args) {
        localPeer.updateClockOnReceive(clock, true);
        localPeer.incrementClock();

        FileChunk chunk = new FileChunk(
                args[0],
                Integer.parseInt(args[2]),
                Integer.parseInt(args[1]),
                localPeer.findPeerByAddress(sender)
        );
        chunk.setContent(args[3]);
        sharedDownloadManager.addFileChunkPart(chunk);
    }
}
