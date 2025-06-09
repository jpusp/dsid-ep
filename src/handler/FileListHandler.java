package handler;

import model.Peer;
import model.SharableFile;
import model.SharedFileListManager;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
                String fileName = URLDecoder.decode(fileArgs[0], StandardCharsets.UTF_8);
                int fileSize = Integer.parseInt(fileArgs[1]);

                files.add(new SharableFile(fileSize, fileName, sender));
            }
            sharedFileListManager.addFilesFromPeer(files, sender);
        } catch (NumberFormatException ignored) {}
    }
}
