import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import java.awt.*;


public class Task {
    private String name;
    private Date dueDate;
    private String priority;
    private String status;
    private ArrayList<Task> dependencies = new ArrayList<>();


    public Task(String name, Date dueDate, String priority, String status)
    {
        this.name = name;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;


    }

    public void addDependency(Task task) {
        if (!this.dependencies.contains(task)) {
            this.dependencies.add(task);
        }
    }

    // Add a method to check if all dependencies are completed
    public boolean canComplete() {
        for (Task task : this.dependencies) {
            if (!"Completed".equals(task.getStatus())) {
                return false;
            }
        }
        return true;
    }

    public String getName(){
        return name;
    }

    public Date getDueDate(){
        return dueDate;
    }

    public String getPriority(){
        return priority;
    }

    public String getStatus(){
        return status;
    }


    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(dueDate);
        return "Task Name: " + name + "\n\n" +
                "Due Date: " + formattedDate + "\n\n" +
                "Priority: " + priority + "\n\n" +
                "Status: " + status + "\n";
    }


}
