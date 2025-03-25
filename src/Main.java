import screens.navigation.Navigation;
import screens.navigation.Route;

import java.io.IOException;
import java.net.InetAddress;
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

        Server server = new Server(address.getPort(), 0);

        List<InetSocketAddress> neighboursAddresses;
        try {
            neighboursAddresses = loadAddressesFromFile(neighboursPath);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de vizinhos: " + e.getMessage());
            return;
        }

        neighboursAddresses.forEach(neighbour -> {
            System.out.println(
                    "Adicionando novo peer "
                    + neighbour.getHostString() + ":"
                    + neighbour.getPort() + " "
                    + "status OFFLINE"
            );
        });

        Navigation navigation = new Navigation();
        navigation.navigate(Route.INITIAL);


    }

    public static List<InetSocketAddress> loadAddressesFromFile(String filePath) throws IOException {
        List<InetSocketAddress> addresses = new ArrayList<>();

        List<String> lines = Files.readAllLines(Path.of(filePath));

        for (String line : lines) {
            String[] address = line.trim().split(":");

            if (address.length != 2) continue;

            try {
                int port = Integer.parseInt(address[1]);
                addresses.add(new InetSocketAddress(address[0], port));
            } catch (NumberFormatException e) {
                System.err.println("Porta inválida na linha: " + line);
            }
        }

        return addresses;
    }
}