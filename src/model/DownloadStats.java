package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DownloadStats {

    private String id;

    private int chunkSize;
    private int fileSize;
    private int peersSize;

    private List<Integer> durations = new ArrayList<>();

    public DownloadStats(int chunkSize, int fileSize, int peersSize) {
        this.chunkSize = chunkSize;
        this.peersSize = peersSize;
        this.fileSize = fileSize;

        StringBuilder sbID = new StringBuilder();
        sbID.append(chunkSize);
        sbID.append(fileSize);
        sbID.append(peersSize);

        this.id = sbID.toString();
    }

    public int getAverageMileseconds() {
        int sum = 0;
        for (Integer duration : durations) {
            sum += duration;
        }
        return  sum / durations.size();
    }

    public int getDesvpad() {
//        return desvpad;
        return 0;
    }

    public int getSampleSize() {
        return durations.size();
    }


    public String getId() {
        return this.id;
    }

    public List<Integer> getDurations() {
        return this.durations;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getPeersSize() {
        return peersSize;
    }
}
