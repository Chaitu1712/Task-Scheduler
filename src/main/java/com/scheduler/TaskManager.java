package com.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class TaskManager {
    private List<Task> tasks =new ArrayList<>();
    private volatile boolean running = true;

    public TaskManager() {
        loadTasksFromDatabase();
    }
    public void loadTasksFromDatabase(){
        String query="SELECT * FROM tasks order by idx";
        try(Connection conn =databaseUtil.getConnection()){
            PreparedStatement pstmt= conn.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery(query);
            while(rs.next()){
                Task task=new Task(rs.getInt("idx"),rs.getString("title"),rs.getString("description"),rs.getInt("priority"),rs.getTimestamp("deadline").toLocalDateTime(),rs.getString("status"));
                tasks.add(task);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    // Add Task
    public void addTask(Task task) {
        String query="INSERT INTO tasks(idx,title,description,priority,deadline,status) VALUES(?,?,?,?,?,?)";
        try(Connection conn =databaseUtil.getConnection()){
            PreparedStatement pstmt= conn.prepareStatement(query);
            pstmt.setInt(1,task.getindex());
            pstmt.setString(2,task.getTitle());
            pstmt.setString(3,task.getDescription());
            pstmt.setInt(4,task.getPriority());
            pstmt.setTimestamp(5,Timestamp.valueOf(task.getDeadline()));
            pstmt.setString(6,task.getStatus());
            int rowsadded=pstmt.executeUpdate();
            if(rowsadded>0)
            tasks.add(task);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    // Update Task
    public void updateTask(int index, Task task) {
        if (index >= 0 && index < tasks.size()) {
            Task temp=tasks.get(index);
            String title=task.getTitle();
            String description=task.getDescription();
            LocalDateTime deadline=task.getDeadline();
            String status=task.getStatus();
            int priority=task.getPriority();
            if(!title.equals(""))
            temp.setTitle(title);
            if(!description.equals(""))
            temp.setDescription(description);
            if(deadline!=null)
            temp.setDeadline(deadline);
            if(priority!=-1)
            temp.setPriority(priority);
            if(!temp.getStatus().equals(status))
            temp.setStatus(status);
            String query="UPDATE tasks SET title=?,description=?,priority=?,deadline=?,status=? WHERE idx=?";
            try(Connection conn =databaseUtil.getConnection()){
                PreparedStatement pstmt= conn.prepareStatement(query);
                pstmt.setString(1,temp.getTitle());
                pstmt.setString(2,temp.getDescription());
                pstmt.setInt(3,temp.getPriority());
                pstmt.setTimestamp(4,Timestamp.valueOf(temp.getDeadline()));
                pstmt.setString(5,temp.getStatus());
                pstmt.setInt(6,temp.getindex());
                int rowsupdated=pstmt.executeUpdate();
                if(rowsupdated>0){
                    tasks.set(index,temp);
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    // Delete Task
    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            String query="DELETE FROM tasks WHERE idx=?";

            try(Connection conn =databaseUtil.getConnection()){
                PreparedStatement pstmt= conn.prepareStatement(query);
                pstmt.setInt(1,tasks.get(index).getindex());
                int rowsdeleted=pstmt.executeUpdate();
                if(rowsdeleted>0){
                    query="UPDATE tasks SET idx=idx-1 WHERE idx>?";
                    pstmt= conn.prepareStatement(query);
                    pstmt.setInt(1,index);
                    tasks.clear();
                    loadTasksFromDatabase();
                    }
                } 
            catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
    public int lastIndex(){
        return tasks.size()-1;
    }
    //reschedule overdue tasks to next day and set them to highest priority
        public void rescheduleTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        for (Task task : tasks) {
            if (task.getDeadline().isBefore(now) && (task.getStatus().equals("Pending")|| task.getStatus().equals("Overdue") )) {
                // Task is overdue
                System.out.println("Task " + task.getTitle() + " is overdue. Rescheduling...");
                LocalDateTime newDeadline = now.plusDays(1); // Reschedule to 1 day later (can be customized)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                newDeadline.format(formatter);

                try(Connection conn =databaseUtil.getConnection()){
                    String query="UPDATE tasks SET deadline=?,priority=?,status=? WHERE idx=?";
                    PreparedStatement pstmt= conn.prepareStatement(query);
                    pstmt.setTimestamp(1,Timestamp.valueOf(newDeadline));
                    pstmt.setInt(2,1);
                    pstmt.setString(3,"Overdue");
                    pstmt.setInt(4,task.getindex());
                    int rowsupdated=pstmt.executeUpdate();
                    if(rowsupdated>0){
                        task.setDeadline(newDeadline);
                        task.setPriority(1);
                        task.setStatus("Overdue");
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                System.out.println("\nNew deadline for " + task.getTitle() + ": " + newDeadline);
            }
        }
    }
    // Remove completed tasks
    public void removeCompletedTasks() {
        for(int i=0,j=0;i<tasks.size();i++){
            if(tasks.get(i).getStatus().equals("Completed")){
                deleteTask(i);
                i--;
            }
        }
    }
     // Method to increase priority based on deadline
     public void increasePriorityForUpcomingTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        for (Task task : tasks) {
            if (task.getStatus().equals("Pending")) {
                // Calculate time difference between now and the task's deadline
                long hoursUntilDeadline = Duration.between(now, task.getDeadline()).toHours();
                
                if (hoursUntilDeadline <= 48 && hoursUntilDeadline > 24) {
                    int newPriority = (task.getPriority()-3)<1?1:(task.getPriority()-3);  // increase priority by 2
                    try(Connection conn =databaseUtil.getConnection()){
                        String query="UPDATE tasks SET priority=? WHERE idx=?";
                        PreparedStatement pstmt= conn.prepareStatement(query);
                        pstmt.setInt(1,newPriority);
                        pstmt.setInt(2,task.getindex());
                        int rowsupdated=pstmt.executeUpdate();
                        if(rowsupdated>0){
                            task.setPriority(newPriority);
                        }
                    }
                    catch(SQLException e){
                        e.printStackTrace();
                    }
                } else if (hoursUntilDeadline <= 24) {
                    try(Connection conn =databaseUtil.getConnection()){
                        String query="UPDATE tasks SET priority=? WHERE idx=?";
                        PreparedStatement pstmt= conn.prepareStatement(query);
                        pstmt.setInt(1,1);
                        pstmt.setInt(2,task.getindex());
                        int rowsupdated=pstmt.executeUpdate();
                        if(rowsupdated>0){
                            task.setPriority(1);
                        }
                    }
                    catch(SQLException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    // Combine rescheduling, deletion, and priority adjustment to allow for background thread implementation
    public void checkAndReschedule() {
        rescheduleTasks();
        increasePriorityForUpcomingTasks();
        removeCompletedTasks();  // Remove tasks marked as "Completed"
    }
    public void stopScheduler() {
        running = false;
    }
    public void startScheduler() {
        Thread rescheduleThread = new Thread(() -> {
            while (running) {
                checkAndReschedule();
                try {
                    Thread.sleep(1000); // Wait for 1 sec before checking again
                } catch (InterruptedException e) {
                    System.out.println("Scheduler interrupted, exiting.");
                    break;
                }
            }
        });

        rescheduleThread.start();
    }
    // Get All Tasks sorted by deadline and priority
    public List<Task> getAllTasks() {
        List<Task> temp=new ArrayList<>(tasks);
        temp.sort(Comparator.comparing(Task::getDeadline)
                .thenComparing(Comparator.comparing(Task::getPriority)).thenComparing(Comparator.comparing(Task::getindex)));
        return temp;
    }
}
