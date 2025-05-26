package model;

import java.util.ArrayList;
import java.util.List;

public class SharedFileListManager {

    public final Object lock = new Object();
    private final List<SharableFile> files = new ArrayList<>();
    private int remainingPeers = 0;
    private SharableFile fileToDownload;

    public void addFilesFromPeer(List<SharableFile> files, String peerAddress) {
        synchronized (lock) {
            mergeFilesFromPeer(files, peerAddress);
            remainingPeers =  remainingPeers - 1;
            lock.notify();
        }
    }

    public void startNewRequest(int knowPeers) {
        remainingPeers = knowPeers;
        files.clear();
    }

    public List<SharableFile> getFiles() {
        return files;
    }

    public int getRemainingPeers() {
        return remainingPeers;
    }

    public void decrementRemainingPeers() {
        synchronized (lock) {
            remainingPeers = remainingPeers - 1;
            lock.notify();
        }
    }

    public void setFileToDownload(SharableFile file) {
        this.fileToDownload = file;
    }

    public SharableFile getFileToDownload() {
        return fileToDownload;
    }

    private void mergeFilesFromPeer(List<SharableFile> newFiles, String peerAddress) {
        for(SharableFile newFile : newFiles) {
            boolean found = false;
            for(SharableFile file : files) {
                if(newFile.equals(file)) {
                    found = true;
                    file.getPeers().add(peerAddress);
                }
            }
            if(!found) {
                files.add(newFile);
            }
        }
    }
}
