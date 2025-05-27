package model;

import java.util.ArrayList;
import java.util.List;

public class SharableFile {

    private int size;
    private String name;
    private List<String> peers = new ArrayList<>();

    public SharableFile(int size, String name, String peer) {
        this.size = size;
        this.name = name;
        this.peers.add(peer);
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public List<String> getPeers() {
        return peers;
    }


    public static ArrayList<SharableFile> getList() {
        ArrayList<SharableFile> files = new ArrayList<SharableFile>();
        files.add(new SharableFile(10, "lalala", "10.30:400"));
        files.add(new SharableFile(55, "oasososo", "10.30:400"));
        return files;
    }

    @Override
    public boolean equals(Object obj) {
        SharableFile file = (SharableFile) obj;
        return (file.getSize() == size && file.getName().equals(name));
    }

    public String getPeersString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < peers.size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            String peer = peers.get(i);
            sb.append(peer);
        }
        return sb.toString();
    }
}
