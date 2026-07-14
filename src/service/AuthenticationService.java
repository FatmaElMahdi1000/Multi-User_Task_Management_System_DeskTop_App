package service;

import Model.UserProfile;
import DAO.UserProfileDAO;
import java.sql.SQLException;
import java.util.UUID;

public class AuthenticationService {

    //An instance/ Object of our DAO for the database connection
    private final UserProfileDAO userProfileDAO = new UserProfileDAO();

    //  LOGIN
    //Below method Return user's profile:
    public UserProfile loginSystemPortal(String email, String password) throws SQLException {

        if (email.trim().isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Credentials cannot be left blank.");
        }

        //ELSE:   Email and User Fields are not Empty
        //We need to check the database if these are found.
        UserProfile profile =
                userProfileDAO.findByEmailAndPassword(email.trim(), password);

        if (profile == null) {
            throw new SecurityException(
                    "Incorrect email or password. Please try again.");
        }

        return profile;
    }

    //   REGISTRATION
    public void registerNewProfile(String name,
                                   String email,
                                   String password) throws Exception {

        if (name.trim().isEmpty()
                || email.trim().isEmpty()
                || password.isEmpty()) {

            throw new IllegalArgumentException(
                    "All fields are required.");
        }
        //if the email only in the database, throw an error: email already registered
        else if(userProfileDAO.EmailIsRegistered(email.trim())) //if this evaluates to true
        {
            throw new IllegalArgumentException("The email is already registered!");
        }
        UserProfile newProfile =
                new UserProfile(name, email.trim(), password);

        newProfile.setUserID(UUID.randomUUID());

        userProfileDAO.save(newProfile);
    }


    public String recoverPassword(String email)
            throws SQLException {

        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter your email.");
        }

        return userProfileDAO.findPasswordByEmail(email);
    }
}