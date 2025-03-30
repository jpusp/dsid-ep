import dispatcher.MessageDispatcher;
import dispatcher.PeerMessenger;
import handler.HelloHandler;
import model.Action;
import model.Peer;
import screens.navigation.Navigation;
import screens.navigation.Route;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Uso correto: java Main <host:porta> <vizinhos.txt> <diretório_compartilhado>");
            return;
        }

        String[] serverAddress = args[0].split(":");
        InetSocketAddress address = new InetSocketAddress(serverAddress[0], Integer.parseInt(serverAddress[1]));
        String neighboursPath = args[1];
        String sharedDir = args[2];

        Peer rootPeer = new Peer(address);

        List<Peer> neighbourPeers;
        try {
            neighbourPeers = loadPeersFromFile(neighboursPath);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de vizinhos: " + e.getMessage());
            return;
        }

        neighbourPeers.forEach(peer -> {
            InetSocketAddress peerSocketAddress = peer.getSocketAddress();
            System.out.println(
                    "Adicionando novo peer "
                    + peerSocketAddress.getHostString() + ":"
                    + peerSocketAddress.getPort() + " "
                    + "status OFFLINE"
            );
        });

        rootPeer.setNeighbours(neighbourPeers);

        MessageDispatcher dispatcher = new MessageDispatcher();
        dispatcher.register(Action.HELLO, new HelloHandler(rootPeer));
        rootPeer.setDispatcher(dispatcher);

        PeerMessenger messenger = new PeerMessenger(rootPeer);
        rootPeer.setMessenger(messenger);


        Navigation navigation = new Navigation(rootPeer, neighbourPeers, sharedDir);
        navigation.navigate(Route.INITIAL);
    }

    public static List<Peer> loadPeersFromFile(String filePath) throws IOException {
        List<Peer> peers = new ArrayList<>();

        List<String> lines = Files.readAllLines(Path.of(filePath));

        for (String line : lines) {
            String[] address = line.trim().split(":");

            if (address.length != 2) continue;

            try {
                int port = Integer.parseInt(address[1]);
                peers.add(new Peer(new InetSocketAddress(address[0], port)));
            } catch (NumberFormatException e) {
                System.err.println("Porta inválida na linha: " + line);
            }
        }

        return peers;
    }
}