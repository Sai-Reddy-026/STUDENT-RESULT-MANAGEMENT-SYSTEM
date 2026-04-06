import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import javax.swing.RowFilter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class AdminFrame  {
    private static final String HOME_CARD = "home";
    private static final String TEACHER_CARD = "teacher";
    private static final String STUDENT_CARD = "student";
    private static final String SUBJECT_CARD = "subject";
    private static final String RESULTS_CARD = "results";

    JFrame frame = new JFrame();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final JTable resultsTable = new JTable();
    private final JTable teacherTable = new JTable();
    private final JTable studentTable = new JTable();
    private final JTable subjectTable = new JTable();
    private JTabbedPane teacherTabPane;
    private JTabbedPane studentTabPane;
    private JTabbedPane subjectTabPane;
    private final JTextField resultsSearchField = new JTextField(18);
    private final JComboBox<String> resultsFilterColumn = new JComboBox<>(new String[]{
            "All Columns", "Student", "Course", "Marks", "Awarded By", "Awarded On"
    });
    private TableRowSorter<DefaultTableModel> resultsSorter;
    private final JTextField teacherSearchField = new JTextField(18);
    private final JComboBox<String> teacherFilterColumn = new JComboBox<>(new String[]{
            "All Columns", "ID", "Name", "Email", "Department"
    });
    private TableRowSorter<DefaultTableModel> teacherSorter;
    private final JTextField studentSearchField = new JTextField(18);
    private final JComboBox<String> studentFilterColumn = new JComboBox<>(new String[]{
            "All Columns", "ID", "Name", "Email", "Department"
    });
    private TableRowSorter<DefaultTableModel> studentSorter;
    private final JTextField subjectSearchField = new JTextField(18);
    private final JComboBox<String> subjectFilterColumn = new JComboBox<>(new String[]{
            "All Columns", "ID", "Subject Name", "Semester", "Teacher"
    });
    private TableRowSorter<DefaultTableModel> subjectSorter;
    private final String adminDisplayName;
    private final String adminId;
    private final String adminDepartment;

    public AdminFrame() {
        this("Administrator", "N/A", "N/A");
    }

    public AdminFrame(String adminDisplayName) {
        this(adminDisplayName, "N/A", "N/A");
    }

    public AdminFrame(Adminfunction admin) {
        this(
                admin == null ? "Administrator" : admin.getDisplayName(),
                admin == null ? "N/A" : admin.getId(),
                admin == null ? "N/A" : admin.getDepartment()
        );
    }

    private AdminFrame(String adminDisplayName, String adminId, String adminDepartment) {
        this.adminDisplayName = safeValue(adminDisplayName, "Administrator");
        this.adminId = safeValue(adminId, "N/A");
        this.adminDepartment = safeValue(adminDepartment, "N/A");
        frame.setTitle("Welcome " + this.adminDisplayName);
        frame.setSize(1120, 720);
        frame.setMinimumSize(new Dimension(980, 640));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.setBackground(new Color(243, 247, 255));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 223, 242)),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("Welcome " + this.adminDisplayName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(36, 50, 84));

        JLabel subtitle = new JLabel("Manage teachers, students, and records");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(96, 111, 144));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(true);
        titleBlock.setBackground(Color.WHITE);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(title);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 6)));
        titleBlock.add(subtitle);

        header.add(titleBlock, BorderLayout.WEST);

        JLabel adminChip = new JLabel("<html><b>Name:</b> " + this.adminDisplayName
                + "<br/><b>ID:</b> " + this.adminId
                + "<br/><b>Department:</b> " + this.adminDepartment + "</html>");
        adminChip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        adminChip.setForeground(new Color(37, 93, 180));
        adminChip.setOpaque(true);
        adminChip.setBackground(new Color(231, 241, 255));
        adminChip.setBorder(new EmptyBorder(10, 12, 10, 12));
        header.add(adminChip, BorderLayout.EAST);

        cards.setOpaque(false);
        cards.add(createHomePanel(), HOME_CARD);
        cards.add(createTeacherPanel(), TEACHER_CARD);
        cards.add(createStudentPanel(), STUDENT_CARD);
        cards.add(createSubjectPanel(), SUBJECT_CARD);
        cards.add(createResultsPanel(), RESULTS_CARD);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);

        JButton logout = createActionButton("Logout", new Color(186, 58, 44));
        footer.add(logout);

        logout.addActionListener(e -> {
            frame.dispose();
            new LoginFrame();
        });

        root.add(header, BorderLayout.NORTH);
        root.add(cards, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        frame.add(root, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        JPanel infoStrip = new JPanel(new GridLayout(1, 4, 12, 0));
        infoStrip.setOpaque(false);
        infoStrip.add(createInfoCard("Teacher Records", "Create and manage teacher profiles", new Color(234, 245, 255)));
        infoStrip.add(createInfoCard("Student Records", "Onboard students and maintain data", new Color(234, 252, 240)));
        infoStrip.add(createInfoCard("Subject Records", "Manage courses and academic subjects", new Color(255, 240, 245)));
        infoStrip.add(createInfoCard("Results", "Review and validate academic outcomes", new Color(255, 245, 232)));

        JPanel actionsPanel = new JPanel(new GridLayout(1, 4, 14, 0));
        actionsPanel.setOpaque(false);

        JPanel teacherCard = createActionCard("Manage Teachers", "Add, view, and remove teacher profiles.", new Color(31, 108, 205));
        JButton manageTeacher = createActionButton("Open", new Color(31, 108, 205));
        teacherCard.add(manageTeacher, BorderLayout.SOUTH);
        manageTeacher.addActionListener(e -> {
            loadTeachers();
            if (teacherTabPane != null) {
                teacherTabPane.setSelectedIndex(1);
            }
            cardLayout.show(cards, TEACHER_CARD);
        });

        JPanel studentCard = createActionCard("Manage Students", "Add, view, and remove student records.", new Color(35, 140, 93));
        JButton manageStudent = createActionButton("Open", new Color(35, 140, 93));
        studentCard.add(manageStudent, BorderLayout.SOUTH);
        manageStudent.addActionListener(e -> {
            loadStudents();
            if (studentTabPane != null) {
                studentTabPane.setSelectedIndex(1);
            }
            cardLayout.show(cards, STUDENT_CARD);
        });

        JPanel subjectCard = createActionCard("Manage Subjects", "Add, view, and remove course subjects.", new Color(155, 35, 140));
        JButton manageSubject = createActionButton("Open", new Color(155, 35, 140));
        subjectCard.add(manageSubject, BorderLayout.SOUTH);
        manageSubject.addActionListener(e -> {
            loadSubjects();
            if (subjectTabPane != null) {
                subjectTabPane.setSelectedIndex(1);
            }
            cardLayout.show(cards, SUBJECT_CARD);
        });

        JPanel resultCard = createActionCard("View Results", "Inspect result records and academic data snapshots.", new Color(204, 132, 40));
        JButton viewResults = createActionButton("Open", new Color(204, 132, 40));
        resultCard.add(viewResults, BorderLayout.SOUTH);
        viewResults.addActionListener(e -> {
            loadResults();
            cardLayout.show(cards, RESULTS_CARD);
        });

        actionsPanel.add(teacherCard);
        actionsPanel.add(studentCard);
        actionsPanel.add(subjectCard);
        actionsPanel.add(resultCard);

        content.add(infoStrip, BorderLayout.NORTH);
        content.add(actionsPanel, BorderLayout.CENTER);
        return content;
    }

    private JPanel createTeacherPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(6, 0, 0, 0));

        JLabel heading = new JLabel("Manage Teachers");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setForeground(new Color(34, 50, 84));

        // Create tabbed pane for Add and View tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setPreferredSize(new Dimension(920, 560));

        // Add Teacher Tab
        JPanel addTab = new JPanel(new BorderLayout(0, 16));
        addTab.setOpaque(false);
        addTab.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 223, 242)),
                new EmptyBorder(26, 30, 24, 30)
        ));
        formCard.setPreferredSize(new Dimension(860, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField = createInputField();
        JTextField nameField = createInputField();
        JTextField deptField = createInputField();
        JTextField emailField = createInputField();
        JPasswordField passField = createPasswordField();

        JLabel formTitle = new JLabel("Add New Teacher");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        formTitle.setForeground(new Color(38, 57, 97));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 14, 8);
        formCard.add(formTitle, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        addFormRow(formCard, gbc, 1, "Teacher ID", idField);
        addFormRow(formCard, gbc, 2, "Full Name", nameField);
        addFormRow(formCard, gbc, 3, "Department", deptField);
        addFormRow(formCard, gbc, 4, "Email", emailField);
        addFormRow(formCard, gbc, 5, "Password", passField);

        JPanel addActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        addActions.setOpaque(false);

        JButton saveBtn = createActionButton("Add Teacher", new Color(31, 108, 205));
        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String dept = deptField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            if (id.isEmpty() || name.isEmpty() || dept.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields.");
                return;
            }

            try (Connection con = DBConnection.getDbConnection();
                 PreparedStatement pt = con.prepareStatement("INSERT INTO teacher VALUES(?,?,?,?,?)")) {
                pt.setString(1, id);
                pt.setString(2, name);
                pt.setString(3, email);
                pt.setString(4, dept);
                pt.setString(5, pass);
                pt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Teacher added successfully.");
                idField.setText("");
                nameField.setText("");
                deptField.setText("");
                emailField.setText("");
                passField.setText("");
                loadTeachers(); // Refresh the view tab
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Insert failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        addActions.add(saveBtn);
        addTab.add(formCard, BorderLayout.CENTER);
        addTab.add(addActions, BorderLayout.SOUTH);

        // View Teachers Tab
        JPanel viewTab = new JPanel(new BorderLayout(0, 12));
        viewTab.setOpaque(false);
        viewTab.setBorder(new EmptyBorder(16, 0, 0, 0));

        teacherTabPane = tabbedPane;
        // Initialize table with empty model to prevent null issues
        teacherTable.setModel(new DefaultTableModel(new String[]{"ID", "Name", "Email", "Department"}, 0));
        styleDataTable(teacherTable);
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        JLabel teacherFilterLabel = new JLabel("Filter:");
        teacherFilterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        teacherFilterLabel.setForeground(new Color(67, 84, 118));
        teacherSearchField.setPreferredSize(new Dimension(220, 34));
        teacherSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teacherSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 211, 233)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        teacherFilterColumn.setPreferredSize(new Dimension(150, 34));
        teacherFilterColumn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(teacherFilterLabel);
        filterPanel.add(teacherSearchField);
        filterPanel.add(teacherFilterColumn);
        teacherSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { applyTableFilter(teacherSorter, teacherSearchField, teacherFilterColumn); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { applyTableFilter(teacherSorter, teacherSearchField, teacherFilterColumn); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { applyTableFilter(teacherSorter, teacherSearchField, teacherFilterColumn); }
        });
        teacherFilterColumn.addActionListener(e -> applyTableFilter(teacherSorter, teacherSearchField, teacherFilterColumn));
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.setPreferredSize(new Dimension(900, 400));
        scrollPane.setMinimumSize(new Dimension(400, 200));
        viewTab.add(filterPanel, BorderLayout.NORTH);

        JPanel viewActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        viewActions.setOpaque(false);

        JButton deleteBtn = createActionButton("Delete Selected", new Color(186, 58, 44));
        JButton refreshBtn = createActionButton("Refresh", new Color(31, 108, 205));

        deleteBtn.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a teacher to delete.");
                return;
            }
            int modelRow = teacherTable.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) teacherTable.getModel();
            String teacherId = model.getValueAt(modelRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete teacher ID: " + teacherId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = DBConnection.getDbConnection();
                     PreparedStatement pt = con.prepareStatement("DELETE FROM teacher WHERE teacher_id = ?")) {
                    pt.setString(1, teacherId);
                    int rowsAffected = pt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "Teacher deleted successfully.");
                        loadTeachers();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Teacher not found.", "Delete failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshBtn.addActionListener(e -> loadTeachers());

        viewActions.add(deleteBtn);
        viewActions.add(refreshBtn);

        viewTab.add(scrollPane, BorderLayout.CENTER);
        viewTab.add(viewActions, BorderLayout.SOUTH);

        tabbedPane.addTab("Add Teacher", addTab);
        tabbedPane.addTab("View Teachers", viewTab);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton backBtn = createActionButton("Back", new Color(107, 119, 146));
        backBtn.addActionListener(e -> cardLayout.show(cards, HOME_CARD));

        actions.add(backBtn);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(6, 0, 0, 0));

        JLabel heading = new JLabel("Manage Students");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setForeground(new Color(34, 50, 84));

        // Create tabbed pane for Add and View tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setPreferredSize(new Dimension(920, 560));

        // Add Student Tab
        JPanel addTab = new JPanel(new BorderLayout(0, 16));
        addTab.setOpaque(false);
        addTab.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 223, 242)),
                new EmptyBorder(26, 30, 24, 30)
        ));
        formCard.setPreferredSize(new Dimension(860, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField = createInputField();
        JTextField nameField = createInputField();
        JTextField deptField = createInputField();
        JTextField emailField = createInputField();
        JPasswordField passField = createPasswordField();

        JLabel formTitle = new JLabel("Add New Student");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        formTitle.setForeground(new Color(38, 57, 97));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 14, 8);
        formCard.add(formTitle, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        addFormRow(formCard, gbc, 1, "Student ID", idField);
        addFormRow(formCard, gbc, 2, "Full Name", nameField);
        addFormRow(formCard, gbc, 3, "Department", deptField);
        addFormRow(formCard, gbc, 4, "Email", emailField);
        addFormRow(formCard, gbc, 5, "Password", passField);

        JPanel addActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        addActions.setOpaque(false);

        JButton saveBtn = createActionButton("Add Student", new Color(35, 140, 93));
        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String dept = deptField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            if (id.isEmpty() || name.isEmpty() || dept.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields.");
                return;
            }

            try (Connection con = DBConnection.getDbConnection();
                 PreparedStatement pt = con.prepareStatement("INSERT INTO student VALUES(?,?,?,?,?)")) {
                pt.setString(1, id);
                pt.setString(2, name);
                pt.setString(3, email);
                pt.setString(4, pass);
                pt.setString(5, dept);
                pt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Student added successfully.");
                idField.setText("");
                nameField.setText("");
                deptField.setText("");
                emailField.setText("");
                passField.setText("");
                loadStudents(); // Refresh the view tab
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Insert failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        addActions.add(saveBtn);
        addTab.add(formCard, BorderLayout.CENTER);
        addTab.add(addActions, BorderLayout.SOUTH);

        // View Students Tab
        JPanel viewTab = new JPanel(new BorderLayout(0, 12));
        viewTab.setOpaque(false);
        viewTab.setBorder(new EmptyBorder(16, 0, 0, 0));

        studentTabPane = tabbedPane;
        // Initialize table with empty model to prevent null issues
        studentTable.setModel(new DefaultTableModel(new String[]{"ID", "Name", "Email", "Department"}, 0));
        styleDataTable(studentTable);
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        JLabel studentFilterLabel = new JLabel("Filter:");
        studentFilterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentFilterLabel.setForeground(new Color(67, 84, 118));
        studentSearchField.setPreferredSize(new Dimension(220, 34));
        studentSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 211, 233)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        studentFilterColumn.setPreferredSize(new Dimension(150, 34));
        studentFilterColumn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(studentFilterLabel);
        filterPanel.add(studentSearchField);
        filterPanel.add(studentFilterColumn);
        studentSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { applyTableFilter(studentSorter, studentSearchField, studentFilterColumn); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { applyTableFilter(studentSorter, studentSearchField, studentFilterColumn); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { applyTableFilter(studentSorter, studentSearchField, studentFilterColumn); }
        });
        studentFilterColumn.addActionListener(e -> applyTableFilter(studentSorter, studentSearchField, studentFilterColumn));
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.setPreferredSize(new Dimension(900, 400));
        scrollPane.setMinimumSize(new Dimension(400, 200));
        viewTab.add(filterPanel, BorderLayout.NORTH);

        JPanel viewActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        viewActions.setOpaque(false);

        JButton deleteBtn = createActionButton("Delete Selected", new Color(186, 58, 44));
        JButton refreshBtn = createActionButton("Refresh", new Color(35, 140, 93));

        deleteBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a student to delete.");
                return;
            }
            int modelRow = studentTable.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            String studentId = model.getValueAt(modelRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete student ID: " + studentId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = DBConnection.getDbConnection();
                     PreparedStatement pt = con.prepareStatement("DELETE FROM student WHERE student_id = ?")) {
                    pt.setString(1, studentId);
                    int rowsAffected = pt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "Student deleted successfully.");
                        loadStudents();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Student not found.", "Delete failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshBtn.addActionListener(e -> loadStudents());

        viewActions.add(deleteBtn);
        viewActions.add(refreshBtn);

        viewTab.add(scrollPane, BorderLayout.CENTER);
        viewTab.add(viewActions, BorderLayout.SOUTH);

        tabbedPane.addTab("Add Student", addTab);
        tabbedPane.addTab("View Students", viewTab);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton backBtn = createActionButton("Back", new Color(107, 119, 146));
        backBtn.addActionListener(e -> cardLayout.show(cards, HOME_CARD));

        actions.add(backBtn);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(6, 0, 0, 0));

        JLabel heading = new JLabel("Manage Subjects");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setForeground(new Color(34, 50, 84));

        // Create tabbed pane for Add and View tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setPreferredSize(new Dimension(920, 560));

        // Add Subject Tab
        JPanel addTab = new JPanel(new BorderLayout(0, 16));
        addTab.setOpaque(false);
        addTab.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 223, 242)),
                new EmptyBorder(26, 30, 24, 30)
        ));
        formCard.setPreferredSize(new Dimension(860, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField = createInputField();
        JTextField nameField = createInputField();
        JTextField teacherField = createInputField();
        JComboBox<String> semesterBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        semesterBox.setPreferredSize(new Dimension(420, 36));
        semesterBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel formTitle = new JLabel("Add New Subject");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        formTitle.setForeground(new Color(38, 57, 97));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 14, 8);
        formCard.add(formTitle, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        addFormRow(formCard, gbc, 1, "Subject ID", idField);
        addFormRow(formCard, gbc, 2, "Subject Name", nameField);
        addFormRow(formCard, gbc, 3, "Teacher ID", teacherField);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel semesterLabel = new JLabel("Semester");
        semesterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        semesterLabel.setForeground(new Color(66, 82, 118));
        formCard.add(semesterLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formCard.add(semesterBox, gbc);
        gbc.fill = GridBagConstraints.NONE;

        JPanel addActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        addActions.setOpaque(false);

        JButton saveBtn = createActionButton("Add Subject", new Color(155, 35, 140));
        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String teacherId = teacherField.getText().trim();
            String semester = (String) semesterBox.getSelectedItem();

            if (id.isEmpty() || name.isEmpty() || teacherId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all required fields.");
                return;
            }

            try (Connection con = DBConnection.getDbConnection();
                 PreparedStatement pt = con.prepareStatement("INSERT INTO course VALUES(?,?,?,?)")) {
                pt.setString(1, id);
                pt.setString(2, name);
                pt.setString(3, teacherId);
                pt.setString(4, semester);
                pt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Subject added successfully.");
                idField.setText("");
                nameField.setText("");
                teacherField.setText("");
                semesterBox.setSelectedIndex(0);
                loadSubjects(); // Refresh the view tab
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Insert failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        addActions.add(saveBtn);
        addTab.add(formCard, BorderLayout.CENTER);
        addTab.add(addActions, BorderLayout.SOUTH);

        // View Subjects Tab
        JPanel viewTab = new JPanel(new BorderLayout(0, 12));
        viewTab.setOpaque(false);
        viewTab.setBorder(new EmptyBorder(16, 0, 0, 0));

        subjectTabPane = tabbedPane;
        // Initialize table with empty model to prevent null issues
        subjectTable.setModel(new DefaultTableModel(new String[]{"ID", "Subject Name", "Semester", "Teacher"}, 0));
        styleDataTable(subjectTable);
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        JLabel subjectFilterLabel = new JLabel("Filter:");
        subjectFilterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subjectFilterLabel.setForeground(new Color(67, 84, 118));
        subjectSearchField.setPreferredSize(new Dimension(220, 34));
        subjectSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjectSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 211, 233)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        subjectFilterColumn.setPreferredSize(new Dimension(150, 34));
        subjectFilterColumn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(subjectFilterLabel);
        filterPanel.add(subjectSearchField);
        filterPanel.add(subjectFilterColumn);
        subjectSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { applyTableFilter(subjectSorter, subjectSearchField, subjectFilterColumn); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { applyTableFilter(subjectSorter, subjectSearchField, subjectFilterColumn); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { applyTableFilter(subjectSorter, subjectSearchField, subjectFilterColumn); }
        });
        subjectFilterColumn.addActionListener(e -> applyTableFilter(subjectSorter, subjectSearchField, subjectFilterColumn));
        JScrollPane scrollPane = new JScrollPane(subjectTable);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.setPreferredSize(new Dimension(900, 400));
        scrollPane.setMinimumSize(new Dimension(400, 200));
        viewTab.add(filterPanel, BorderLayout.NORTH);

        JPanel viewActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        viewActions.setOpaque(false);

        JButton deleteBtn = createActionButton("Delete Selected", new Color(186, 58, 44));
        JButton refreshBtn = createActionButton("Refresh", new Color(155, 35, 140));

        deleteBtn.addActionListener(e -> {
            int selectedRow = subjectTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a subject to delete.");
                return;
            }
            int modelRow = subjectTable.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) subjectTable.getModel();
            String subjectId = model.getValueAt(modelRow, 0).toString();
            String subjectName = model.getValueAt(modelRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete subject: " + subjectName + " (ID: " + subjectId + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = DBConnection.getDbConnection();
                     PreparedStatement pt = con.prepareStatement("DELETE FROM course WHERE course_id = ?")) {
                    pt.setString(1, subjectId);
                    int rowsAffected = pt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "Subject deleted successfully.");
                        loadSubjects();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Subject not found.", "Delete failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshBtn.addActionListener(e -> loadSubjects());

        viewActions.add(deleteBtn);
        viewActions.add(refreshBtn);

        viewTab.add(scrollPane, BorderLayout.CENTER);
        viewTab.add(viewActions, BorderLayout.SOUTH);

        tabbedPane.addTab("Add Subject", addTab);
        tabbedPane.addTab("View Subjects", viewTab);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton backBtn = createActionButton("Back", new Color(107, 119, 146));
        backBtn.addActionListener(e -> cardLayout.show(cards, HOME_CARD));

        actions.add(backBtn);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JLabel heading = new JLabel("Results");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setForeground(new Color(34, 50, 84));

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterLabel.setForeground(new Color(67, 84, 118));

        resultsSearchField.setPreferredSize(new Dimension(220, 34));
        resultsSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultsSearchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 211, 233)),
                new EmptyBorder(6, 8, 6, 8)
        ));

        resultsFilterColumn.setPreferredSize(new Dimension(150, 34));
        resultsFilterColumn.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        resultsSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyResultsFilter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyResultsFilter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyResultsFilter();
            }
        });
        resultsFilterColumn.addActionListener(e -> applyResultsFilter());

        filters.add(filterLabel);
        filters.add(resultsSearchField);
        filters.add(resultsFilterColumn);
        top.add(heading, BorderLayout.NORTH);
        top.add(filters, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowHeight(30);
        resultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        resultsTable.getTableHeader().setBackground(new Color(230, 237, 252));
        resultsTable.getTableHeader().setForeground(new Color(37, 63, 111));
        resultsTable.setGridColor(new Color(223, 231, 247));
        resultsTable.setSelectionBackground(new Color(211, 227, 255));
        resultsTable.setSelectionForeground(new Color(34, 50, 84));
        resultsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton backBtn = createActionButton("Back", new Color(107, 119, 146));
        JButton refreshBtn = createActionButton("Refresh", new Color(204, 132, 40));

        backBtn.addActionListener(e -> cardLayout.show(cards, HOME_CARD));
        refreshBtn.addActionListener(e -> loadResults());

        actions.add(backBtn);
        actions.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent input) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel rowLabel = new JLabel(label);
        rowLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rowLabel.setForeground(new Color(66, 82, 118));
        panel.add(rowLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(input, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }

    private void loadResults() {
        try (Connection con = DBConnection.getDbConnection()) {
            String studentNameColumn = resolveFirstExistingColumn(con, "student", new String[]{"name", "student_name"});
            String courseNameColumn = resolveFirstExistingColumn(con, "course", new String[]{"course_name", "name"});
            String teacherNameColumn = resolveFirstExistingColumn(con, "teacher", new String[]{"name", "teacher_name"});
            String insertedAtColumn = resolveFirstExistingColumn(con, "result", new String[]{"inserted_at"});

            if (studentNameColumn == null || courseNameColumn == null) {
                throw new RuntimeException("Missing required name columns in student/course table.");
            }

            String dateSelect = insertedAtColumn == null
                    ? "'N/A' as awarded_on"
                    : "DATE_FORMAT(r." + insertedAtColumn + ", '%d/%m/%Y') as awarded_on";
            String awardedBySelect = teacherNameColumn == null
                    ? "r.awardedby as awarded_by"
                    : "coalesce(t." + teacherNameColumn + ", r.awardedby) as awarded_by";
            String teacherJoin = teacherNameColumn == null ? "" : "left join teacher t on r.awardedby = t.teacher_id ";

            String sql =
                    "Select s." + studentNameColumn + " as student_name, " +
                    "c." + courseNameColumn + " as course_name, " +
                    "r.marks, " +
                    awardedBySelect + ", " +
                    dateSelect + " " +
                    "from result r " +
                    "join student s on r.student_id = s.student_id " +
                    "join course c on r.course_id = c.course_id " +
                    teacherJoin +
                    "order by s." + studentNameColumn;

            try (PreparedStatement pt = con.prepareStatement(sql);
                 ResultSet rs = pt.executeQuery()) {

                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                String[] columns = new String[colCount];
                for (int i = 1; i <= colCount; i++) {
                    columns[i - 1] = meta.getColumnLabel(i);
                }

                DefaultTableModel model = new DefaultTableModel(columns, 0);
                while (rs.next()) {
                    Object[] row = new Object[colCount];
                    for (int i = 1; i <= colCount; i++) {
                        row[i - 1] = rs.getObject(i);
                    }
                    model.addRow(row);
                }
                resultsTable.setModel(model);
                resultsSorter = new TableRowSorter<>(model);
                resultsTable.setRowSorter(resultsSorter);
                applyResultsFilter();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Unable to load results", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyResultsFilter() {
        if (resultsSorter == null) {
            return;
        }
        String text = resultsSearchField.getText() == null ? "" : resultsSearchField.getText().trim();
        if (text.isEmpty()) {
            resultsSorter.setRowFilter(null);
            return;
        }

        int selected = resultsFilterColumn.getSelectedIndex();
        String regex = "(?i)" + Pattern.quote(text);
        if (selected <= 0) {
            resultsSorter.setRowFilter(RowFilter.regexFilter(regex));
        } else {
            int modelColumn = selected - 1;
            resultsSorter.setRowFilter(RowFilter.regexFilter(regex, modelColumn));
        }
    }

    private void applyTableFilter(TableRowSorter<DefaultTableModel> sorter, JTextField searchField, JComboBox<String> filterColumn) {
        if (sorter == null) {
            return;
        }
        String text = searchField.getText() == null ? "" : searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        String regex = "(?i)" + Pattern.quote(text);
        int selected = filterColumn.getSelectedIndex();
        if (selected <= 0) {
            sorter.setRowFilter(RowFilter.regexFilter(regex));
        } else {
            int modelColumn = selected - 1;
            sorter.setRowFilter(RowFilter.regexFilter(regex, modelColumn));
        }
    }

    private String resolveFirstExistingColumn(Connection con, String tableName, String[] candidates) {
        try {
            DatabaseMetaData meta = con.getMetaData();
            for (String candidate : candidates) {
                try (ResultSet cols = meta.getColumns(con.getCatalog(), null, tableName, candidate)) {
                    if (cols.next()) {
                        return candidate;
                    }
                }
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private void loadTeachers() {
        try (Connection con = DBConnection.getDbConnection();
             PreparedStatement pt = con.prepareStatement("SELECT teacher_id, teacher_name, email, department FROM teacher ORDER BY teacher_name");
             ResultSet rs = pt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Department"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("teacher_id"),
                    rs.getString("teacher_name"),
                    rs.getString("email"),
                    rs.getString("department")
                });
            }
            teacherTable.setModel(model);
            teacherSorter = new TableRowSorter<>(model);
            teacherTable.setRowSorter(teacherSorter);
            applyTableFilter(teacherSorter, teacherSearchField, teacherFilterColumn);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Unable to load teachers", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudents() {
        try (Connection con = DBConnection.getDbConnection();
             PreparedStatement pt = con.prepareStatement("SELECT student_id, student_name, email, department FROM student ORDER BY student_name");
             ResultSet rs = pt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Department"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("department")
                });
            }
            studentTable.setModel(model);
            studentSorter = new TableRowSorter<>(model);
            studentTable.setRowSorter(studentSorter);
            applyTableFilter(studentSorter, studentSearchField, studentFilterColumn);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Unable to load students", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSubjects() {
        try (Connection con = DBConnection.getDbConnection();
             PreparedStatement pt = con.prepareStatement(
                 "SELECT c.course_id, c.course_name, c.semester, t.teacher_name " +
                 "FROM course c LEFT JOIN teacher t ON c.teacher_id = t.teacher_id " +
                 "ORDER BY c.course_name");
             ResultSet rs = pt.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Subject Name", "Semester", "Teacher"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("semester"),
                    rs.getString("teacher_name") != null ? rs.getString("teacher_name") : "Not Assigned"
                });
            }
            subjectTable.setModel(model);
            subjectSorter = new TableRowSorter<>(model);
            subjectTable.setRowSorter(subjectSorter);
            applyTableFilter(subjectSorter, subjectSearchField, subjectFilterColumn);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Unable to load subjects", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createInfoCard(String heading, String text, Color bg) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bg);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel head = new JLabel(heading);
        head.setFont(new Font("Segoe UI", Font.BOLD, 14));
        head.setForeground(new Color(48, 63, 95));
        head.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel("<html><div style='width:220px'>" + text + "</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(new Color(89, 103, 132));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(head);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(desc);
        return panel;
    }

    private JPanel createActionCard(String heading, String description, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(34, 50, 84));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(0, 4));

        JLabel desc = new JLabel("<html><div style='width:250px'>" + description + "</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(new Color(95, 108, 136));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(title);
        body.add(Box.createRigidArea(new Dimension(0, 10)));
        body.add(desc);

        card.add(accentBar, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JButton createActionButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JTextField createInputField() {
        JTextField field = new JTextField();
        field.setColumns(24);
        field.setPreferredSize(new Dimension(420, 36));
        field.setMinimumSize(new Dimension(420, 36));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(196, 208, 230)),
                new EmptyBorder(7, 10, 7, 10)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setColumns(24);
        field.setPreferredSize(new Dimension(420, 36));
        field.setMinimumSize(new Dimension(420, 36));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(196, 208, 230)),
                new EmptyBorder(7, 10, 7, 10)
        ));
        return field;
    }

    private void styleDataTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(230, 237, 252));
        table.getTableHeader().setForeground(new Color(37, 63, 111));
        table.setGridColor(new Color(223, 231, 247));
        table.setSelectionBackground(new Color(211, 227, 255));
        table.setSelectionForeground(new Color(34, 50, 84));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
    }

    private String safeValue(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }
}
