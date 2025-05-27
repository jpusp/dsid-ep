package model;

public class FileChunk {

    private final String fileName;
    private final int chunkIndex;
    private final int chunkSize;
    private Peer peerWithFile;
    private String content;

    public FileChunk(String fileName, int chunkIndex, int chunkSize, Peer peerWithFile) {
        this.fileName = fileName;
        this.chunkIndex = chunkIndex;
        this.chunkSize = chunkSize;
        this.peerWithFile = peerWithFile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public String getFileName() {
        return fileName;
    }

    public Peer getPeerWithFile() {
        return peerWithFile;
    }

}
