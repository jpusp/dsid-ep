import handler.ByeHandler;
import handler.GetPeersHandler;
import dispatcher.MessageDispatcher;
import handler.HelloHandler;
import handler.PeerListHandler;
import model.Action;
import model.Peer;
import screens.navigation.Navigation;
import screens.navigation.Route;

import java.io.File;
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

        MessageDispatcher dispatcher = new MessageDispatcher();
        ArrayList<Peer> neighbourPeers;
        try {
            neighbourPeers = loadPeersFromFile(neighboursPath, dispatcher);
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

        Peer rootPeer = new Peer(address, dispatcher, neighbourPeers);
        Navigation navigation = new Navigation(rootPeer, neighbourPeers, sharedDir);

        dispatcher.register(Action.HELLO, new HelloHandler(rootPeer));
        dispatcher.register(Action.LIST_PEERS, new PeerListHandler(rootPeer));
        dispatcher.register(Action.GET_PEERS, new GetPeersHandler(rootPeer));
        dispatcher.register(Action.BYE, new ByeHandler(rootPeer));

        if (isPathValid(sharedDir)) {
            rootPeer.startServer();
            navigation.navigate(Route.INITIAL);
        }
    }

    private static boolean isPathValid(String path) {
        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory() || !directory.canRead()) {
            System.err.println("Diretório inválido ou sem permissão de leitura: " + path);
            return false;
        } else return true;

    }

    public static ArrayList<Peer> loadPeersFromFile(String filePath, MessageDispatcher dispatcher) throws IOException {
        ArrayList<Peer> peers = new ArrayList<>();

        List<String> lines = Files.readAllLines(Path.of(filePath));

        for (String line : lines) {
            String[] address = line.trim().split(":");

            if (address.length != 2) continue;

            try {
                int port = Integer.parseInt(address[1]);
                peers.add(new Peer(new InetSocketAddress(address[0], port), dispatcher));
            } catch (NumberFormatException e) {
                System.err.println("Porta inválida na linha: " + line);
            }
        }

        return peers;
    }
}