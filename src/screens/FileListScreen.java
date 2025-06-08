package screens;

import dispatcher.PeerMessageErrorCallback;
import dispatcher.PeerMessenger;
import model.*;
import screens.navigation.Navigation;
import screens.navigation.Route;

import java.util.ArrayList;
import java.util.List;

public class FileListScreen extends AbstractScreen implements PeerMessageErrorCallback {

    private final Peer localPeer;
    private final SharedFileListManager sharedFileListManager;
    private final SharedDownloadManager sharedDownloadManager;

    public FileListScreen(Navigation navigation, Peer localPeer, SharedFileListManager sharedFileListManager, SharedDownloadManager sharedDownloadManager) {
        super(navigation);
        this.localPeer = localPeer;
        this.sharedFileListManager = sharedFileListManager;
        this.sharedDownloadManager = sharedDownloadManager;
    }

    @Override
    protected void showOptions() {
        sharedFileListManager.startNewRequest(localPeer.getNeighbours().size());

        List<Peer> neighboursPeers = this.localPeer.getNeighbours();
        for (Peer peer : neighboursPeers) {
            PeerMessenger.sendMessageToPeer(Action.LIST_FILES, localPeer, peer, this);
        }

        synchronized (sharedFileListManager.lock) {
            while (sharedFileListManager.getRemainingPeers() > 0) {
                try {
                    System.out.println("Carregando arquivos...");
                    sharedFileListManager.lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Arquivos encontrados na rede:");
            System.out.printf("%-4s %-60s %-10s %-15s\n", "","Nome", "Tamanho", "Peer");
            System.out.println("[0]  Voltar para o menu anterior");

            List<SharableFile> files = sharedFileListManager.getFiles();
            for (int i = 0; i < files.size(); i++) {
                String option = "[" + (i + 1) + "]";
                String name = files.get(i).getName();
                int size = files.get(i).getSize();
                System.out.printf("%-4s %-60s %-10d %-30s\n", option, name, size, files.get(i).getPeersString());
            }
        }
    }

    @Override
    protected void onOptionSelected(int option) {
        if (option == 0) {
            navigation.navigate(Route.INITIAL);
        } else {
            int index = option - 1;
            List<SharableFile> files = sharedFileListManager.getFiles();
            if (index >= 0 && index < files.size()) {
                SharableFile file = files.get(index);
                System.out.println("SELECTED " + file.getName() + " " + file.getPeersString());

                sharedDownloadManager.startDownload(file, localPeer);
                navigation.navigate(Route.INITIAL);
            } else {
                System.out.println("INVALID OPTION");
            }

        }
    }

    @Override
    public void onMessageError() {
        sharedFileListManager.decrementRemainingPeers();
    }
}
