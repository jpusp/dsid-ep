import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;



public class Client {
    private final InetAddress address;
    private AtomicInteger currentMinBid = new AtomicInteger(0);
    private Socket socket;
    private String name;

    public Client(InetAddress address) {
        this.address = address;
    }

    public void start() {
    }

    private int intValue(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }


    private void sendMessage(Properties message) {
        StringWriter writer = new StringWriter();
        try{
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            message.store(new PrintWriter(writer), "");
            String serializedResponse = writer.getBuffer().toString();
            serializedResponse = serializedResponse.replaceAll("\\n", ";");
            serializedResponse = serializedResponse + "\n";
            out.println(serializedResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
