package com.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;

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
    // Get All Tasks sorted by deadline and priority
    public List<Task> getAllTasks() {
        List<Task> temp=new ArrayList<>(tasks);
        temp.sort(Comparator.comparing(Task::getDeadline)
                .thenComparing(Comparator.comparing(Task::getPriority)).thenComparing(Comparator.comparing(Task::getindex)));
        return temp;
    }
}
