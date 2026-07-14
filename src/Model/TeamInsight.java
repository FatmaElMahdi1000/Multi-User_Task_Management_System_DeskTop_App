package Model;
import service.*;
import DAO.*;
import Model.*;
import UI.*;

public class TeamInsight {

    private String userName;
    private int completedToday;
    private int completedALLTime;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCompletedToday() {
        return completedToday;
    }

    public void setCompletedToday(int completedToday) {
        this.completedToday = completedToday;
    }

    public int getCompletedALLTime() {
        return completedALLTime;
    }

    public void setCompletedALLTime(int completedALLTime) {
        this.completedALLTime = completedALLTime;
    }
}