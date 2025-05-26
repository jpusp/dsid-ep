package model;

import dispatcher.PeerMessenger;

import java.util.ArrayList;
import java.util.List;

public class SharedDownloadManager {

    private SharableFile fileToDownload;
    private int globalChunkSize = 500;

    public void startDownload(SharableFile sharableFile, Peer localPeer) {
        this.fileToDownload = sharableFile;
        int numberOfChunks = sharableFile.getSize()/globalChunkSize;
        if (sharableFile.getSize() % globalChunkSize > 0) {
            numberOfChunks++;
        }
        List<FileChunk> chunks = new ArrayList<FileChunk>();
        List<Peer> peersWithFile = new ArrayList<>();
        for (String peerAddress : sharableFile.getPeers()) {
            peersWithFile.add(localPeer.findPeerByAddress(peerAddress));
        }

        for (int i = 0; i < numberOfChunks; i++) {
            chunks.add(new FileChunk(sharableFile.getName(), i, globalChunkSize, peerForChunk(peersWithFile, i)));
        }

        for (FileChunk chunk : chunks) {
            PeerMessenger.sendMessageToPeer(
                    Action.DOWNLOAD,
                    localPeer,
                    chunk.getPeerWithFile(),
                    sharableFile.getName(),
                    String.valueOf(chunk.getChunkSize()),
                    String.valueOf(chunk.getChunkIndex()));
        }


    }

    private Peer peerForChunk(List<Peer> peers, int chunkIndex) {
        return peers.get(chunkIndex%peers.size());
    }

    public SharableFile getFileToDownload() {
        return fileToDownload;
    }

    public int getGlobalChunkSize() {
        return globalChunkSize;
    }

    public void setGlobalChunkSize(int chunkSize) {
        this.globalChunkSize = chunkSize;
    }

}
