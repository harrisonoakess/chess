package server;
import dataaccess.datastorage.MemoryUserDAO;
import model.UserData;
import spark.*;

public class Server {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (request, response) -> {
            userDAO.createNewUser(new UserData("test_user", "test_pass",""));
            response.status(200);
            return "{}"; // needs to be a json
        });

        Spark.delete("/db", (request, response) -> {
            userDAO.ClearUsers();
            response.status(200);
            return "{}"; // needs to be a json
        });
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
