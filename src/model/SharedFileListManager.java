package model;

import java.util.ArrayList;
import java.util.List;

public class SharedFileListManager {

    public final Object lock = new Object();
    private final List<SharableFile> files = new ArrayList<>();
    private int remainingPeers = 0;

    public void addFilesFromPeer(List<SharableFile> files) {
        synchronized (lock) {
            this.files.addAll(files);
            remainingPeers =  remainingPeers - 1;
            System.out.println("DEBUG SHARED FILE LIST MANAGER");
            System.out.println("Remaining peers: " + remainingPeers);
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

}
