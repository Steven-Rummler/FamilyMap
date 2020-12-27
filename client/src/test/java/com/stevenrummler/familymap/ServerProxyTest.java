package com.stevenrummler.familymap;

import com.stevenrummler.familymap.data.DataCache;
import com.stevenrummler.familymap.data.ServerProxy;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import model.User;
import request.LoginRequest;
import request.RegisterRequest;
import result.RegisterResult;

public class ServerProxyTest {
    String ip = "localhost";
    String port = "8080";
    User user;

    private String newUsername() {
        return RandomStringUtils.randomAlphabetic(16);
    }

    @Test
    public void loginPass() {
        ServerProxy serverProxy = new ServerProxy(ip, port);

        DataCache.getInstance().setAuthToken(null);

        // Register a new user
        String username = newUsername();
        user = new User(username, "password", "email",
                "first", "last", "gender", "id");
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.userName = user.getUserName();
        registerRequest.password = user.getPassword();
        registerRequest.firstName = user.getFirstName();
        registerRequest.lastName = user.getLastName();
        registerRequest.email = user.getEmail();
        registerRequest.gender = user.getGender();
        serverProxy.register(registerRequest);
        RegisterResult registerResult = serverProxy.getRegisterResult();
        String personID = registerResult.personID;
        // Assertions
        Assert.assertTrue(serverProxy.getRegisterResult().success);
        Assert.assertTrue(serverProxy.isSuccess());
        Assert.assertNotNull(DataCache.getInstance().getAuthToken());

        DataCache.getInstance().setAuthToken(null);

        // Log them in
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.userName = user.getUserName();
        loginRequest.password = user.getPassword();
        serverProxy.login(loginRequest);
        //Assertions
        Assert.assertTrue(serverProxy.getLoginResult().success);
        Assert.assertTrue(serverProxy.isSuccess());
        Assert.assertEquals(personID, serverProxy.getPersonResult().personID);
        Assert.assertNotNull(DataCache.getInstance().getAuthToken());
    }

    @Test
    public void loginFail() {
        ServerProxy serverProxy = new ServerProxy(ip, port);

        DataCache.getInstance().setAuthToken(null);

        // Attempt to log in a user that doesn't exist
        String username = newUsername();
        user = new User(username, "password", "email",
                "first", "last", "gender", "id");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.userName = user.getUserName();
        loginRequest.password = user.getPassword();
        serverProxy.login(loginRequest);
        //Assertions
        Assert.assertFalse(serverProxy.isSuccess());
        Assert.assertNull(DataCache.getInstance().getAuthToken());

        DataCache.getInstance().setAuthToken(null);

        // Register the user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.userName = user.getUserName();
        registerRequest.password = user.getPassword();
        registerRequest.firstName = user.getFirstName();
        registerRequest.lastName = user.getLastName();
        registerRequest.email = user.getEmail();
        registerRequest.gender = user.getGender();
        serverProxy.register(registerRequest);
        // Assertions
        Assert.assertTrue(serverProxy.getRegisterResult().success);
        Assert.assertTrue(serverProxy.isSuccess());
        Assert.assertNotNull(DataCache.getInstance().getAuthToken());

        DataCache.getInstance().setAuthToken(null);

        // Login them in with the wrong password and confirm that it fails
        loginRequest = new LoginRequest();
        loginRequest.userName = user.getUserName();
        loginRequest.password = "wrong" + user.getPassword();
        serverProxy.login(loginRequest);
        Assert.assertFalse(serverProxy.isSuccess());
        Assert.assertNull(DataCache.getInstance().getAuthToken());
    }

    @Test
    public void registerPass() {
        ServerProxy serverProxy = new ServerProxy(ip, port);

        DataCache.getInstance().setAuthToken(null);

        // Register a new user
        String username = newUsername();
        user = new User(username, "password", "email",
                "first", "last", "gender", "id");
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.userName = user.getUserName();
        registerRequest.password = user.getPassword();
        registerRequest.firstName = user.getFirstName();
        registerRequest.lastName = user.getLastName();
        registerRequest.email = user.getEmail();
        registerRequest.gender = user.getGender();
        serverProxy.register(registerRequest);
        // Assertions
        Assert.assertTrue(serverProxy.getRegisterResult().success);
        Assert.assertTrue(serverProxy.isSuccess());
        Assert.assertNotNull(DataCache.getInstance().getAuthToken());

        DataCache.getInstance().setAuthToken(null);

        // Log them in
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.userName = user.getUserName();
        loginRequest.password = user.getPassword();
        serverProxy.login(loginRequest);
        //Assertions
        Assert.assertTrue(serverProxy.getLoginResult().success);
        Assert.assertTrue(serverProxy.isSuccess());
        Assert.assertNotNull(DataCache.getInstance().getAuthToken());
    }

    @Test
    public void registerFail() {
        ServerProxy serverProxy = new ServerProxy(ip, port);

        DataCache.getInstance().setAuthToken(null);

        // Register a new user
        String username = newUsername();
        user = new User(username, "password", "email",
                "first", "last", "gender", "id");
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.userName = user.getUserName();
        registerRequest.password = user.getPassword();
        registerRequest.firstName = user.getFirstName();
        registerRequest.lastName = user.getLastName();
        registerRequest.email = user.getEmail();
        registerRequest.gender = user.getGender();
        serverProxy.register(registerRequest);
        // Assertions
        Assert.assertTrue(serverProxy.getRegisterResult().success);
        Assert.assertTrue(serverProxy.isSuccess());
        Assert.assertNotNull(DataCache.getInstance().getAuthToken());

        DataCache.getInstance().setAuthToken(null);

        // Register them again
        serverProxy.register(registerRequest);
        // Assertions
        Assert.assertFalse(serverProxy.isSuccess());
        Assert.assertNull(DataCache.getInstance().getAuthToken());
    }

    @Test
    public void personsPass() {
        ServerProxy serverProxy = new ServerProxy(ip, port);

        DataCache.getInstance().setAuthToken(null);
        if (DataCache.getInstance().getPersons() != null)
            DataCache.getInstance().getPersons().clear();

        // Register a new user
        String username = newUsername();
        user = new User(username, "password", "email",
                "first", "last", "gender", "id");
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.userName = user.getUserName();
        registerRequest.password = user.getPassword();
        registerRequest.firstName = user.getFirstName();
        registerRequest.lastName = user.getLastName();
        registerRequest.email = user.getEmail();
        registerRequest.gender = user.getGender();
        serverProxy.register(registerRequest);

        // Get persons for them
        serverProxy.getPersons();
        Assert.assertTrue(serverProxy.getPersonsResult().success);
        Assert.assertTrue(serverProxy.isSuccess());
        Assert.assertFalse(DataCache.getInstance().getPersons().isEmpty());
    }

    @Test
    public void personsFail() {
        ServerProxy serverProxy = new ServerProxy(ip, port);

        DataCache.getInstance().setAuthToken(null);
        if (DataCache.getInstance().getPersons() != null)
            DataCache.getInstance().getPersons().clear();

        // Try to get persons without logging in
        serverProxy.getPersons();
        Assert.assertFalse(serverProxy.isSuccess());
        Assert.assertTrue(DataCache.getInstance().getPersons().isEmpty());
    }

    @Test
    public void eventsPass() {
        ServerProxy serverProxy = new ServerProxy(ip, port);

        DataCache.getInstance().setAuthToken(null);
        if (DataCache.getInstance().getEvents() != null)
            DataCache.getInstance().getEvents().clear();

        // Register a new user
        String username = newUsername();
        user = new User(username, "password", "email",
                "first", "last", "gender", "id");
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.userName = user.getUserName();
        registerRequest.password = user.getPassword();
        registerRequest.firstName = user.getFirstName();
        registerRequest.lastName = user.getLastName();
        registerRequest.email = user.getEmail();
        registerRequest.gender = user.getGender();
        serverProxy.register(registerRequest);

        // Get persons for them
        serverProxy.getEvents();
        Assert.assertTrue(serverProxy.getEventsResult().success);
        Assert.assertTrue(serverProxy.isSuccess());
        Assert.assertFalse(DataCache.getInstance().getEvents().isEmpty());
    }

    @Test
    public void eventsFail() {
        ServerProxy serverProxy = new ServerProxy(ip, port);

        DataCache.getInstance().setAuthToken(null);
        if (DataCache.getInstance().getEvents() != null)
            DataCache.getInstance().getEvents().clear();

        // Try to get events without logging in
        serverProxy.getEvents();
        Assert.assertFalse(serverProxy.isSuccess());
        Assert.assertTrue(DataCache.getInstance().getEvents().isEmpty());
    }
}