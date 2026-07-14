package UI;

import service.TaskService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ActivityLogWindow extends JDialog {
    private final JTable logTable;
    private final DefaultTableModel tableModel;
    private final JTextField txtStart;
    private final JTextField txtEnd;
    private final TaskService taskService;
    private final String activeUserId;

    private final Color COLOR_BG = new Color(245, 247, 250);
    private final Color COLOR_TEXT_MAIN = new Color(44, 62, 80);
    private final Color COLOR_ACCENT = new Color(52, 152, 219);

    public ActivityLogWindow(Frame parent, String userId) {
        super(parent, "Task Operational Audit Trail Logs", true);
        this.taskService = new TaskService();
        this.activeUserId = userId;

        setSize(800, 480);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(15, 15));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 224, 230)));

        Font boldFont = new Font("Segoe UI", Font.BOLD, 12);
        Font plainFont = new Font("Segoe UI", Font.PLAIN, 12);

        JLabel lblFrom = new JLabel("From Date:"); lblFrom.setFont(boldFont);
        lblFrom.setForeground(COLOR_TEXT_MAIN);
        txtStart = new JTextField(LocalDate.now().minusDays(7).toString(), 10); txtStart.setFont(plainFont);

        JLabel lblTo = new JLabel("To Date:"); lblTo.setFont(boldFont);
        lblTo.setForeground(COLOR_TEXT_MAIN);
        txtEnd = new JTextField(LocalDate.now().toString(), 10); txtEnd.setFont(plainFont);

        JButton btnFilter = new JButton("Run Analytics Search");
        btnFilter.setFont(boldFont);
        btnFilter.setBackground(COLOR_ACCENT);
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFocusPainted(false);
        btnFilter.setBorderPainted(false);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.setPreferredSize(new Dimension(160, 30));

        filterPanel.add(lblFrom); filterPanel.add(txtStart);
        filterPanel.add(lblTo); filterPanel.add(txtEnd);
        filterPanel.add(btnFilter);
        add(filterPanel, BorderLayout.NORTH);


        String[] columns = {"Log Record ID", "Task Title", "Action Description", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        logTable = new JTable(tableModel);
        logTable.setRowHeight(32);
        logTable.setFont(plainFont);
        logTable.setGridColor(new Color(230, 233, 237));
        logTable.setSelectionBackground(new Color(235, 243, 250));
        logTable.setSelectionForeground(COLOR_TEXT_MAIN);

        JTableHeader header = logTable.getTableHeader();
        header.setFont(boldFont);
        header.setBackground(Color.WHITE);
        header.setForeground(COLOR_TEXT_MAIN);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 223, 230), 1));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        //  EVENT ACTIONS
        btnFilter.addActionListener(e -> loadLogDataset());

        loadLogDataset();
    }

    private void loadLogDataset() {
        try {
            tableModel.setRowCount(0);
            LocalDate start = LocalDate.parse(txtStart.getText().trim());
            LocalDate end = LocalDate.parse(txtEnd.getText().trim());


            List<String[]> rows = taskService.getActivityLogs(start, end, this.activeUserId);
            for (String[] row : rows) {
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Please check date inputs AND follow YYYY-MM-DD formatting exactly.",
                    "Time Format Error ❌",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}