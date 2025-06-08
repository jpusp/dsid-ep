package screens.navigation;

import dispatcher.PeerMessenger;
import handler.FileHandler;
import model.*;
import screens.*;

import java.util.ArrayList;
import java.util.List;

public class Navigation {

    private final Peer rootPeer;
    private final List<Peer> neighboursPeers;
    private final String sharedDir;
    private final SharedFileListManager sharedFileListManager;
    private final SharedDownloadManager sharedDownloadManager;

    public Navigation(
            Peer rootPeer,
            List<Peer> neighboursPeers,
            String sharedDir,
            SharedFileListManager sharedFileListManager,
            SharedDownloadManager sharedDownloadManager
    ) {
        this.rootPeer = rootPeer;
        this.neighboursPeers = neighboursPeers;
        this.sharedDir = sharedDir;
        this.sharedFileListManager = sharedFileListManager;
        this.sharedDownloadManager = sharedDownloadManager;
    }

    public void navigate(Route route) {
        switch (route) {
            case INITIAL:
                navigateToScreen(new InitialScreen(this));
                break;

            case DISPLAY_PEERS:
                navigateToScreen(new DisplayPeersScreen(this, rootPeer, neighboursPeers));
                break;

            case GET_PEERS:
                for (Peer peer : neighboursPeers) {
                    PeerMessenger.sendMessageToPeer(Action.GET_PEERS, rootPeer, peer);
                }
                navigate(Route.INITIAL);
                break;

            case DISPLAY_FILES:
                FileHandler.listAndPrintLocalFiles(sharedDir);
                navigate(Route.INITIAL);
                break;

            case SEARCH_FILES:
                navigateToScreen(new FileListScreen(this, rootPeer, sharedFileListManager, sharedDownloadManager));
                break;

            case SHOW_STATS:
                navigateToScreen(new DisplayStatisticsScreen(this, sharedDownloadManager));
                break;

            case CHANGE_CHUNK:
                navigateToScreen(new ChangeChunkScreen(this, sharedDownloadManager));
                System.out.println("Alterando tamanho de chunk...");
                break;

            case EXIT:
                rootPeer.shutdown();
                System.exit(0);
                break;

            default:
                System.out.println("Comando inválido.");
                break;
        }
    }


    private void navigateToScreen(AbstractScreen screen) {
        screen.display();
    }
}
