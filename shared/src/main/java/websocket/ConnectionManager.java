package websocket;

import com.google.gson.Gson;
import websocket.Connection;
import websocket.messages.ServerMessage;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Connection connection) {
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeUsername, ServerMessage message) throws IOException {
        var gson = new Gson();
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeUsername)) {
                    c.send(gson.toJson(message));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }
}

