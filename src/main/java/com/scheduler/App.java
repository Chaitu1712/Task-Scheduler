package com.scheduler;

import java.time.LocalDateTime;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner sc = new Scanner(System.in);
        // Sample loop for managing tasks in console
        while (true) {
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Update Task");
            System.out.println("4. Delete Task");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline
            int i=taskManager.lastIndex()==-1?0:taskManager.lastIndex();
            if (choice == 1) {
                // Add Task
                System.out.print("Enter title: ");
                String title = sc.nextLine();
                System.out.println("Enter description (type 'end' on a new line to finish): ");
                StringBuilder description = new StringBuilder();
                String line;
                while (!(line = sc.nextLine()).equalsIgnoreCase("end")) {
                    description.append(line).append("\n");
                }
                
                String descriptionstr = description.toString();
                System.out.print("Enter priority (1-10): ");
                int priority = sc.nextInt();
                sc.nextLine(); // Consume newline
                System.out.print("Enter deadline (yyyy-MM-ddTHH:mm): ");
                String deadlineStr = sc.nextLine();
                LocalDateTime deadline = LocalDateTime.parse(deadlineStr);

                Task task = new Task(i++,title, descriptionstr, priority, deadline);
                taskManager.addTask(task);
            } else if (choice == 2) {
                // View Tasks
                for (Task task : taskManager.getAllTasks()) {
                    System.out.println(task+"\n");
                }
            } else if (choice == 3) {
                // Update Task (assuming we update by index)
                System.out.print("Enter task index to update: ");
                int index = sc.nextInt();
                sc.nextLine(); // Consume newline
                System.out.print("Enter new title: ");
                String title = sc.nextLine();
                System.out.println("Enter description (type 'end' on a new line to finish): ");
                StringBuilder description = new StringBuilder();
                String line;
                while (!(line = sc.nextLine()).equalsIgnoreCase("end")) {
                    description.append(line).append("\n");
                
                }
                
                String descriptionstr = description.toString();
                System.out.print("Enter new priority (1-10): ");
                int priority = sc.nextInt();
                sc.nextLine(); // Consume newline
                System.out.print("Enter new deadline (yyyy-MM-ddTHH:mm): ");
                String deadlineStr = sc.nextLine();
                LocalDateTime deadline = LocalDateTime.parse(deadlineStr);

                Task updatedTask = new Task(i,title, descriptionstr, priority, deadline);
                taskManager.updateTask(index, updatedTask);
            } else if (choice == 4) {
                // Delete Task
                System.out.print("Enter task index to delete: ");
                int index = sc.nextInt();
                taskManager.deleteTask(index);
            } else if (choice == 5) {
                // Exit
                break;
            }
        }
        sc.close();
    }
}
