import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class StudentDashboardFrame extends JFrame {

    public StudentDashboardFrame(studentfunctions student) {
        setTitle("Student Dashboard");
        setSize(920, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(new Color(243, 247, 255));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(34, 50, 84));

        JLabel subtitle = new JLabel("View your marks and course-wise results");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(95, 108, 136));

        JPanel titleWrap = new JPanel();
        titleWrap.setOpaque(false);
        titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
        titleWrap.add(title);
        titleWrap.add(Box.createRigidArea(new Dimension(0, 6)));
        titleWrap.add(subtitle);

        JLabel studentChip = new JLabel("Student: " + student.getStudentName());
        studentChip.setOpaque(true);
        studentChip.setBackground(new Color(228, 238, 255));
        studentChip.setForeground(new Color(34, 91, 177));
        studentChip.setBorder(new EmptyBorder(8, 12, 8, 12));
        studentChip.setFont(new Font("Segoe UI", Font.BOLD, 12));

        header.add(titleWrap, BorderLayout.WEST);
        header.add(studentChip, BorderLayout.EAST);

        JTable resultTable = new JTable();
        resultTable.setFillsViewportHeight(true);
        resultTable.setRowHeight(30);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        resultTable.getTableHeader().setBackground(new Color(230, 237, 252));
        resultTable.getTableHeader().setForeground(new Color(37, 63, 111));
        resultTable.setGridColor(new Color(223, 231, 247));
        resultTable.setSelectionBackground(new Color(211, 227, 255));
        resultTable.setSelectionForeground(new Color(34, 50, 84));
        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 251, 255));
                    c.setForeground(new Color(48, 63, 95));
                }
                return c;
            }
        });
        DefaultTableModel model = student.getResultModel();
        resultTable.setModel(model);
        applyColumnRenderers(resultTable);
        int[] counts = calculatePassFail(model);
        AtomicReference<TableRowSorter<DefaultTableModel>> sorterRef = new AtomicReference<>(new TableRowSorter<>(model));
        resultTable.setRowSorter(sorterRef.get());

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(214, 223, 242)));

        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(new Color(96, 111, 144));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(180, 32));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 211, 233)),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Pass", "Fail"});
        statusFilter.setPreferredSize(new Dimension(100, 32));
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel totalInfoLabel = new JLabel(model.getRowCount() == 0
                ? "No results available yet."
                : "Total subjects: " + model.getRowCount());
        totalInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalInfoLabel.setForeground(new Color(96, 111, 144));

        JLabel passInfoLabel = createSummaryChip("Passed: " + counts[0], new Color(230, 247, 238), new Color(29, 121, 88));
        JLabel failInfoLabel = createSummaryChip("Failed: " + counts[1], new Color(255, 236, 236), new Color(170, 60, 60));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(totalInfoLabel);
        statsPanel.add(passInfoLabel);
        statsPanel.add(failInfoLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(filterLabel);
        filterPanel.add(searchField);
        filterPanel.add(statusFilter);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        JButton refreshBtn = createButton("Refresh", new Color(37, 112, 205));
        JButton logout = createButton("Logout", new Color(186, 58, 44));

        JPanel bottomLeft = new JPanel();
        bottomLeft.setLayout(new BoxLayout(bottomLeft, BoxLayout.Y_AXIS));
        bottomLeft.setOpaque(false);
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomLeft.add(filterPanel);
        bottomLeft.add(Box.createRigidArea(new Dimension(0, 8)));
        bottomLeft.add(statsPanel);

        Runnable applyFilter = () -> applyStudentFilter(
                sorterRef.get(),
                searchField.getText(),
                (String) statusFilter.getSelectedItem()
        );

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter.run();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter.run();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter.run();
            }
        });
        statusFilter.addActionListener(e -> applyFilter.run());

        refreshBtn.addActionListener(e -> {
            DefaultTableModel refreshed = student.getResultModel();
            resultTable.setModel(refreshed);
            sorterRef.set(new TableRowSorter<>(refreshed));
            resultTable.setRowSorter(sorterRef.get());
            applyColumnRenderers(resultTable);
            applyFilter.run();
            int[] refreshedCounts = calculatePassFail(refreshed);
            totalInfoLabel.setText(refreshed.getRowCount() == 0
                    ? "No results available yet."
                    : "Total subjects: " + refreshed.getRowCount());
            passInfoLabel.setText("Passed: " + refreshedCounts[0]);
            failInfoLabel.setText("Failed: " + refreshedCounts[1]);
        });

        logout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        footer.add(refreshBtn);
        footer.add(logout);

        root.add(header, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(bottomLeft, BorderLayout.WEST);
        root.add(footer, BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);
        setVisible(true);
    }

    private int[] calculatePassFail(DefaultTableModel model) {
        int pass = 0;
        int fail = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object gradeObj = model.getValueAt(i, 2);
            Object marksObj = model.getValueAt(i, 1);

            if (gradeObj != null) {
                String grade = gradeObj.toString().trim().toUpperCase();
                if (!grade.isEmpty()) {
                    if ("F".equals(grade) || "FAIL".equals(grade)) {
                        fail++;
                    } else {
                        pass++;
                    }
                    continue;
                }
            }

            if (marksObj != null) {
                try {
                    int marks = Integer.parseInt(marksObj.toString());
                    if (marks >= 40) {
                        pass++;
                    } else {
                        fail++;
                    }
                } catch (NumberFormatException ignored) {
                    fail++;
                }
            }
        }
        return new int[]{pass, fail};
    }

    private void applyStudentFilter(TableRowSorter<DefaultTableModel> sorter, String keyword, String status) {
        if (sorter == null) {
            return;
        }

        List<RowFilter<DefaultTableModel, Integer>> filters = new ArrayList<>();
        String text = keyword == null ? "" : keyword.trim();
        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
        }

        if (status != null && !"All".equalsIgnoreCase(status)) {
            boolean requirePass = "Pass".equalsIgnoreCase(status);
            filters.add(new RowFilter<>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    Object gradeObj = entry.getValue(2);
                    Object marksObj = entry.getValue(1);
                    boolean isPass = false;

                    if (gradeObj != null && !gradeObj.toString().trim().isEmpty()) {
                        String grade = gradeObj.toString().trim().toUpperCase();
                        isPass = !("F".equals(grade) || "FAIL".equals(grade));
                    } else if (marksObj != null) {
                        try {
                            int marks = Integer.parseInt(marksObj.toString());
                            isPass = marks >= 40;
                        } catch (NumberFormatException ignored) {
                            isPass = false;
                        }
                    }
                    return requirePass == isPass;
                }
            });
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private JLabel createSummaryChip(String text, Color bg, Color fg) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setForeground(fg);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(new EmptyBorder(6, 10, 6, 10));
        return label;
    }

    private void applyColumnRenderers(JTable table) {
        if (table.getColumnCount() < 3) {
            return;
        }
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
    }

    private JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
