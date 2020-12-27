package dao;

import helper.StringJsonHelper;
import model.AuthToken;
import model.Event;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthTokenDao {
    private final Connection conn;

    /**
     * Connects to the database
     *
     * @param conn Database Connection Object
     */
    public AuthTokenDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts a new auth token into the database
     *
     * @param authToken The auth token to insert
     */
    public void insert(AuthToken authToken) throws DataAccessException {
        String sql = "INSERT INTO AuthTokens (token, username) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, authToken.getToken());
            stmt.setString(2, authToken.getUser());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    /**
     * Returns the authToken with the given string
     *
     * @param token
     * @return AuthToken
     */
    public AuthToken find(String token) throws DataAccessException {
        AuthToken authToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthTokens WHERE token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if (rs.next()) {
                authToken = new AuthToken(rs.getString("username"), rs.getString("token"));
                return authToken;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding authToken");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Authenticates a user
     * @param username the user request the token
     * @return a valid auth token
     * @throws DataAccessException
     */
    public String authenticate(String username) throws DataAccessException {
        AuthToken authToken;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthTokens WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("token");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding authToken");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Generates a random token string and confirms that it is not in use
     * @return A random token string
     * @throws DataAccessException
     */
    public String generateToken() throws DataAccessException {
        boolean generated = false;
        int tries = 0;
        String token = "";
        while (!generated && tries < 20) {
            tries++;
            token = StringJsonHelper.getRandomString(16);
            if (find(token) == null) {
                generated = true;
            }
        }
        if (tries < 20) {
            return token;
        }
        return "";
    }
}
