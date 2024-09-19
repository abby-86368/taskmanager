import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;


public class TaskManager{
    private static ArrayList <Task> tasks = new ArrayList<>();
    private static DefaultListModel<String> listModel = new DefaultListModel<>();

    private static JProgressBar progressBar = new JProgressBar(0, 100);

    public TaskManager() {
        tasks = new ArrayList<>();
        listModel = new DefaultListModel<>();
        loadFromFile();
        refresh();
    }

    public void addTask(Task task){
        tasks.add(task);
        listModel.addElement(task.toString());
        }

    public static void deleteTask(int i){
        tasks.remove(i);
        listModel.remove(i);
        saveToFile();
        }

    public static void editTask(int i, Task task){
        if (i >= 0 && i < tasks.size() && i < listModel.size()) {
            tasks.set(i, task);
            listModel.set(i, task.toString());
            saveToFile();

        } else {
            // Handle the case where i is not a valid index
            System.out.println("Cannot edit task: index " + i + " is out of bounds.");
        }
    }


    public static void refresh(){
        listModel.clear();
        for(Task task : tasks){
            listModel.addElement(task.toString());
        }
        //we need code to refresh the table
    }

    public static void loadFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("taskList.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String name = parts[0];
                Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(parts[1]);
                String priority = parts[2];
                String status = parts[3];
                Task task = new Task(name, dueDate, priority, status);
                tasks.add(task);
            }
            reader.close();
            System.out.println("Task list has been loaded from taskList.txt");
        } catch (IOException | java.text.ParseException e) {
            System.out.println("Error occurred while loading the task list: " + e.getMessage());
        }
    }

    public static void saveToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("taskList.txt"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Task task : tasks) {
                String dueDateFormatted = dateFormat.format(task.getDueDate());
                String taskString = task.getName() + " " +
                        dueDateFormatted + " " +
                        task.getPriority() + " " +
                        task.getStatus() + "\n";
                writer.write(taskString);
            }
            writer.close();
            System.out.println("Task list has been saved to taskList.txt");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error occurred while saving the task list.");
        }
    }


    public static void updateProgressBar() {
        int completed = 0;


        for (Task task : tasks) {
            switch (task.getStatus()) {
                case "Completed":
                    completed++;
                    break;
            }
        }

        int total = tasks.size();
        int completedPercentage = total > 0 ? (completed * 100) / total : 0;

        progressBar.setValue(completedPercentage);
        progressBar.setStringPainted(true);
    }

    // Method to select a dependency task
    public static Task selectDependencyTask() {
        String[] taskNames = new String[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            taskNames[i] = tasks.get(i).getName();
        }
        String selectedTaskName = (String) JOptionPane.showInputDialog(null, "Select a task:",
                "Task Selection", JOptionPane.QUESTION_MESSAGE, null, taskNames, taskNames[0]);
        for (Task task : tasks) {
            if (task.getName().equals(selectedTaskName)) {
                return task;
            }
        }
        return null;
    }


    public static void main(String[] args) {

            JFrame frame = new JFrame("Task Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            frame.setLayout(new BorderLayout());
            frame.getContentPane().setBackground(Color.PINK);
            frame.add(progressBar, BorderLayout.SOUTH);

            // the form for tasks
            JPanel form = new JPanel();
            form.setLayout(new GridLayout(10, 10));
            form.add(new JLabel("Name:")); //prompting the user for the name of the task
            JTextField nameField = new JTextField(); //creating a space for the user to enter the name
            form.add(nameField); //adding the name to the form

            form.add(new JLabel("Due Date (yyyy-MM-dd):")); //Prompting the user for the due date
            JTextField dueDateField = new JTextField(); //creating a text field for the due date
            form.add(dueDateField); //adding the due date to the form

            form.add(new JLabel("Priority:")); //Prompting the user for the priority
            String[] priorities = {"Choose", "High", "Medium", "Low"}; // Priority options form high(most important) to low(least important)
            JComboBox<String> priorityBox = new JComboBox<>(priorities);
            form.add(priorityBox);

            form.add(new JLabel("Status:"));
            String[] statuses = {"Choose", "Not Started", "In Progress", "Completed", "Paused"};
            JComboBox<String> statusBox = new JComboBox<>(statuses);
            form.add(statusBox);

            frame.add(form, BorderLayout.NORTH);

            JButton addButton = new JButton("Add Task");
            form.add(addButton);

            // Delete button
            JButton deleteButton = new JButton("Delete Task");
            form.add(deleteButton);



            JButton sortByPriorityButton = new JButton("Sort Tasks by Priority");
            form.add(sortByPriorityButton);

            // Sort by Due Date button
            JButton sortByDueDateButton = new JButton("Sort Tasks by Due Date");
            form.add(sortByDueDateButton);



            // Edit button
            JButton editButton = new JButton("Edit Task");
            form.add(editButton);

        // Dependencies button
        JButton addDependencyButton = new JButton("Add Task Dependency");
        form.add(addDependencyButton);

        JButton saveButton = new JButton("Save");
        form.add(saveButton); // Add the Save button




        // Add action listener for the Save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });

            // Task list
            JList<String> taskList = new JList<>(listModel);
            frame.add(new JScrollPane(taskList), BorderLayout.CENTER);



            // Add button action
            addButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    String name = nameField.getText();
                    Date dueDate = new Date();
                    try {
                        dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateField.getText());
                        // Check if the date entered is before today's date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date today = sdf.parse(sdf.format(new Date()));
                        if (dueDate.before(today)) {
                            JOptionPane.showMessageDialog(null, "Today's date is " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ". Please enter a valid date.");
                            return;
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Incorrect format for date. Try entering yyyy-MM-dd.");
                        return;
                    }
                    String priority = (String) priorityBox.getSelectedItem();
                    String status = (String) statusBox.getSelectedItem();
                    Task task = new Task(name, dueDate, priority, status);
                    listModel.addElement(task.toString());
                    tasks.add(task);
                    updateProgressBar();
                }

            });



        //sorting by priority

            //Action listener for sort by priority
        sortByPriorityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Low to High", "High to Low"};
                int n = JOptionPane.showOptionDialog(frame,
                        "How do you want to sort the tasks by priority?",
                        "Sort by Priority",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);

                if (n == 0) { // Low to High
                    tasks.sort((Task t1, Task t2) -> {
                        int p1 = t1.getPriority().equals("High") ? 3 : t1.getPriority().equals("Medium") ? 2 : 1;
                        int p2 = t2.getPriority().equals("High") ? 3 : t2.getPriority().equals("Medium") ? 2 : 1;
                        int myComparison = Integer.compare(p1, p2);
                        if (myComparison != 0) return myComparison;
                        return t1.getDueDate().compareTo(t2.getDueDate());
                    });
                } else if (n == 1) { // High to Low
                    tasks.sort((Task t1, Task t2) -> {
                        int p1 = t1.getPriority().equals("High") ? 3 : t1.getPriority().equals("Medium") ? 2 : 1;
                        int p2 = t2.getPriority().equals("High") ? 3 : t2.getPriority().equals("Medium") ? 2 : 1;
                        int myComparison = Integer.compare(p2, p1);
                        if (myComparison != 0) return myComparison;
                        return t1.getDueDate().compareTo(t2.getDueDate());
                    });
                }
                refresh();
            }
        });


// Action listener for sort by due date
        sortByDueDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"First to Last Task Due", "Last to First Task Due"};
                int n = JOptionPane.showOptionDialog(frame,
                        "How do you want to sort the tasks by due date?",
                        "Sort by Due Date",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);

                if (n == 0) { // First to Last Task Due
                    tasks.sort(Comparator.comparing(Task::getDueDate));
                } else if (n == 1) { // Last to First Task Due
                    tasks.sort(Comparator.comparing(Task::getDueDate).reversed());
                }
                refresh();
            }
        });




            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (listModel.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "You have no tasks to delete.");
                    } else {
                        int selectedIndex = taskList.getSelectedIndex();
                        if (selectedIndex != -1) {
                            int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to delete this task?","Warning",JOptionPane.YES_NO_OPTION);
                            if(dialogResult == JOptionPane.YES_OPTION){
                                listModel.remove(selectedIndex);
                                updateProgressBar();
                            }
                        }
                    }
                }
            });


        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();

                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a task to edit.");
                } else if (tasks.isEmpty() || listModel.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "There are no tasks to edit.");
                } else if (selectedIndex >= tasks.size() || selectedIndex >= listModel.getSize()) {
                    JOptionPane.showMessageDialog(null, "Selected task does not exist.");
                } else {
                    String name = nameField.getText();
                    Date dueDate = new Date();
                    try {
                        dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateField.getText());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Incorrect format for date. Try entering yyyy-MM-dd.");
                        return;
                    }
                    String priority = (String) priorityBox.getSelectedItem();
                    String status = (String) statusBox.getSelectedItem();
                    Task task = new Task(name, dueDate, priority, status);
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure this is the task you would like to edit with the information?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (status.equals("Completed") && !task.canComplete()) {
                            JOptionPane.showMessageDialog(null, "Please complete dependent tasks first.");
                        } else {
                            editTask(selectedIndex, task);
                            updateProgressBar();
                        }
                    }
                }
            }
        });



        // Add action listener for the addDependencyButton
        addDependencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a task to add a dependency.");
                } else if (tasks.isEmpty() || listModel.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "There are no tasks to add a dependency.");
                } else if (selectedIndex >= tasks.size() || selectedIndex >= listModel.getSize()) {
                    JOptionPane.showMessageDialog(null, "Selected task does not exist.");
                } else {
                    // Assuming you have a method to select a dependency task
                    Task dependencyTask = selectDependencyTask();
                    if (dependencyTask != null) {
                        tasks.get(selectedIndex).addDependency(dependencyTask);
                        listModel.set(selectedIndex, tasks.get(selectedIndex).toString());
                        JOptionPane.showMessageDialog(null, "Dependency added successfully.");
                    }
                }
            }
        });


        frame.setVisible(true);
        };
    }

