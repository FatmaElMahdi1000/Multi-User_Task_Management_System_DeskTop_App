package service;

import Model.Task;
import DAO.TaskDAO;
import Model.TeamInsight;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TaskService {
    private final TaskDAO taskDAO = new TaskDAO();

    public void createNewTask(Task task, String userId) throws SQLException {
        taskDAO.createTask(task, userId);
    }
    public void updateExistingTask(Task task, String userId) throws SQLException {
        taskDAO.updateTask(task, userId);
    }

    public void removeTaskPermanently(String taskId, String taskTitle, String userId) throws SQLException {
        taskDAO.deleteTaskPermanently(taskId, taskTitle, userId);
    }

    public List<Task> getActiveTasksByUserId(String userId) throws SQLException {
        return taskDAO.getAllTasksByUserId(userId);
    }

    public void delayTaskToTomorrow(String taskId) throws SQLException {
        taskDAO.moveTaskToTomorrow(taskId);
    }

    public void completeTask(String taskId) throws SQLException {
        taskDAO.completeTask(taskId);
    }

    //this is to be used in the main app: Storing categories in a map
    //we've categories as Foreign keys in the TASKS table:
    //I need to program to store: FK(Key) : Value (Equivalent category)
    public Map<Integer, String> getCategoryOptions() throws SQLException {
        return taskDAO.getAllCategories();
    }

    public List<String[]> getActivityLogs(LocalDate start, LocalDate end, String userId) throws java.sql.SQLException {
        return taskDAO.getLogsByDateRange(start, end, userId);
    }

    public List<TeamInsight> getTodayCompletedTask()
            throws SQLException {

        return taskDAO.GetCompletedTaskTodayPerUser();
    }

    public List<TeamInsight> getAllTimeCompletedTask()
            throws SQLException {

        return taskDAO.getCompletedTaskAllTimePerUser();
    }

}
