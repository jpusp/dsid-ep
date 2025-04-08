package screens;

import dispatcher.PeerMessenger;
import model.Action;
import model.Peer;
import screens.navigation.Navigation;
import screens.navigation.Route;

import java.util.List;

public class DisplayPeersScreen extends AbstractScreen {

    private final Peer localPeer;
    private final List<Peer> neighboursPeers;

    public DisplayPeersScreen(Navigation navigation, Peer localPeer, List<Peer> neighboursPeers) {
        super(navigation);
        this.localPeer = localPeer;
        this.neighboursPeers = neighboursPeers;
    }

    @Override
    protected void showOptions() {
        System.out.println("Lista de peers:");
        System.out.println("\t[0] Voltar para o menu anterior");

        for (int i = 0; i < neighboursPeers.size(); i++) {
            System.out.println("\t[" + (i + 1) + "] " + neighboursPeers.get(i).toString());
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
                PeerMessenger.sendMessageToPeer(Action.HELLO, localPeer, selectedPeer);
            }
            navigation.navigate(Route.DISPLAY_PEERS);
        }
    }
}
