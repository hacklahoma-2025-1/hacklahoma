package tech.touchgrass.db.classes.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.touchgrass.db.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDatabase {

    private static final Logger logger = LoggerFactory.getLogger(UserDatabase.class);

    public static void initializeUserDatabase() {
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (\n"
                + "    userID TEXT PRIMARY KEY,\n"
                + "    username VARCHAR(50) NOT NULL UNIQUE,\n"
                + "    email VARCHAR(100) NOT NULL UNIQUE,\n"
                + "    passwordHash VARCHAR(255) NOT NULL,\n"
                + "    salt VARCHAR(255) NOT NULL UNIQUE,\n"
                + "    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "    lastLogin TIMESTAMP NULL DEFAULT NULL,\n"
                + "    isActive BOOLEAN DEFAULT TRUE\n"
                + ");";
        try {
            DatabaseManager.executeQuery(createUsersTableSQL);
        } catch (SQLException e) {
            logger.error("Error creating users table", e);
            throw new RuntimeException(e);
        }
    }

    public static boolean createUser(User user) {
        String createUserSQL = "INSERT INTO users (UserID, username, email, passwordHash, salt) VALUES ('"
                + user.getUserID() + "', '"
                + user.getUsername() + "', '"
                + user.getEmail() + "', '"
                + user.getPasswordHash() + "', '"
                + user.getSalt() + "');";
        try {
            int rowsAffected = DatabaseManager.getConnection()
                    .createStatement()
                    .executeUpdate(createUserSQL);
            return rowsAffected > 0; // Returns true if a row was inserted
        } catch (SQLException e) {
            logger.error("Error creating user", e);
            return false; // Returns false if an exception occurred
        }
    }


    public static User getUser(String username) {
        String getUserSQL = "SELECT * FROM users WHERE username = '" + username + "';";
        try (Statement statement = DatabaseManager.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(getUserSQL)) {

            if (resultSet.next()) {  // Move cursor to the first row
                return new User(resultSet);
            } else {
                return null; // No user found with the given username
            }
        } catch (SQLException e) {
            logger.error("Error retrieving user", e);
            return null;
        }
    }

    public static boolean userExists(String email, String username) {
        String userExistsSQL = "SELECT 1 FROM users WHERE email = ? OR username = ?";
        try (PreparedStatement preparedStatement = DatabaseManager.getConnection().prepareStatement(userExistsSQL)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a row is found
            }
        } catch (SQLException e) {
            logger.error("Error checking if user exists with email: " + email + " or username: " + username, e);
            return false; // Returns false if an exception occurred
        }
    }

    public static boolean updateUserPassword(String userID, String newPasswordHash, String newSalt) {
        String updatePasswordSQL = "UPDATE users SET passwordHash = ?, salt = ? WHERE userID = ?";
        try (PreparedStatement preparedStatement = DatabaseManager.getConnection().prepareStatement(updatePasswordSQL)) {
            preparedStatement.setString(1, newPasswordHash);
            preparedStatement.setString(2, newSalt);
            preparedStatement.setString(3, userID);


            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Returns true if a row was updated
        } catch (SQLException e) {
            logger.error("Error updating password for userID: " + userID, e);
            return false; // Returns false if an exception occurred
        }
    }
}
