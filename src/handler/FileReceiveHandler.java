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
        try {
            localPeer.updateClockOnReceive(clock, true);
            localPeer.incrementClock();

            String fileName = args[0];
            int chunkSize = Integer.parseInt(args[1]);
            int chunkIndex = Integer.parseInt(args[2]);
            String base64Content = args[3];

            Peer senderPeer = localPeer.findPeerByAddress(sender);
            if (senderPeer == null) {
                System.err.println("Sender peer not found: " + sender);
                return;
            }

            FileChunk chunk = new FileChunk(fileName, chunkIndex, chunkSize, senderPeer);
            chunk.setContent(base64Content);

            sharedDownloadManager.addFileChunkPart(chunk);

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem FILE de " + sender + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
