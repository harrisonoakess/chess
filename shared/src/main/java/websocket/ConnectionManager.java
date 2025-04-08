package websocket;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Session, Boolean> connections = new ConcurrentHashMap<>();
}
