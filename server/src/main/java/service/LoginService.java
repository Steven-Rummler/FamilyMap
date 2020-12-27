package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.AuthToken;
import model.User;
import request.LoginRequest;
import result.LoginResult;

import java.sql.Connection;

public class LoginService {
    /***
     * Creates a new AuthToken object associated
     * with the User described in the request
     * @param r LoginRequest
     * @return LoginResult
     */
    public LoginResult login(LoginRequest r) {
        LoginResult result = new LoginResult();
        try {
            // Connect to database, create result object
            Database database = new Database();
            database.openConnection();
            try {
                Connection conn = database.getConnection();
                UserDao userDao = new UserDao(conn);
                AuthTokenDao authTokenDao = new AuthTokenDao(conn);

                // Check if the username exists
                User user = userDao.find(r.userName);
                if (user == null) {
                    result.message = "error: invalid username";
                    result.success = false;
                } else {
                    // Check if the password is correct
                    if (user.getPassword().equals(r.password)) {
                        // Create a new auth token for the user
                        AuthToken newAuthToken = new AuthToken(user.getUserName(), authTokenDao.generateToken());
                        authTokenDao.insert(newAuthToken);

                        result.authToken = newAuthToken.getToken();
                        result.userName = user.getUserName();
                        result.personID = user.getPersonID();
                        result.success = true;

                    } else {
                        result.message = "error: invalid password";
                        result.success = false;
                    }
                }

                database.closeConnection(true);

                return result;
            } catch (DataAccessException e) {
                database.closeConnection(false);
                throw e;
            }
        } catch (DataAccessException e) {
            result.message = "error: could not connect to database";
            result.success = false;
            return result;
        }
    }
}
