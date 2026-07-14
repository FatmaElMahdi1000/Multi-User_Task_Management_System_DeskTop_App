package Model;

import java.time.LocalDateTime;

public class Task {

    private String taskId;
    private String title;
    private String description;
    //Instead of storing category names inside TASKS  each task stores a foreign key referencing the CATEGORIES table.
    private int categoryId;
    private String priority;
    private String status;
    private LocalDateTime deadline;

    private String formattedCreatedAt;
    private String formattedDeadline;


    //   Connects this specific task instance directly to its owner profile
    private UserProfile assignedUser; //Aggregation

    public Task() {}

    public UserProfile getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(UserProfile assignedUser) //association
    {
        this.assignedUser = assignedUser;
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public String getFormattedCreatedAt() { return formattedCreatedAt; }
    public void setFormattedCreatedAt(String formattedCreatedAt) { this.formattedCreatedAt = formattedCreatedAt; }

    public String getFormattedDeadline() { return formattedDeadline; }
    public void setFormattedDeadline(String formattedDeadline) { this.formattedDeadline = formattedDeadline; }
}