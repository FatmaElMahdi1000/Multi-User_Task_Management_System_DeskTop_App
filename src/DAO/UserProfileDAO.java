package DAO;

import Model.UserProfile;
import Model.Task;
import java.sql.*;

//DAO: running SQL , handles connection, converts result sets into
//java objects

//Methods
//Save , register new profile
//Find existing profile, retrieve tasks

//CRUD Create Read Update Delete
public class UserProfileDAO {

    //Creation
    public void save(UserProfile profile) throws SQLException {
        String sql = "INSERT INTO USER_PROFILES (USER_ID,USER_NAME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?)";

        try (Connection conn = DAO.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, profile.getUserID().toString());
                stmt.setString(2, profile.getUserName());
                stmt.setString(3, profile.getUserEmail());
                stmt.setString(4, profile.getPassword());

                stmt.executeUpdate();
                conn.commit();
                System.out.println("Profile saved successfully.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    //Read
    public boolean  EmailIsRegistered(String email) throws SQLException
    {
        String EmailSql = "SELECT * FROM USER_PROFILES WHERE EMAIL = ?";
        try(Connection conn = DAO.DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(EmailSql))
        {
            stmt.setString(1, email);
            try(ResultSet rs = stmt.executeQuery())
            {
                return rs.next(); //return true if email found.
            }
        }

    }

    //Read
    public UserProfile findByEmailAndPassword(String email, String password) throws SQLException {
        String joinSql = "SELECT u.USER_NAME, u.USER_ID, u.EMAIL, u.PASSWORD, " +
                "t.TASK_ID, t.TITLE, t.DESCRIPTION, t.CATEGORY_ID, t.PRIORITY, t.STATUS, t.DEADLINE " +
                "FROM USER_PROFILES u " +
                "LEFT JOIN TASKS t ON u.USER_ID = t.USER_ID " +
                "WHERE u.EMAIL = ? AND u.PASSWORD = ? " +
                "ORDER BY t.DEADLINE ASC";

        //Null since we still don't know if the user is in the database or not.
        UserProfile profile = null;

        try (Connection conn = DAO.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(joinSql)) {
            //the parameters
            stmt.setString(1, email);
            stmt.setString(2, password);


            try (ResultSet rs = stmt.executeQuery()) {
                boolean profileInitialized = false;

                while (rs.next()) {
                    if (!profileInitialized) {
                        String dbEmail = rs.getString("EMAIL");
                        String dbPassword = rs.getString("PASSWORD");

                        //I forgot to add Username:
                        String dbUSER_Name = rs.getString("USER_NAME");

                        profile = new UserProfile(dbUSER_Name, dbEmail, dbPassword);

                        //the UserProfile object contains UUID, expect id in UUID(type), we're converting the ID from string to UUID
                        //Conversion from String to UUID type.
                        profile.setUserID(java.util.UUID.fromString(rs.getString("USER_ID")));
                        profileInitialized = true;
                    }


                    //Object Mapping/Hydrating an object part
                    String taskId = rs.getString("TASK_ID");
                    if (taskId != null) {

                        Task task = new Task();

                        task.setTaskId(taskId);
                        task.setTitle(rs.getString("TITLE"));
                        task.setDescription(rs.getString("DESCRIPTION"));
                        task.setCategoryId(rs.getInt("CATEGORY_ID"));
                        task.setPriority(rs.getString("PRIORITY"));
                        task.setStatus(rs.getString("STATUS"));
                        Timestamp ts = rs.getTimestamp("DEADLINE");
                        if (ts != null) {
                            task.setDeadline(ts.toLocalDateTime());
                        }

                        //where the connection happens::Connecting the task to its owner
                        task.setAssignedUser(profile);
                        profile.getUserTasks().add(task);
                    }
                }
            }
        }
        return profile;
    }


    //for forgetting pass
    public String findPasswordByEmail(String email) throws SQLException {
        String sql = "SELECT PASSWORD FROM USER_PROFILES WHERE EMAIL = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("PASSWORD");
                }
            }

            throw new IllegalArgumentException("Email not found.");
        }
    }


}