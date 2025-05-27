package model;

import dispatcher.PeerMessenger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SharedDownloadManager {

    private String sharedDir;
    private SharableFile fileToDownload;
    private List<FileChunk> chunksToDownload;
    private List<FileChunk> downloadedChunks;
    private int globalChunkSize = 256;
    private long requestedAt = 0;

    private List<DownloadStats> stats =  new ArrayList<>();

    public SharedDownloadManager(String sharedDir) {
        this.sharedDir = sharedDir;
    }

    public void startDownload(SharableFile sharableFile, Peer localPeer) {
        this.fileToDownload = sharableFile;
        this.chunksToDownload = new ArrayList<>();
        this.downloadedChunks = new ArrayList<>();

        int numberOfChunks = sharableFile.getSize()/globalChunkSize;
        if (sharableFile.getSize() % globalChunkSize > 0) {
            numberOfChunks++;
        }
        List<Peer> peersWithFile = new ArrayList<>();
        for (String peerAddress : sharableFile.getPeers()) {
            peersWithFile.add(localPeer.findPeerByAddress(peerAddress));
        }

        for (int i = 0; i < numberOfChunks; i++) {
            chunksToDownload.add(new FileChunk(sharableFile.getName(), i, globalChunkSize, peerForChunk(peersWithFile, i)));
        }

        requestedAt = System.currentTimeMillis();
        for (FileChunk chunk : chunksToDownload) {
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

    public List<DownloadStats> getStats() {
        return stats;
    }

    public void addFileChunkPart(FileChunk fileChunk) {
        this.downloadedChunks.add(fileChunk);

        if (this.downloadedChunks.size() == this.chunksToDownload.size()) {
            downloadedChunks.sort((o1, o2) -> o1.getChunkIndex() - o2.getChunkIndex());
            List<byte[]> decodedChunks = new ArrayList<>();
            for (FileChunk chunk : downloadedChunks) {
                decodedChunks.add(Base64.getDecoder().decode(chunk.getContent()));
            }


            long currentTime = System.currentTimeMillis();
            int duration = Math.toIntExact(currentTime - requestedAt);


            addStatistic(globalChunkSize, fileToDownload.getSize(), fileToDownload.getPeers().size(), duration);
            try (FileOutputStream fos = new FileOutputStream(sharedDir + "/" + fileToDownload.getName())) {
                for (byte[] parte : decodedChunks) {
                    fos.write(parte);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void addStatistic(int chunkSize, int fileSize, int peersSize, int duration) {
        StringBuilder sbID = new StringBuilder();
        sbID.append(chunkSize);
        sbID.append(fileSize);
        sbID.append(peersSize);
        DownloadStats downloadStats = null;
        for (DownloadStats stat : stats) {
            if (stat.getId().contentEquals(sbID)) {
                downloadStats = stat;
                stat.getDurations().add(duration);
            }
        }
        if (downloadStats == null) {
            downloadStats = new DownloadStats(chunkSize, fileSize, peersSize);
            downloadStats.getDurations().add(duration);
            stats.add(downloadStats);
        }
    }

}
