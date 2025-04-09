package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    public final Map<Integer, Map<String, Connection>> gameConnections = new HashMap<>();

    public void add(String visitorName, int gameID, Connection connection) {
        if (!gameConnections.containsKey(gameID)) {
            gameConnections.put(gameID, new HashMap<>());
        }
        gameConnections.get(gameID).put(visitorName, connection);
    }

    public void remove(String visitorName, int gameID) {
        Map<String, Connection> connections = gameConnections.get(gameID);
        if (connections != null) {
            connections.remove(visitorName);
            if (connections.isEmpty()) {
                gameConnections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String excludeUsername, String message) throws IOException {
        Map<String, Connection> connections = gameConnections.get(gameID);
        if (connections != null) {
            var removeList = new ArrayList<String>();
            for (var entry : connections.entrySet()) {
                String username = entry.getKey();
                Connection connection = entry.getValue();
                if (!username.equals(excludeUsername)) {
                    if (connection.session.isOpen()) {
                        connection.send(message);
                    } else {
                        removeList.add(username);
                    }
                }
            }
            // Clean up closed connections
            for (String username : removeList) {
                connections.remove(username);
            }
            if (connections.isEmpty()) {
                gameConnections.remove(gameID);
            }
        }
    }
}

