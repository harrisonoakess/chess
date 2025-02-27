package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.datastorage.MemoryUserDAO;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;
import service.UserService;
import spark.*;

import java.util.Objects;

public class Server {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final UserService userService = new UserService(userDAO);
    private final Gson gson = new Gson();


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        RegisteredEndpoints();

        Spark.delete("/db", (request, response) -> {
            userDAO.ClearUsers();
            response.status(200);
            return "{}"; // needs to be a json
        });
        //This line initializes the server and can be removed once you have a functioning endpoint
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void RegisteredEndpoints(){
        // create a new user account
        Spark.post("/user", (request, response) -> {
            try{
                RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
                RegisterResult result = userService.register(registerRequest);
                response.status(200);
                return gson.toJson(result);
            }catch (DataAccessException DataAccessException){
                if (Objects.equals(DataAccessException.getMessage(), "Error: already taken")){
                    response.status(403);
                    return gson.toJson(new AddErrorMessage("Error: User already taken"));
                }
                response.status(500);
                return gson.toJson((new AddErrorMessage("Error: "+ DataAccessException.getMessage())));

            } catch(Exception exception){ // this will hopefully tell me if anything else other than the database is failing
                response.status(400);
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
