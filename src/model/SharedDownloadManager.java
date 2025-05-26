package model;

import dispatcher.PeerMessenger;

import java.io.File;
import java.io.FileNotFoundException;
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
    private int globalChunkSize = 3000;

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

    public void addFileChunkPart(FileChunk fileChunk) {
        this.downloadedChunks.add(fileChunk);
        System.out.println("DEBUG RECEIVED: " + downloadedChunks.size());

        if (this.downloadedChunks.size() == this.chunksToDownload.size()) {
            System.out.println("DEBUG FINIIIIIIISH DOWNLOADING");
            downloadedChunks.sort((o1, o2) -> o1.getChunkIndex() - o2.getChunkIndex());
            List<byte[]> decodedChunks = new ArrayList<>();
            for (FileChunk chunk : downloadedChunks) {
                decodedChunks.add(Base64.getDecoder().decode(chunk.getContent()));
            }


            try (FileOutputStream fos = new FileOutputStream(sharedDir + "/" + fileToDownload.getName())) {
                for (byte[] parte : decodedChunks) {
                    fos.write(parte);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveFileFromBase64(String encodedFile) throws IOException {
        byte[] fileContent = Base64.getDecoder().decode(encodedFile);
        File file = new File(sharedDir + "/" + fileToDownload.getName());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(fileContent);
        fos.close();
    }

}
