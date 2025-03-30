package screens;

import model.Peer;
import screens.navigation.Navigation;
import screens.navigation.Route;

import java.util.List;
import java.util.Scanner;

public class DisplayPeersScreen extends AbstractScreen {

    private final Peer rootPeer;
    private final List<Peer> neighboursPeers;

    public DisplayPeersScreen(Navigation navigation, Peer rootPeer, List<Peer> neighboursPeers) {
        super(navigation);
        this.rootPeer = rootPeer;
        this.neighboursPeers = neighboursPeers;
    }

    @Override
    protected void showOptions() {
        System.out.println("Lista de peers:");
        System.out.println("\t\t[0] Voltar para o menu anterior");

        for (int i = 0; i < neighboursPeers.size(); i++) {
            System.out.println("\t\t[" + (i + 1) + "] " + neighboursPeers.get(i).toString());
        }
    }

    @Override
    protected void onOptionSelected(int option) {
        if (option == 0) {
            navigation.navigate(Route.INITIAL);
        } else {
            int index = option - 1;
            if (index >= 0 && index < neighboursPeers.size()) {
                Peer selectedPeer = neighboursPeers.get(index);
                rootPeer.sendHelloTo(selectedPeer);
            }
            navigation.navigate(Route.DISPLAY_PEERS);
        }
    }
}
