package screens.navigation;

import dispatcher.PeerMessenger;
import handler.FileHandler;
import model.Action;
import model.Peer;
import screens.AbstractScreen;
import screens.DisplayPeersScreen;
import screens.InitialScreen;

import java.util.List;

public class Navigation {

    private final Peer rootPeer;
    private final List<Peer> neighboursPeers;
    private final String sharedDir;

    public Navigation(Peer rootPeer, List<Peer> neighboursPeers, String sharedDir) {
        this.rootPeer = rootPeer;
        this.neighboursPeers = neighboursPeers;
        this.sharedDir = sharedDir;
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
                FileHandler.listLocalFiles(sharedDir);
                navigate(Route.INITIAL);
                break;

            case SEARCH_FILES:
                System.out.println("Buscando arquivos...");
                break;

            case SHOW_STATS:
                System.out.println("Exibindo estatísticas...");
                break;

            case CHANGE_CHUNK:
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
