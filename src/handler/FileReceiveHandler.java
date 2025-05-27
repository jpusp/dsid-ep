package handler;

import model.Peer;
import model.SharedFileListManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class FileReceiveHandler implements MessageHandler {
    private final Peer localPeer;
    private final String sharedDir;
    private final SharedFileListManager sharedFileListManager;

    public FileReceiveHandler(Peer peer, String sharedDir, SharedFileListManager sharedFileListManager) {
        this.localPeer = peer;
        this.sharedDir = sharedDir;
        this.sharedFileListManager = sharedFileListManager;
    }

    @Override
    public void handle(String sender, int clock, String[] args) {
        localPeer.updateClockOnReceive(clock, true);
        localPeer.incrementClock();

        try {
            File file = fileFromBase64(args[0]);
            System.out.println("Download do arquivo finalizado.");
        } catch (IOException e) {
            System.out.println("FALHA AO SALVAR ARQUIVO");
            throw new RuntimeException(e);
        }
    }

    private File fileFromBase64(String encodedFile) throws IOException {
        byte[] fileContent = Base64.getDecoder().decode(encodedFile);
        File file = new File(sharedDir + "/" + sharedFileListManager.getFileToDownload().getName());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(fileContent);
        fos.close();
        return file;
    }
}
