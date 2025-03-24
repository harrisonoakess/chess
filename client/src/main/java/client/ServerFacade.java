package client;

import exception.ResponseException;
import model.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.Locale;


public class ServerFacade {
    private final String serverUrl;


    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    // Registered endpoint is returning a RegisterResult as
    // a json object (username, authToken)
    public RegisterResult register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        // this is what will be sent into the endpoint
        RegisterRequest request = new RegisterRequest(username, password, email);
        return makeRequest("POST", path, request, RegisterResult.class, null);
    }

    public LoginResult login(String username, String password) throws ResponseException {
        var path = "/session";
        // this is what will be sent into the endpoint
        LoginRequest request = new LoginRequest(username, password);
        return makeRequest("POST", path, request, LoginResult.class, null);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        makeRequest("DELETE", path, null, null, authToken );
    }

    public CreateGameResult createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        CreateGameRequest request = new CreateGameRequest(gameName);
        return makeRequest("POST", path, request, CreateGameResult.class, authToken);
    }

    public void joinGame(String playerColor, String gameID, String authToken) throws ResponseException {
        var path = "/game";
        JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
        makeRequest("PUT", path, request, JoinGameRequest.class, authToken);
    }

    public ListAllGamesResult listGames(String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("GET", path, null, ListAllGamesResult.class, authToken);
    }

public void clear() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
}



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(method.equals("POST") || method.equals("PUT"));
//          This statement adds the header with authtoken because that's how server reads it in.
            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
