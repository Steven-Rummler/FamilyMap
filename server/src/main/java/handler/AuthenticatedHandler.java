package handler;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;

import java.sql.Connection;

public class AuthenticatedHandler {
    /**
     * Gets the AuthToken object for a token string
     * @param token
     * @return the AuthToken object
     */
    public AuthToken authenticate(String token) {
        try {
            Database database = new Database();
            database.openConnection();
            try {
                Connection conn = database.getConnection();
                AuthTokenDao authTokenDao = new AuthTokenDao(conn);
                AuthToken authToken = authTokenDao.find(token);
                database.closeConnection(true);
                return authToken;
            } catch (DataAccessException e) {
                database.closeConnection(false);
                throw e;
            }
        } catch (DataAccessException e) {
            return null;
        }
    }
}
