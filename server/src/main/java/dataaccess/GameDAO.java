package dataaccess;

import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.List;

public interface GameDAO {
//    List<GameData> listAllGames();
    void createNewGame(GameData gameInfo);
//    void joinGame(UserData user);
}
