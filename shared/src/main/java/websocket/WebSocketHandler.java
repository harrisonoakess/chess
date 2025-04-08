package websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;


import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(action.getAuthToken(), action.getGameID(), session);
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        try {
            DBAuthDAO authDAO = new DBAuthDAO();
            DBGameDAO gameDAO = new DBGameDAO();
            String username = authDAO.getAuthToken(authToken).username();
            if (gameData == null) throw new DataAccessException("invalid Game ID")
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connected: " + session.getId());
    }
    @OnClose
    public void onClose(Session session) {
        connections.remove(session);
        System.out.println("WebSocket closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

}
