package screens;

import dispatcher.PeerMessageErrorCallback;
import dispatcher.PeerMessenger;
import model.Action;
import model.Peer;
import model.SharableFile;
import model.SharedFileListManager;
import screens.navigation.Navigation;
import screens.navigation.Route;

import java.util.ArrayList;
import java.util.List;

public class FileListScreen extends AbstractScreen implements PeerMessageErrorCallback {

    private final Peer localPeer;
    private final SharedFileListManager sharedFileListManager;

    public FileListScreen(Navigation navigation, Peer localPeer, SharedFileListManager sharedFileListManager) {
        super(navigation);
        this.localPeer = localPeer;
        this.sharedFileListManager = sharedFileListManager;
    }

    @Override
    protected void showOptions() {
        System.out.println("DEBUG FILE LIST SCREEN");
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
                String address = files.get(i).getPeer();
                System.out.printf("%-4s %-60s %-10d %-15s\n", option, name, size, address);
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
                System.out.println("SELECTED " + file.getName() + " " + file.getPeer());
                Peer peerWithFile = localPeer.findPeerByAddress(file.getPeer());
                sharedFileListManager.setFileToDownload(file);
                PeerMessenger.sendMessageToPeer(
                        Action.DOWNLOAD,
                        localPeer, peerWithFile,
                        file.getName(), "0", "0");
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
