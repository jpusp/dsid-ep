package handler;

public interface MessageHandler {
    void handle(String sender, int clock, String[] args);
}
