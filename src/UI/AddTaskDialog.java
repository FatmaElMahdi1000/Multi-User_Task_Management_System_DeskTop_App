package UI;

import Model.Task;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import service.TaskService;

public class AddTaskDialog extends JDialog {
    private final JTextField txtTitle;
    private final JTextField txtDescription;
    private final JComboBox<String> comboPriority;
    private final JComboBox<String> comboCategory;
    private final JComboBox<String> comboStatus;
    private final JTextField txtDeadlineDate;

    private final JComboBox<String> comboHour;
    private final JComboBox<String> comboMinute;
    private final JComboBox<String> comboAmPm;

    private boolean saved = false;
    private Task taskContext;
    private Map<Integer, String> categoryMap;

    public AddTaskDialog(Frame parent, Task existingTask) {
        super(parent, existingTask == null ? "Create New Task" : "Edit Task", true);
        this.taskContext = existingTask != null ? existingTask : new Task();

        setSize(500, 480);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 12);

        formPanel.add(new JLabel("Task Title:")).setFont(fontLabel);
        txtTitle = new JTextField(taskContext.getTitle());
        txtTitle.setFont(fontInput);
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Description:")).setFont(fontLabel);
        txtDescription = new JTextField(taskContext.getDescription());
        txtDescription.setFont(fontInput);
        formPanel.add(txtDescription);

        formPanel.add(new JLabel("Category:")).setFont(fontLabel);
        comboCategory = new JComboBox<>(); comboCategory.setFont(fontInput);
        comboCategory.setBackground(Color.WHITE);
        formPanel.add(comboCategory);

        formPanel.add(new JLabel("Priority Level:")).setFont(fontLabel);
        comboPriority = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        comboPriority.setFont(fontInput); comboPriority.setBackground(Color.WHITE);
        if(existingTask != null) comboPriority.setSelectedItem(taskContext.getPriority());
        formPanel.add(comboPriority);

        formPanel.add(new JLabel("Current Status:")).setFont(fontLabel);
        comboStatus = new JComboBox<>(new String[]{"TODO", "IN_PROGRESS", "PENDING", "DONE"});
        comboStatus.setFont(fontInput); comboStatus.setBackground(Color.WHITE);

        if(existingTask != null) comboStatus.setSelectedItem(taskContext.getStatus());
        formPanel.add(comboStatus);

        formPanel.add(new JLabel("Deadline Date (YYYY-MM-DD):")).setFont(fontLabel);

        String initialDate = LocalDate.now().toString();
        if (existingTask != null && taskContext.getDeadline() != null) {
            initialDate = taskContext.getDeadline().toLocalDate().toString();
        }
        txtDeadlineDate = new JTextField(initialDate); txtDeadlineDate.setFont(fontInput);
        formPanel.add(txtDeadlineDate);

        formPanel.add(new JLabel("Deadline Time (12hr Clock):")).setFont(fontLabel);
        JPanel timePickerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePickerPanel.setBackground(Color.WHITE);

        String[] hours = new String[12];
        for (int i = 0; i < 12; i++) hours[i] = String.format("%02d", i == 0 ? 12 : i);
        comboHour = new JComboBox<>(hours); comboHour.setBackground(Color.WHITE);

        String[] minutes = new String[60];
        for (int i = 0; i < 60; i++) minutes[i] = String.format("%02d", i);
        comboMinute = new JComboBox<>(minutes); comboMinute.setBackground(Color.WHITE);

        comboAmPm = new JComboBox<>(new String[]{"AM", "PM"}); comboAmPm.setBackground(Color.WHITE);

        timePickerPanel.add(comboHour);
        timePickerPanel.add(new JLabel(":"));
        timePickerPanel.add(comboMinute);
        timePickerPanel.add(Box.createHorizontalStrut(5));
        timePickerPanel.add(comboAmPm);
        formPanel.add(timePickerPanel);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(new Color(245, 247, 250));

        JButton btnSave = new JButton("Save Changes");
        btnSave.setFont(fontLabel); btnSave.setBackground(new Color(52, 152, 219)); btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false); btnSave.setBorderPainted(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(fontLabel); btnCancel.setBackground(new Color(189, 195, 199)); btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false); btnCancel.setBorderPainted(false);

        btnSave.addActionListener(e -> {
            if (txtTitle.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title field cannot be empty.", "Validation Alert", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                taskContext.setTitle(txtTitle.getText().trim());
                taskContext.setDescription(txtDescription.getText().trim());
                taskContext.setPriority((String) comboPriority.getSelectedItem());
                taskContext.setStatus((String) comboStatus.getSelectedItem());

                String dateString = txtDeadlineDate.getText().trim();
                String timeString = comboHour.getSelectedItem() + ":" + comboMinute.getSelectedItem() + " " + comboAmPm.getSelectedItem();
                String fullCombinedString = dateString + " " + timeString;

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
                LocalDateTime parsedDeadline = LocalDateTime.parse(fullCombinedString, formatter);
                taskContext.setDeadline(parsedDeadline);

                String selCat = (String) comboCategory.getSelectedItem();
                int catId = 1;
                for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
                    if (entry.getValue().equals(selCat)) { catId = entry.getKey(); break; }
                }
                taskContext.setCategoryId(catId);

                saved = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error parsing time values. Make sure the date format matches YYYY-MM-dd exactly.", "Time Parse Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        loadCategories();
    }

    private void loadCategories() {
        try {
            this.categoryMap = new TaskService().getCategoryOptions();
            for (String val : categoryMap.values()) comboCategory.addItem(val);
            if (taskContext.getCategoryId() > 0) {
                comboCategory.setSelectedItem(categoryMap.get(taskContext.getCategoryId()));
            }
        } catch (Exception ignored) {}
    }

    public boolean isSaved() { return saved; }
    public Task getTask() { return taskContext; }
}