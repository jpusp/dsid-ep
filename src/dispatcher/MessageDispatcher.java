package dispatcher;

import handler.MessageHandler;
import model.Action;

import java.util.HashMap;
import java.util.Map;

public class MessageDispatcher {
    private final Map<Action, MessageHandler> handlers = new HashMap<>();

    public void register(Action action, MessageHandler handler) {
        handlers.put(action, handler);
    }

    public MessageHandler getHandler(Action action) {
        return handlers.get(action);
    }
}
