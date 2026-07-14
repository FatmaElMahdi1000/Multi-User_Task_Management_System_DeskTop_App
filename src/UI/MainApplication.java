package UI;

import Model.*;
import service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainApplication extends JFrame {

    //  SERVICES

    private final TaskService taskService;
    private final UserProfile currentUser;

    //this is to be used in the main app: Storing categories in a map
    //we've categories as Foreign keys in the TASKS table:
    //I need to program to store: FK(Key) : Value (Equivalent category)
    private Map<Integer, String> categoryCache;


    //  UI COMPONENTS
    //Table - Model - Sorter
    private final JTable activeTable, completedTable, teamInsightTable;

    private final DefaultTableModel activeModel, completedModel, teamInsightModel;
    private TableRowSorter<DefaultTableModel> activeSorter, completedSorter, teamInsightSorter;

    // Filter Controls

    private JTextField txtFilterId = new JTextField(10);
    private JTextField txtFilterName = new JTextField(15);
       //--Drop down lists
    private JComboBox<String> comboFilterStatus = new JComboBox<>(new String[]{"ALL", "TODO", "IN_PROGRESS", "PENDING", "OVERDUE"});
    private JComboBox<String> filterCategory = new JComboBox<>(new String[]{"ALL", "Personal", "Feature Development", "Bug Fix & Hotfix", "Code Review & QA", "DevOps & Deployment", "Technical Debt & Refactoring", "Client Follow up", "Sending Email"});
    private JComboBox  FilterPriority = new JComboBox<>(new String[]{"ALL", "HIGH", "MEDIUM", "LOW"});

    // Dashboard Card Metric Counters
    private final JLabel lblTotalVal, lblPendingVal, lblCompletedTodayVal, lblCompletedAllVal;

    //  COLOR -- DESIGN

    private final Color COLOR_BG = new Color(248, 249, 250);
    private final Color COLOR_CARD_BG = Color.WHITE;
    private final Color COLOR_TEXT_MAIN = new Color(33, 37, 41);
    private final Color COLOR_TEXT_MUTED = new Color(108, 117, 125);
    private final Color COLOR_BORDER = new Color(222, 226, 230);

    private final Color ACCENT_BLUE = new Color(42, 102, 245);
    private final Color ACCENT_ORANGE = new Color(229, 142, 38);
    private final Color ACCENT_GREEN = new Color(78, 168, 114);
    private final Color ACCENT_DARK = new Color(52, 58, 64);


    // MAIN CONSTRUCTOR

    public MainApplication(TaskService sharedService, UserProfile currentUser) {
        this.taskService = sharedService;
        this.currentUser = currentUser;

        // Load initialization dictionary map data safely
        try {
            this.categoryCache = this.taskService.getCategoryOptions();
        } catch (Exception e) {
            this.categoryCache = new java.util.HashMap<>();
        }

        //  Window  Setup
        setTitle("Personal Task Management Dashboard");
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(0, 0));


        //  LAYOUT: Header Panel (Welcome text --- Sign Out)
        // ------------------------------------------
        JPanel topContainerPanel = new JPanel(new BorderLayout(0, 0));
        topContainerPanel.setBackground(COLOR_BG);

        JPanel headerUtilityPanel = new JPanel(new BorderLayout());
        headerUtilityPanel.setBackground(COLOR_BG);
        headerUtilityPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 0, 25));

        JLabel lblWelcome = new JLabel("Welcome, " + this.currentUser.getUserName() + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setForeground(COLOR_TEXT_MAIN);
        headerUtilityPanel.add(lblWelcome, BorderLayout.WEST);


        //Signing out Button
        JButton btnLogout = new JButton("Sign Out");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(217, 83, 79));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(100, 32));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerUtilityPanel.add(btnLogout, BorderLayout.EAST);
        topContainerPanel.add(headerUtilityPanel, BorderLayout.NORTH);



        //  LAYOUT: Metric Cards Blocks
        // ------------------------------------------
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(COLOR_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        lblTotalVal = new JLabel("0", SwingConstants.LEFT);
        lblPendingVal = new JLabel("0", SwingConstants.LEFT);
        lblCompletedTodayVal = new JLabel("0", SwingConstants.LEFT);
        lblCompletedAllVal = new JLabel("0", SwingConstants.LEFT);

        statsPanel.add(createMetricCard("TOTAL TASKS", lblTotalVal, ACCENT_BLUE));
        statsPanel.add(createMetricCard("UNFINISHED ITEMS", lblPendingVal, ACCENT_ORANGE));
        statsPanel.add(createMetricCard("COMPLETED TODAY", lblCompletedTodayVal, ACCENT_GREEN));
        statsPanel.add(createMetricCard("COMPLETED ALL TIME", lblCompletedAllVal, ACCENT_DARK));
        topContainerPanel.add(statsPanel, BorderLayout.CENTER);


        // BOTTOM LAYOUT PANEL:  Filters
        // ------------------------------------------
        JPanel filterGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        filterGridPanel.setBackground(Color.WHITE);
        filterGridPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, COLOR_BORDER));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 13);

        JLabel lblId = new JLabel("Filter ID:");
        lblId.setFont(labelFont);
        lblId.setForeground(COLOR_TEXT_MUTED);
        txtFilterId.setFont(inputFont);
        txtFilterId.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDER), BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JLabel lblName = new JLabel("Filter Name:");
        lblName.setFont(labelFont);
        lblName.setForeground(COLOR_TEXT_MUTED);
        txtFilterName.setFont(inputFont);
        txtFilterName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDER), BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(labelFont);
        lblStatus.setForeground(COLOR_TEXT_MUTED);
        comboFilterStatus.setFont(inputFont);
        comboFilterStatus.setBackground(Color.WHITE);

        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setFont(labelFont);
        lblCategory.setForeground(COLOR_TEXT_MUTED);
        filterCategory.setFont(inputFont);
        filterCategory.setBackground(Color.WHITE);

        filterGridPanel.add(lblId);
        filterGridPanel.add(txtFilterId);
        filterGridPanel.add(Box.createHorizontalStrut(10));
        filterGridPanel.add(lblName);
        filterGridPanel.add(txtFilterName);
        filterGridPanel.add(Box.createHorizontalStrut(10));
        filterGridPanel.add(lblStatus);
        filterGridPanel.add(comboFilterStatus);
        filterGridPanel.add(lblCategory);
        filterGridPanel.add(filterCategory);

        //Priority


        filterCategory.addActionListener(e -> refreshTaskData());
        topContainerPanel.add(filterGridPanel, BorderLayout.SOUTH);
        add(topContainerPanel, BorderLayout.NORTH);


        // CORE WORKSPACE DISPLAY CENTER REGION:
        // ------------------------------------------
        String[] taskColumns = {"Task ID", "Title", "Category", "Description", "Priority", "Status", "Date Created", "Deadline" };

        activeModel = new DefaultTableModel(taskColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        activeTable = new JTable(activeModel);
        configureTableStyling(activeTable);

        completedModel = new DefaultTableModel(taskColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        completedTable = new JTable(completedModel);
        configureTableStyling(completedTable);

        String[] insightColumns = {"Rank", "User Name", "Completed Today", "Total Completed"};
        teamInsightModel = new DefaultTableModel(insightColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        teamInsightTable = new JTable(teamInsightModel);
        configureTableStyling(teamInsightTable);

        // Scroll panes and JTabbedPane Assembly Configuration
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane activeScroll = new JScrollPane(activeTable);
        activeScroll.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane completedScroll = new JScrollPane(completedTable);
        completedScroll.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane insightScroll = new JScrollPane(teamInsightTable);
        insightScroll.setBorder(BorderFactory.createEmptyBorder());

        tabbedPane.addTab("Active Tasks", activeScroll);
        tabbedPane.addTab("Completed Tasks", completedScroll);
        tabbedPane.addTab("Team Insights", insightScroll);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(COLOR_BG);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        wrapperPanel.add(tabbedPane, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);


        //   Action Buttons
        // ------------------------------------------
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        actionPanel.setBackground(COLOR_BG);

        JButton btnAdd = createModernButton("Add New Task", ACCENT_BLUE);
        JButton btnEdit = createModernButton("Edit Selected", ACCENT_DARK);
        JButton btnDone = createModernButton("Mark Done", ACCENT_GREEN);
        JButton btnDelete = createModernButton("Delete Permanently", new Color(217, 83, 79));
        JButton btnShowLogs = createModernButton("View Audit Logs", new Color(111, 78, 168));

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDone);
        actionPanel.add(btnDelete);
        actionPanel.add(btnShowLogs);
        add(actionPanel, BorderLayout.SOUTH);



        //  Action LISTENERS
        // ------------------------------------------
        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)
            { executeAdvancedFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)
            { executeAdvancedFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e)
            { executeAdvancedFilters(); }
        };
        txtFilterId.getDocument().addDocumentListener(dl);
        txtFilterName.getDocument().addDocumentListener(dl);
        comboFilterStatus.addActionListener(e -> executeAdvancedFilters());

        btnAdd.addActionListener(e -> {
            AddTaskDialog dialog = new AddTaskDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                try {
                    taskService.createNewTask(dialog.getTask(), this.currentUser.getUserID().toString());
                    refreshTaskData();
                } catch (Exception ex) { error(ex.getMessage()); }
            }
        });

        btnEdit.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            if (selectedTab == 2) {
                JOptionPane.showMessageDialog(this, "Editing is not supported on Team Insights records.");
                return;
            }
            JTable currentTable = (selectedTab == 0) ? activeTable : completedTable;
            DefaultTableModel currentModel = (DefaultTableModel) currentTable.getModel();
            int row = currentTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a task row layout item first."); return; }
            int mIdx = currentTable.convertRowIndexToModel(row);

            Task selected = new Task();
            selected.setTaskId((String) currentModel.getValueAt(mIdx, 0));
            selected.setTitle((String) currentModel.getValueAt(mIdx, 1));
            selected.setDescription((String) currentModel.getValueAt(mIdx, 3));
            selected.setPriority((String) currentModel.getValueAt(mIdx, 4));
            selected.setStatus((String) currentModel.getValueAt(mIdx, 5));

            String deadlineStr = (String) currentModel.getValueAt(mIdx, 7);
            try {
                if (deadlineStr != null && deadlineStr.length() >= 10) {
                    selected.setDeadline(LocalDate.parse(deadlineStr.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay());
                } else {
                    selected.setDeadline(LocalDate.now().atStartOfDay());
                }
            } catch (Exception ex) {
                selected.setDeadline(LocalDate.now().atStartOfDay());
            }

            AddTaskDialog dialog = new AddTaskDialog(this, selected);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                try {
                    taskService.updateExistingTask(dialog.getTask(), this.currentUser.getUserID().toString());
                    refreshTaskData();
                } catch (Exception ex) { error(ex.getMessage()); }
            }
        });

        btnDone.addActionListener(e -> {
            int row = activeTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select an active task row to complete."); return; }
            int mIdx = activeTable.convertRowIndexToModel(row);
            try {
                taskService.completeTask((String) activeModel.getValueAt(mIdx, 0));
                refreshTaskData();
            } catch (Exception ex) { error(ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) return;
            JTable currentTable = (tabbedPane.getSelectedIndex() == 0) ? activeTable : completedTable;
            DefaultTableModel currentModel = (DefaultTableModel) currentTable.getModel();
            int row = currentTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row target item to remove."); return; }
            int mIdx = currentTable.convertRowIndexToModel(row);
            String id = (String) currentModel.getValueAt(mIdx, 0);
            String title = (String) currentModel.getValueAt(mIdx, 1);

            int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    taskService.removeTaskPermanently(id, title, this.currentUser.getUserID().toString());
                    refreshTaskData();
                } catch (Exception ex) { error(ex.getMessage()); }
            }
        });

        btnShowLogs.addActionListener(e -> new ActivityLogWindow(this, this.currentUser.getUserID().toString()).setVisible(true));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Log out of your session workspace?", "Confirm Sign Out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        AuthenticationService authService = new AuthenticationService();
                        LoginFrame loginPortal = new LoginFrame(authService);
                        loginPortal.setOnLoginSuccessCallback((UserProfile user) -> {
                            loginPortal.dispose();
                            new MainApplication(taskService, user).setVisible(true);
                        });
                        loginPortal.setVisible(true);
                    } catch (Exception ex) { error(ex.getMessage()); }
                });
            }
        });


        refreshTaskData();
    }





    // LAYOut / VISUAL METHODS
    // ==========================================
    private void configureTableStyling(JTable table) {
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(241, 243, 245));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(233, 236, 239));
        table.setSelectionForeground(COLOR_TEXT_MAIN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(Color.WHITE);
        header.setForeground(COLOR_TEXT_MUTED);
        header.setPreferredSize(new Dimension(header.getWidth(), 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER));
    }

    //METRIC CARDS
    private JPanel createMetricCard(String title, JLabel val, Color leftBorderColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, leftBorderColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                )
        ));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(COLOR_TEXT_MUTED);
        val.setFont(new Font("Segoe UI", Font.BOLD, 26));
        val.setForeground(COLOR_TEXT_MAIN);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private JButton createModernButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(170, 40));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }


    //   FILTER MODELING
    // ==========================================
    private void executeAdvancedFilters() {
        applyFilterModel(activeSorter, txtFilterId.getText().trim(), txtFilterName.getText().trim(), (String) comboFilterStatus.getSelectedItem(), (String) filterCategory.getSelectedItem(), (String) FilterPriority.getSelectedItem());
        applyFilterModel(completedSorter, txtFilterId.getText().trim(), txtFilterName.getText().trim(), "ALL", (String) filterCategory.getSelectedItem(), (String) FilterPriority.getSelectedItem());
    }

    private void applyFilterModel(TableRowSorter<DefaultTableModel> sorter, String id, String name, String status, String category, String priority) {
        if (sorter == null) return;
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!id.isEmpty()) filters.add(RowFilter.regexFilter("(?i)" + id, 0));
        if (!name.isEmpty()) filters.add(RowFilter.regexFilter("(?i)" + name, 1));
        if (status != null && !status.equals("ALL")) filters.add(RowFilter.regexFilter("^" + status + "$", 5));
        if (category != null && !category.equals("ALL")) filters.add(RowFilter.regexFilter("^" + category + "$", 2));
                                                                                                                                     //Later must add action Listener of the new Filter (ex:priority) before refresh data  (e->advancedFilter)


        sorter.setRowFilter(RowFilter.andFilter(filters));
    }


    //  DATA REFRESH

    private void loadTeamInsights() {
        try {

            teamInsightModel.setRowCount(0);

            List<TeamInsight> todayRanking = taskService.getTodayCompletedTask();
            List<TeamInsight> allTimeRanking = taskService.getAllTimeCompletedTask();

            int rank = 1;

            // Loop through ALL-TIME ranking so the rank is based on total completed tasks
            for (TeamInsight allTime : allTimeRanking) {

                int todayCount = 0;

                // Find today's count for the same user
                for (TeamInsight today : todayRanking) {

                    if (allTime.getUserName().equals(today.getUserName())) {
                        todayCount = today.getCompletedToday();
                        break;
                    }
                }

                teamInsightModel.addRow(new Object[]{
                        rank++,
                        allTime.getUserName(),
                        todayCount,
                        allTime.getCompletedALLTime()
                });
            }

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }




    //To update lots of things: Metric cards , adding new columns(rowData)
    public void refreshTaskData() {
        try {
            //  Clearing old row metrics view models records data
            activeModel.setRowCount(0);
            completedModel.setRowCount(0);
            teamInsightModel.setRowCount(0);

            // Fetching  data collections array maps references from business logic
            List<Task> list = taskService.getActiveTasksByUserId(this.currentUser.getUserID().toString());

            int totalTasks = list.size();
            int pendingCount = 0;
            int completedToday = 0;
            int completedAllTime = 0;

            String todayStr = LocalDate.now().toString();

            //  Loop construct mapping models records components items straight into data rows
            for (Task t : list) {
                String cat = categoryCache.getOrDefault(t.getCategoryId(), "General Work");
                Object[] rowData = {t.getTaskId(), t.getTitle(), cat, t.getDescription(), t.getPriority(), t.getStatus(), t.getFormattedCreatedAt(), t.getFormattedDeadline()};

                if ("DONE".equals(t.getStatus())) {
                    completedAllTime++;
                    if (t.getFormattedDeadline() != null && t.getFormattedDeadline().startsWith(todayStr)) {
                        completedToday++;
                    }
                    completedModel.addRow(rowData);
                } else {
                    pendingCount++;
                    activeModel.addRow(rowData);
                }
            }

            // Update   counter scorecard labels numbers views values structures
            lblTotalVal.setText(String.valueOf(totalTasks));
            lblPendingVal.setText(String.valueOf(pendingCount));
            lblCompletedTodayVal.setText(String.valueOf(completedToday));
            lblCompletedAllVal.setText(String.valueOf(completedAllTime));


            //   Pull cross-referenced team metrics records counts logs
            loadTeamInsights();

            //   Sync data   sorters filters straight back to active layout tables components structures
            activeSorter = new TableRowSorter<>(activeModel);
            activeTable.setRowSorter(activeSorter);

            completedSorter = new TableRowSorter<>(completedModel);
            completedTable.setRowSorter(completedSorter);

            teamInsightSorter = new TableRowSorter<>(teamInsightModel);
            teamInsightTable.setRowSorter(teamInsightSorter);

            executeAdvancedFilters();
        } catch (Exception e) { error(e.getMessage()); }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, "System Tracking Alert: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}