package dispatcher;

import model.Action;
import model.Peer;
import model.PeerStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PeerMessenger {

    public static void sendMessageToPeer(
            Action action,
            Peer localPeer,
            Peer targetPeer,
            String... args
    ) {
        InetSocketAddress addr = targetPeer.getSocketAddress();
        int clockValue = localPeer.incrementClock();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(localPeer.getAddressString()).append(" ")
                      .append(clockValue).append(" ")
                      .append(action.toString());

        for (String arg : args) {
            messageBuilder.append(" ").append(arg);
        }

        String message = messageBuilder.toString();

        System.out.println("Encaminhando mensagem \"" + message + "\" para " + targetPeer.getAddressString());

        try (
            Socket socket = new Socket(addr.getHostString(), addr.getPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println(message);
            targetPeer.setStatus(PeerStatus.ONLINE);
            System.out.println("Atualizando peer " + targetPeer.getAddressString() + " status ONLINE");

        } catch (IOException e) {
            targetPeer.setStatus(PeerStatus.OFFLINE);
            System.out.println("Atualizando peer " + targetPeer.getAddressString() + " status OFFLINE");
        }
    }
}
