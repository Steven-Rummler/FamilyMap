package service;

import dao.*;
import model.AuthToken;
import model.User;
import model.Person;
import request.FillRequest;
import request.RegisterRequest;
import result.RegisterResult;

import java.sql.Connection;

public class RegisterService {
    /***
     * Creates a row in the User table,
     * creates a row in the Person table,
     * and generates four generations of ancestors for that Person
     * with Events for the User's Person and all ancestors.
     * @param r RegisterRequest
     * @return RegisterResult
     */
    public RegisterResult register(RegisterRequest r) {
        RegisterResult result = new RegisterResult();
        try {
            Database database = new Database();
            database.openConnection();
            Connection conn = database.getConnection();
            UserDao userDao = new UserDao(conn);
            PersonDao personDao = new PersonDao(conn);
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);

            try {

                // Generate a person ID
                String personID = personDao.generateID();
                String token = authTokenDao.generateToken();

                Person person = new Person(personID, r.userName, r.firstName, r.lastName, r.gender, "", "", "");
                User user = new User(r.userName, r.password, r.email, r.firstName, r.lastName, r.gender, personID);
                AuthToken authToken = new AuthToken(r.userName, token);

                userDao.insert(user);
                personDao.insert(person);
                authTokenDao.insert(authToken);

                database.closeConnection(true);

                FillRequest fillRequest = new FillRequest();
                fillRequest.user = r.userName;
                fillRequest.generations = 4;
                FillService fillService = new FillService();
                fillService.fill(fillRequest);

                result.success = true;
                result.authToken = authToken.getToken();
                result.userName = user.getUserName();
                result.personID = person.getPersonID();

                return result;
            } catch (DataAccessException e) {
                database.closeConnection(false);
                throw e;
            }
        } catch (DataAccessException e) {
            result.message = "database error";
            result.success = false;
            return result;
        }
    }
}
