package handler;

import model.Peer;
import model.SharableFile;
import model.SharedFileListManager;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class FileListHandler implements MessageHandler {
    private final Peer localPeer;
    private final SharedFileListManager sharedFileListManager;

    public FileListHandler(Peer localPeer, SharedFileListManager sharedFileListManager) {
        this.localPeer = localPeer;
        this.sharedFileListManager = sharedFileListManager;
    }

    @Override
    public void handle(String sender, int clock, String[] args) {
        localPeer.updateClockOnReceive(clock, true);
        localPeer.incrementClock();

        try {
            ArrayList<SharableFile> files = new ArrayList<>();
            for (String arg: args) {
                String[] fileArgs = arg.split(":");
                files.add(new SharableFile(Integer.parseInt(fileArgs[1]), fileArgs[0], sender));
            }
            sharedFileListManager.addFilesFromPeer(files, sender);
        } catch (NumberFormatException ignored) {}
    }
}
