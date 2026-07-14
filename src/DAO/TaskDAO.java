package DAO;
import Model.TeamInsight;
import Model.Task;
import oracle.jdbc.replay.ConnectionInitializationCallback;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TaskDAO {

    public void createTask(Task task, String userId) throws SQLException {
        String insertTaskSql = "INSERT INTO TASKS (TASK_ID, TITLE, DESCRIPTION, CATEGORY_ID, PRIORITY, STATUS, DEADLINE, USER_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String logSql = "INSERT INTO TASK_LOGS (TASK_ID, ACTION) VALUES (?, ?)";

        task.setTaskId(UUID.randomUUID().toString());

        try (Connection conn = DAO.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(insertTaskSql);
                 PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                stmt.setString(1, task.getTaskId());
                stmt.setString(2, task.getTitle());
                stmt.setString(3, task.getDescription());
                stmt.setInt(4, task.getCategoryId());
                stmt.setString(5, task.getPriority());
                stmt.setString(6, task.getStatus() != null ? task.getStatus() : "TODO");
                stmt.setTimestamp(7, Timestamp.valueOf(task.getDeadline()));
                stmt.setString(8, userId);
                stmt.executeUpdate();

                logStmt.setString(1, task.getTaskId());
                logStmt.setString(2, "Task Created: " + task.getTitle());
                logStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void updateTask(Task task, String userId) throws SQLException {
        String updateSql = "UPDATE TASKS SET TITLE = ?, DESCRIPTION = ?, CATEGORY_ID = ?, PRIORITY = ?, STATUS = ?, DEADLINE = ? WHERE TASK_ID = ? AND USER_ID = ?";
        String logSql = "INSERT INTO TASK_LOGS (TASK_ID, ACTION) VALUES (?, ?)";

        try (Connection conn = DAO.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(updateSql);
                 PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                stmt.setString(1, task.getTitle());
                stmt.setString(2, task.getDescription());
                stmt.setInt(3, task.getCategoryId());
                stmt.setString(4, task.getPriority());
                stmt.setString(5, task.getStatus());
                stmt.setTimestamp(6, Timestamp.valueOf(task.getDeadline()));
                stmt.setString(7, task.getTaskId());
                stmt.setString(8, userId);
                stmt.executeUpdate();

                logStmt.setString(1, task.getTaskId());
                logStmt.setString(2, "Task Edited/Updated: " + task.getTitle() + " (" + task.getStatus() + ")");
                logStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void deleteTaskPermanently(String taskId, String taskTitle, String userId) throws SQLException {
        String logSql = "INSERT INTO TASK_LOGS (TASK_ID, ACTION) VALUES (?, ?)";
        String deleteSql = "DELETE FROM TASKS WHERE TASK_ID = ? AND USER_ID = ?";

        try (Connection conn = DAO.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement logStmt = conn.prepareStatement(logSql)) {
                    logStmt.setString(1, taskId);
                    logStmt.setString(2, "PERMANENT DELETION - Task Title: " + taskTitle);
                    logStmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                    stmt.setString(1, taskId);
                    stmt.setString(2, userId);
                    stmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void processOverdueTasks() throws SQLException {
        String updateSql = "UPDATE TASKS SET STATUS = 'OVERDUE' WHERE DEADLINE < CURRENT_TIMESTAMP AND STATUS <> 'DONE' AND STATUS <> 'OVERDUE'";
        try (Connection conn = DAO.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.executeUpdate();
        }
    }

    public List<Task> getAllTasksByUserId(String userId) throws SQLException {
        processOverdueTasks();
        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT TASK_ID, TITLE, DESCRIPTION, CATEGORY_ID, PRIORITY, STATUS, " +
                "TO_CHAR(CREATED_AT, 'YYYY-MM-DD HH12:MI AM') AS FORM_CREATED, " +
                "TO_CHAR(DEADLINE, 'YYYY-MM-DD HH12:MI AM') AS FORM_DEADLINE, " +
                "TO_CHAR(COMPLETED_AT, 'YYYY-MM-DD') AS COMP_DATE_RAW, " +
                "DEADLINE AS RAW_DEADLINE " +
                "FROM TASKS WHERE USER_ID = ? ORDER BY DEADLINE ASC";

        try (Connection conn = DAO.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setTaskId(rs.getString("TASK_ID"));
                    task.setTitle(rs.getString("TITLE"));
                    task.setDescription(rs.getString("DESCRIPTION"));
                    task.setCategoryId(rs.getInt("CATEGORY_ID"));
                    task.setPriority(rs.getString("PRIORITY"));
                    task.setStatus(rs.getString("STATUS"));
                    task.setFormattedCreatedAt(rs.getString("FORM_CREATED"));

                    if ("DONE".equals(task.getStatus()) && rs.getString("COMP_DATE_RAW") != null) {
                        task.setFormattedDeadline(rs.getString("COMP_DATE_RAW") + " (Completed)");
                    } else {
                        task.setFormattedDeadline(rs.getString("FORM_DEADLINE"));
                    }

                    Timestamp ts = rs.getTimestamp("RAW_DEADLINE");
                    if (ts != null) {
                        task.setDeadline(ts.toLocalDateTime());
                    }

                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public void moveTaskToTomorrow(String taskId) throws SQLException {
        String updateSql = "UPDATE TASKS SET DEADLINE = CURRENT_TIMESTAMP + 1, STATUS = 'TODO' WHERE TASK_ID = ?";
        String logSql = "INSERT INTO TASK_LOGS (TASK_ID, ACTION) VALUES (?, 'Task deferred to tomorrow')";

        try (Connection conn = DAO.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(updateSql);
                 PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                stmt.setString(1, taskId);
                stmt.executeUpdate();
                logStmt.setString(1, taskId);
                logStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void completeTask(String taskId) throws SQLException {
        String updateSql = "UPDATE TASKS SET STATUS = 'DONE', COMPLETED_AT = CURRENT_TIMESTAMP WHERE TASK_ID = ?";
        String logSql = "INSERT INTO TASK_LOGS (TASK_ID, ACTION) VALUES (?, 'Status changed: DONE')";

        try (Connection conn = DAO.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(updateSql);
                 PreparedStatement logStmt = conn.prepareStatement(logSql)) {

                stmt.setString(1, taskId);
                stmt.executeUpdate();
                logStmt.setString(1, taskId);
                logStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    //Get Completed Tasks today per users for: Team insights tab:
    public List<TeamInsight>  GetCompletedTaskTodayPerUser() throws SQLException
    {
        String ComSql = "SELECT u.USER_NAME, COUNT(t.TASK_ID) AS COMPLETED_TODAY " +
                "FROM USER_PROFILES u LEFT JOIN TASKS t ON u.USER_ID = t.USER_ID " +
                "AND t.STATUS = 'DONE' AND TRUNC(t.COMPLETED_AT) = TRUNC(SYSDATE) " +
                "GROUP BY u.USER_ID, u.USER_NAME " +
                "ORDER BY COMPLETED_TODAY DESC";

        try(Connection conn = DatabaseConnection.getConnection())
        {
            try (PreparedStatement stmt = conn.prepareStatement(ComSql))
            {
                ResultSet rs =  stmt.executeQuery();

            //list that return an instance with(username, completed tasks today or all time)
                List<TeamInsight> ranking = new ArrayList<>();

                while(rs.next())
                {
                    //instance of the team insight model/class
                    TeamInsight insight = new TeamInsight();

                    insight.setUserName(rs.getString("USER_NAME"));
                    insight.setCompletedToday(rs.getInt("COMPLETED_TODAY"));
                    ranking.add(insight);
                }
            return ranking;

    }
    }

    }

    //Get Completed Tasks (All Time) per users for: Team Insights tab
    public List<TeamInsight> getCompletedTaskAllTimePerUser() throws SQLException {

        String comSql =
                "SELECT u.USER_NAME, COUNT(t.TASK_ID) AS COMPLETED_ALL_TIME " +
                        "FROM USER_PROFILES u " +
                        "LEFT JOIN TASKS t ON u.USER_ID = t.USER_ID " +
                        "AND t.STATUS = 'DONE' " +
                        "GROUP BY u.USER_ID, u.USER_NAME " +
                        "ORDER BY COMPLETED_ALL_TIME DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(comSql)) {

            ResultSet rs = stmt.executeQuery();

            // List that returns an instance with (username, completed tasks all time)
            List<TeamInsight> ranking = new ArrayList<>();

            while (rs.next()) {

                // Instance of the TeamInsight model
                TeamInsight insight = new TeamInsight();

                insight.setUserName(rs.getString("USER_NAME"));
                insight.setCompletedALLTime(rs.getInt("COMPLETED_ALL_TIME"));

                ranking.add(insight);
            }

            return ranking;
        }
    }


    //this is to be used in the main app: Storing categories in a map
    //we've categories as Foreign keys in the TASKS table:
    //I need to program to store: FK(Key) : Value (Equivalent category)

    public Map<Integer, String> getAllCategories() throws SQLException {
        Map<Integer, String> categories = new LinkedHashMap<>();
        String sql = "SELECT CATEGORY_ID, CATEGORY_NAME FROM CATEGORIES ORDER BY CATEGORY_ID ASC";

        try (Connection conn = DAO.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.put(rs.getInt("CATEGORY_ID"),
                        rs.getString("CATEGORY_NAME"));
            }
        }
        return categories;
    }


    //  Fetch Activity Logs across Custom Date Ranges (Isolated by User ID)
    public List<String[]> getLogsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate, String userId) throws SQLException {
        List<String[]> logs = new ArrayList<>();

        String sql = "SELECT L.LOG_ID, NVL(T.TITLE, 'DELETED TASK REFERENCE') AS COMP_TITLE, L.ACTION, TO_CHAR(L.ACTION_TIME, 'YYYY-MM-DD HH12:MI AM') AS FORMATTED_TIME " +
                "FROM TASK_LOGS L " +
                "LEFT JOIN TASKS T ON L.TASK_ID = T.TASK_ID " +
                "WHERE TRUNC(L.ACTION_TIME) BETWEEN ? AND ? AND T.USER_ID = ? " +
                "ORDER BY L.ACTION_TIME DESC";

        try (Connection conn = DAO.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            stmt.setString(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new String[]{
                            rs.getString("LOG_ID"),
                            rs.getString("COMP_TITLE"),
                            rs.getString("ACTION"),
                            rs.getString("FORMATTED_TIME")
                    });
                }
            }
        }
        return logs;
    }



}


















