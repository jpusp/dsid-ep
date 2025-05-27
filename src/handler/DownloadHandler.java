package handler;

import dispatcher.PeerMessenger;
import model.Action;
import model.FileChunk;
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
        try {
            FileChunk chunk = fileContent(args[0], Integer.parseInt(args[2]), Integer.parseInt(args[1]));
            PeerMessenger.sendMessageToPeer(
                    Action.FILE,
                    localPeer,
                    senderPeer,
                    chunk.getFileName(),
                    String.valueOf(chunk.getChunkSize()),
                    String.valueOf(chunk.getChunkIndex()),
                    chunk.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private FileChunk fileContent(String filename, int chunkIndex, int globalChunkSize) throws IOException {
        File file = findFile(filename);
        byte[] bytesFileContent = Files.readAllBytes(file.toPath());
        int chunkStart = chunkIndex * globalChunkSize;
        int chunkSize = Math.min(globalChunkSize, (bytesFileContent.length - chunkStart));

        FileChunk fileChunk = new FileChunk(
                filename,
                chunkIndex,
                chunkSize,
                localPeer
        );

        byte[] chunkContent = new byte[chunkSize];

        for (int i = 0; i < chunkSize; i++) {
            int bytesIntex = chunkStart + i;
            chunkContent[i] = bytesFileContent[bytesIntex];
        }

        String fileContentString =  Base64.getEncoder().encodeToString(chunkContent);
        fileChunk.setContent(fileContentString);
        return fileChunk;
    }
}
