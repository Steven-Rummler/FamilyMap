package com.stevenrummler.familymap.data;

import com.google.gson.Gson;
import com.stevenrummler.familymap.data.DataCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import model.AuthToken;
import request.LoginRequest;
import request.RegisterRequest;
import result.EventsResult;
import result.LoginResult;
import result.PersonResult;
import result.PersonsResult;
import result.RegisterResult;

public class ServerProxy {
    private boolean success;
    private String message;
    private final String baseUrl;

    private RegisterResult registerResult;
    private LoginResult loginResult;
    private PersonResult personResult;
    private PersonsResult personsResult;
    private EventsResult eventsResult;

    public ServerProxy(String ip, String port) {
        this.baseUrl = "http://" + ip + ":" + port + "/";
    }

    public void login(LoginRequest loginRequest) {
        String data = (new Gson()).toJson(loginRequest);
        authenticate("login", data);
        if (success) {
            getPersons();
            getEvents();
        }
    }

    public void register(RegisterRequest registerRequest) {
        String data = (new Gson()).toJson(registerRequest);
        authenticate("register", data);
        if (success) {
            getPersons();
            getEvents();
        }
    }

    private void authenticate(String type, String data) {
        try {
            // Set up authentication connection
            success = true;
            URL logRegUrl = new URL(baseUrl + "user/" + type);
            HttpURLConnection logRegConn = (HttpURLConnection) logRegUrl.openConnection();

            logRegConn.setRequestMethod("POST");
            logRegConn.setRequestProperty("Content-Type", "application/json");
            logRegConn.setRequestProperty("Accept", "application/json");
            logRegConn.setDoOutput(true);

            logRegConn.connect();

            // Process authentication result
            byte[] out = data.getBytes();
            try (OutputStream os = logRegConn.getOutputStream()) {
                os.write(out);
            }

            if (logRegConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = logRegConn.getInputStream();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = responseBody.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                String logRegResult = outputStream.toString();

                // Set the authToken and personID for user
                String personID;
                if (type.equals("login")) {
                    loginResult = (new Gson()).fromJson(logRegResult, LoginResult.class);
                    DataCache.getInstance().setAuthToken(new AuthToken(loginResult.userName, loginResult.authToken));
                    personID = loginResult.personID;
                } else {
                    registerResult = (new Gson()).fromJson(logRegResult, RegisterResult.class);
                    DataCache.getInstance().setAuthToken(new AuthToken(registerResult.userName, registerResult.authToken));
                    personID = registerResult.personID;
                }
                DataCache.getInstance().setUserPersonId(personID);

                // Get first and last name for toast
                URL personUrl = new URL(baseUrl + "person/" + personID);
                String result = getData(personUrl);
                personResult = (new Gson()).fromJson(result, PersonResult.class);
                message = personResult.firstName + " " + personResult.lastName;
            } else {
                success = false;
                message = "Login or registration failed.";
            }
        } catch (IOException e) {
            success = false;
            message = "Server connection failed.";
        }
    }

    public void getPersons() {
        try {
            URL url = new URL(baseUrl + "person");
            String result = getData(url);
            personsResult = (new Gson()).fromJson(result, PersonsResult.class);
            DataCache.getInstance().setPersons(personsResult.data);
        } catch (IOException e) {
            success = false;
            message = "Server connection failed.";
        }
    }

    public void getEvents() {
        try {
            URL url = new URL(baseUrl + "event");
            String result = getData(url);
            eventsResult = (new Gson()).fromJson(result, EventsResult.class);
            DataCache.getInstance().setEvents(eventsResult.data);
        } catch (IOException e) {
            success = false;
            message = "Server connection failed.";
        }
    }

    private String getData(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        if (DataCache.getInstance().getAuthToken() == null) throw new IOException();
        conn.setRequestProperty("Authorization", DataCache.getInstance().getAuthToken().getToken());

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            success = true;
            InputStream responseBody = conn.getInputStream();

            OutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = responseBody.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            return outputStream.toString();
        } else {
            success = false;
            message = "Data retrieval failed.";
            return null;
        }
    }

    // Getters

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public RegisterResult getRegisterResult() {
        return registerResult;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public PersonResult getPersonResult() {
        return personResult;
    }

    public PersonsResult getPersonsResult() {
        return personsResult;
    }

    public EventsResult getEventsResult() {
        return eventsResult;
    }
}