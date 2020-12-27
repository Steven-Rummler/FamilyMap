package model;

import java.util.Objects;

public class AuthToken {
    /**
     * A random string used to persist a logged-in session across multiple requests
     */
    String token;
    /**
     * The username of the user associated with this AuthToken
     */
    String user;

    /**
     * Creates a new AuthToken object for a specific user and
     * generates a new random string as the authorization token.
     *
     * @param user The user with which this AuthToken will be associated
     */
    public AuthToken(String user, String token) {
        this.user = user;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return Objects.equals(token, authToken.token) &&
                Objects.equals(user, authToken.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, user);
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}
