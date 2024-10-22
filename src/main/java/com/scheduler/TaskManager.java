package com.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private volatile boolean running = true;

    public TaskManager() {
        this.tasks = new ArrayList<>();
    }

    // Add Task
    public void addTask(Task task) {
        tasks.add(task);
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
            tasks.set(index,temp);
        }
    }

    // Delete Task
    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            for(int i=index;i<tasks.size();i++)
            {
                tasks.get(i).setindex(tasks.get(i).getindex()-1);
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm");
                newDeadline.format(formatter);
                task.setDeadline(newDeadline);
                task.setPriority(1);
                task.setStatus("Overdue");
                System.out.println("New deadline for " + task.getTitle() + ": " + newDeadline);
            }
        }
    }
    // Remove completed tasks
    public void removeCompletedTasks() {
        Iterator<Task> iterator = tasks.iterator();
        
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getStatus().equals("Completed")) {
                System.out.println("Removing completed task: " + task.getTitle());
                iterator.remove(); // Remove the completed task
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
                    task.setPriority((task.getPriority()-3)<1?1:(task.getPriority()-3));  // increase priority by 3
                } else if (hoursUntilDeadline <= 24) {
                    task.setPriority(1);  // Highest priority
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
                    Thread.sleep(10000); // Wait for 10 secs before checking again
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
