package model;

import java.util.ArrayList;

public class SharableFile {

    private int size;
    private String name;
    private String peer;


    public SharableFile(int size, String name, String peer) {
        this.size = size;
        this.name = name;
        this.peer = peer;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public String getPeer() {
        return peer;
    }


    public static ArrayList<SharableFile> getList() {
        ArrayList<SharableFile> files = new ArrayList<SharableFile>();
        files.add(new SharableFile(10, "lalala", "10.30:400"));
        files.add(new SharableFile(55, "oasososo", "10.30:400"));
        return files;
    }
}
