package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.datastorage.CreateNewTables;
import dataaccess.datastorage.DBAuthDAO;
import dataaccess.datastorage.DBGameDAO;
import dataaccess.datastorage.DBUserDAO;
import websocket.WebSocketHandler;
import model.*;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;
import java.util.Objects;

public class Server {
    private final DBAuthDAO authDAO = new DBAuthDAO();
    private final DBGameDAO gameDAO = new DBGameDAO();
    private final DBUserDAO userDAO = new DBUserDAO(authDAO);
    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final Gson gson = new Gson();


    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
            CreateNewTables.initialize();
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // websocket
        Spark.webSocket("/ws", WebSocketHandler.class);

        // Register your endpoints and handle exceptions here.
        registeredUserEndpoints();
        registeredGameEndpoints();



        //This line initializes the server and can be removed once you have a functioning endpoint
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void registeredUserEndpoints(){
        // create a new user account
        Spark.post("/user", (request, response) -> {
            try{
                RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
                RegisterResult result = userService.register(registerRequest);
                response.status(200);
                return gson.toJson(result);
            }catch (DataAccessException DataAccessException){
                if (Objects.equals(DataAccessException.getMessage(), "Password cannot be blank")){
                    response.status(400);
                    return gson.toJson(new AddErrorMessage("Error: User already taken"));
                }
                if (Objects.equals(DataAccessException.getMessage(), "Error: User already exists")){
                    response.status(403);
                    return gson.toJson(new AddErrorMessage("Error: User already taken"));
                }
                response.status(500);
                return gson.toJson((new AddErrorMessage("Error: "+ DataAccessException.getMessage())));
            }
        });
        // login
        Spark.post("/session", (request, response) -> {
            try {
                LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);
                LoginResult loginResult = userService.login(loginRequest);
                response.status(200);
                return gson.toJson(loginResult);
            } catch(DataAccessException dataAccessException){
                if(Objects.equals(dataAccessException.getMessage(), "User does not exist")){
                    response.status(401);
                    return gson.toJson(new AddErrorMessage("Error: user does not exist"));
                }
                if (Objects.equals(dataAccessException.getMessage(), "Password does not match")){
                    response.status(401);
                    return gson.toJson(new AddErrorMessage("Error: password does not match"));
                }
                response.status(401);
                return gson.toJson((new AddErrorMessage("Error: "+ dataAccessException.getMessage())));
            }
        });
        // logout
        Spark.delete("/session", ((request, response) -> {
            try{
                String authToken = request.headers("Authorization");
                userService.logout(authToken);
                response.status(200);
                return "{}";

            } catch(DataAccessException e) {
                response.status(401);
                return gson.toJson(new AddErrorMessage("Error: " + e.getMessage()));
            }
        }));
        // delete database
        Spark.delete("/db", ((request, response) -> {
            gameService.clearData();
            userService.clearData();
            response.status(200);
            return "{}";
        }));
    }

    public void registeredGameEndpoints(){
        // create new game
        Spark.post("/game", (request, response) -> {
            try{
                String authToken = request.headers("Authorization");
                CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
                CreateGameResult createGameResult = gameService.createGame(createGameRequest.gameName(), authToken);
                response.status(200);
                return gson.toJson(createGameResult);
            } catch (DataAccessException exception){
                response.status(401);
                return gson.toJson(new AddErrorMessage("Error: " + exception.getMessage()));
            }
        });
        // join game
        Spark.put("/game", ((request, response) -> {
            try{
                String authToken = request.headers("Authorization");
                JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
                gameService.joinGame(joinGameRequest.playerColor(), joinGameRequest.gameID(), authToken);
                response.status(200);
                return gson.toJson(new AddErrorMessage("test"));
            } catch (DataAccessException exception){
                if (Objects.equals(exception.getMessage(), "Team already filled")){
                    response.status(403);
                    return gson.toJson(new AddErrorMessage("Error: " + exception.getMessage()));
                }
                if (Objects.equals(exception.getMessage(), "Invalid Game ID")){
                    response.status(400);
                    return gson.toJson(new AddErrorMessage("Error: " + exception.getMessage()));
                }
                if (Objects.equals(exception.getMessage(), "Please choose Black team or White team")){
                    response.status(400);
                    return gson.toJson(new AddErrorMessage("Error: " + exception.getMessage()));
                }
                response.status(401);
                return gson.toJson(new AddErrorMessage("Error: " + exception.getMessage()));
            }
        }));
        // list all games
        Spark.get("/game", (request,response) -> {
            try{
                String authToken = request.headers("Authorization");
                Map<Integer, GameData> listOfGames = gameService.listGames(authToken);
                GameData[] gamesArray = listOfGames.values().toArray(new GameData[0]);
                response.status(200);
                return gson.toJson(new ListAllGamesResult(gamesArray));

//                response.status(200);
//                return gson.toJson(listOfGames);
            } catch (DataAccessException exception){
                if (Objects.equals(exception.getMessage(), "User not logged in")){
                    response.status(401);
                    return gson.toJson(new AddErrorMessage("Error: " + exception.getMessage()));
                }
                response.status(401);
                return gson.toJson(new AddErrorMessage("Error: " + exception.getMessage()));
            }
        });
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static class AddErrorMessage {
        String message;

        AddErrorMessage(String message) {
            this.message = message;
        }
    }

}
