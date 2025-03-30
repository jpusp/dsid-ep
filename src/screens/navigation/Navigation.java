package screens.navigation;

import handler.FileHandler;
import model.Peer;
import screens.DisplayPeersScreen;
import screens.InitialScreen;
import screens.Screen;

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
            case INITIAL -> {
                navigateToScreen(new InitialScreen(this));
            }
            case DISPLAY_PEERS -> {
                navigateToScreen(new DisplayPeersScreen(this, rootPeer, neighboursPeers));
            }
            case GET_PEERS -> {
                System.out.println("Obtendo peers...");
            }
            case DISPLAY_FILES -> {
                FileHandler.listLocalFiles(sharedDir);
                System.out.println("Listando arquivos locais...");
            }
            case SEARCH_FILES -> {
                System.out.println("Buscando arquivos...");
            }
            case SHOW_STATS -> {
                System.out.println("Exibindo estatísticas...");
            }
            case CHANGE_CHUNK -> {
                System.out.println("Alterando tamanho de chunk...");
            }
            case EXIT -> {
                System.out.println("Saindo...");
                System.exit(0);
            }
            default -> {
                System.out.println("Comando inválido.");
            }
        }

    }

    private void navigateToScreen(Screen screen) {
        screen.display();
    }
}
