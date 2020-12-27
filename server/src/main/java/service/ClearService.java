package service;

import dao.DataAccessException;
import dao.Database;
import result.ClearResult;

import java.sql.Connection;

public class ClearService {
    /***
     * Clears the database completely,
     * dropping all rows from all tables.
     * @return ClearResult
     */
    public ClearResult clear() {
        ClearResult result = new ClearResult();

        try {
            Database database = new Database();
            database.openConnection();
            Connection conn = database.getConnection();
            try {
                database.clearTables();

                database.closeConnection(true);

                result.message = "clear succeeded";
                result.success = true;
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
