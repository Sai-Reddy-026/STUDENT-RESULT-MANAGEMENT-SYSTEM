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
        String studentName = safeValue(student.getStudentName());
        setTitle("Welcome " + studentName);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(new Color(239, 245, 255));
        root.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(new Color(34, 69, 124));
        header.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("Welcome " + studentName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("View marks, grades, and subject-wise performance");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(219, 229, 246));

        JPanel titleWrap = new JPanel();
        titleWrap.setOpaque(true);
        titleWrap.setBackground(new Color(34, 69, 124));
        titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
        titleWrap.add(title);
        titleWrap.add(Box.createRigidArea(new Dimension(0, 6)));
        titleWrap.add(subtitle);

        String studentId = safeValue(student.getId());
        String studentDepartment = safeValue(student.getStudentDepartment());
        JLabel studentChip = new JLabel("<html><b>Name:</b> " + studentName
                + "<br/><b>ID:</b> " + studentId
                + "<br/><b>Department:</b> " + studentDepartment + "</html>");
        studentChip.setOpaque(true);
        studentChip.setBackground(new Color(230, 240, 255));
        studentChip.setForeground(new Color(33, 73, 138));
        studentChip.setBorder(new EmptyBorder(10, 14, 10, 14));
        studentChip.setFont(new Font("Segoe UI", Font.PLAIN, 12));

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
        List<Integer> semesterOptions = student.getAvailableSemesters();
        Integer latestSemester = student.getLatestSemester();
        AtomicReference<Integer> selectedSemesterRef = new AtomicReference<>(latestSemester);
        DefaultTableModel model = student.getResultModel(latestSemester);
        resultTable.setModel(model);
        applyColumnRenderers(resultTable);
        int[] counts = calculatePassFail(model);
        AtomicReference<TableRowSorter<DefaultTableModel>> sorterRef = new AtomicReference<>(new TableRowSorter<>(model));
        resultTable.setRowSorter(sorterRef.get());

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(214, 223, 242), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Search Subject:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(new Color(96, 111, 144));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(230, 34));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 211, 233)),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        semesterLabel.setForeground(new Color(96, 111, 144));

        JComboBox<String> semesterFilter = new JComboBox<>();
        semesterFilter.setPreferredSize(new Dimension(130, 32));
        semesterFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (semesterOptions.isEmpty()) {
            semesterFilter.addItem("No semester");
            semesterFilter.setEnabled(false);
        } else {
            for (Integer semester : semesterOptions) {
                semesterFilter.addItem("Semester " + semester);
            }
            if (latestSemester != null) {
                semesterFilter.setSelectedItem("Semester " + latestSemester);
            }
        }

        JComboBox<String> resultFilter = new JComboBox<>(new String[]{"All Results", "Pass", "Fail", "A+", "A", "B", "C", "D", "E", "F"});
        resultFilter.setPreferredSize(new Dimension(125, 32));
        resultFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel totalInfoLabel = new JLabel(buildTotalInfoText(model, selectedSemesterRef.get()));
        totalInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalInfoLabel.setForeground(new Color(96, 111, 144));

        JLabel passInfoLabel = createSummaryChip("Passed: " + counts[0], new Color(230, 247, 238), new Color(29, 121, 88));
        JLabel failInfoLabel = createSummaryChip("Failed: " + counts[1], new Color(255, 236, 236), new Color(170, 60, 60));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statsPanel.setOpaque(true);
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(new EmptyBorder(6, 10, 6, 10));
        statsPanel.add(totalInfoLabel);
        statsPanel.add(passInfoLabel);
        statsPanel.add(failInfoLabel);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(true);
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        filterPanel.add(filterLabel);
        filterPanel.add(searchField);
        filterPanel.add(semesterLabel);
        filterPanel.add(semesterFilter);
        filterPanel.add(resultFilter);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setOpaque(true);
        controls.setBackground(Color.WHITE);
        controls.add(filterPanel);
        controls.add(statsPanel);

        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 218, 241)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        tableCard.add(controls, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(true);
        footer.setBackground(new Color(239, 245, 255));

        JButton refreshBtn = createButton("Refresh", new Color(37, 112, 205));
        JButton logout = createButton("Logout", new Color(186, 58, 44));

        Runnable applyFilter = () -> applyStudentFilter(
                sorterRef.get(),
                searchField.getText(),
                (String) resultFilter.getSelectedItem()
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
        resultFilter.addActionListener(e -> applyFilter.run());
        semesterFilter.addActionListener(e -> {
            Integer selectedSemester = parseSemesterSelection((String) semesterFilter.getSelectedItem());
            selectedSemesterRef.set(selectedSemester);
            DefaultTableModel refreshed = student.getResultModel(selectedSemester);
            resultTable.setModel(refreshed);
            sorterRef.set(new TableRowSorter<>(refreshed));
            resultTable.setRowSorter(sorterRef.get());
            applyColumnRenderers(resultTable);
            applyFilter.run();
            int[] refreshedCounts = calculatePassFail(refreshed);
            totalInfoLabel.setText(buildTotalInfoText(refreshed, selectedSemester));
            passInfoLabel.setText("Passed: " + refreshedCounts[0]);
            failInfoLabel.setText("Failed: " + refreshedCounts[1]);
        });

        refreshBtn.addActionListener(e -> {
            Integer selectedSemester = selectedSemesterRef.get();
            DefaultTableModel refreshed = student.getResultModel(selectedSemester);
            resultTable.setModel(refreshed);
            sorterRef.set(new TableRowSorter<>(refreshed));
            resultTable.setRowSorter(sorterRef.get());
            applyColumnRenderers(resultTable);
            applyFilter.run();
            int[] refreshedCounts = calculatePassFail(refreshed);
            totalInfoLabel.setText(buildTotalInfoText(refreshed, selectedSemester));
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
        root.add(tableCard, BorderLayout.CENTER);
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

    private void applyStudentFilter(TableRowSorter<DefaultTableModel> sorter, String keyword, String resultFilter) {
        if (sorter == null) {
            return;
        }

        List<RowFilter<DefaultTableModel, Integer>> filters = new ArrayList<>();
        String text = keyword == null ? "" : keyword.trim();
        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0));
        }

        if (resultFilter != null && ("Pass".equalsIgnoreCase(resultFilter) || "Fail".equalsIgnoreCase(resultFilter))) {
            boolean requirePass = "Pass".equalsIgnoreCase(resultFilter);
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

        if (resultFilter != null) {
            String normalized = resultFilter.trim().toUpperCase();
            boolean gradeSelected = "A+".equals(normalized) || "A".equals(normalized) || "B".equals(normalized)
                    || "C".equals(normalized) || "D".equals(normalized) || "E".equals(normalized) || "F".equals(normalized);
            if (gradeSelected) {
                String expectedGrade = normalized;
                filters.add(new RowFilter<>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        Object gradeObj = entry.getValue(2);
                        if (gradeObj == null) {
                            return false;
                        }
                        return expectedGrade.equals(gradeObj.toString().trim().toUpperCase());
                    }
                });
            }
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private Integer parseSemesterSelection(String semesterText) {
        if (semesterText == null) {
            return null;
        }
        String value = semesterText.trim();
        if (!value.startsWith("Semester ")) {
            return null;
        }
        try {
            return Integer.parseInt(value.substring("Semester ".length()).trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String buildTotalInfoText(DefaultTableModel model, Integer semester) {
        if (model.getRowCount() == 0) {
            if (semester == null) {
                return "No results available yet.";
            }
            return "No results for Semester " + semester + ".";
        }
        if (semester == null) {
            return "Total subjects: " + model.getRowCount();
        }
        return "Semester " + semester + " subjects: " + model.getRowCount();
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

    private String safeValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "N/A";
        }
        return value.trim();
    }
}
