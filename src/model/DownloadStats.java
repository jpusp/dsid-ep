package model;

import java.util.ArrayList;
import java.util.List;

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

    public double getAverageMilliseconds() {
        int sum = 0;
        for (Integer duration : durations) {
            sum += duration;
        }
        return  (sum / (double) durations.size()) / 1000.0;
    }

    public double getStdDeviation() {
        double average = durations.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double sqrSum = durations.stream()
                .mapToDouble(i -> Math.pow(i - average, 2))
                .sum();
        return Math.sqrt(sqrSum / ((double) durations.size()));
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
