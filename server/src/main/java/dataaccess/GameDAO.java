package dataaccess;

import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.List;

public interface GameDAO {
    List<GameData> listAllGames();
    GameData createNewGame(GameData name_of_game);
    void joinGame(UserData user);
}
