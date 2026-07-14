package Model;

import java.util.*;

public class UserProfile {

    //Added a list so that once the user's tasks
    // are loaded from the database,
    // the application can work with them in memory without querying the database repeatedly.
    private List<Task> UserTasks;

    private UUID UserID;
    private String UserName;
    private String UserEmail;
    private String password;

    public UserProfile(String UserName, String UserEmail, String password) {
        this.UserID = UUID.randomUUID();
        this.UserName = UserName;
        this.UserEmail = UserEmail;
        this.password = password;
        this.UserTasks = new ArrayList<>(); //Temporary memory
    }

    public String getUserEmail() { return UserEmail; }
    public void setUserEmail(String userEmail) { UserEmail = userEmail; }

    //Temporary data base for object mapping or Hydration
    public List<Task> getUserTasks() { return UserTasks; }


    public void setUserTasks(List<Task> userTasks) { UserTasks = userTasks; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserName() { return UserName; }
    public void setUserName(String userName) { UserName = userName; }

    public UUID getUserID() { return UserID; }
    public void setUserID(UUID userID) { UserID = userID; }
}