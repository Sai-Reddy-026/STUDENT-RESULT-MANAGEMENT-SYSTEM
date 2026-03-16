import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class TeacherFrame {
    JFrame frame = new JFrame();

    public TeacherFrame(teacherverify check) {
        frame.setTitle("Teacher Dashboard");
        frame.setSize(900, 560);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(new Color(243, 248, 255));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Teacher Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(34, 50, 84));

        JLabel subtitle = new JLabel("Add and update marks by Student ID and Course ID");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(95, 108, 136));

        JPanel titleWrap = new JPanel();
        titleWrap.setOpaque(false);
        titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
        titleWrap.add(title);
        titleWrap.add(Box.createRigidArea(new Dimension(0, 6)));
        titleWrap.add(subtitle);

        String teacherName = safeValue(check.getTeacherName());
        String teacherId = safeValue(check.getId());
        String teacherDepartment = safeValue(check.getTeacherDepartment());
        JLabel chip = new JLabel("<html><b>Name:</b> " + teacherName
                + "<br/><b>ID:</b> " + teacherId
                + "<br/><b>Department:</b> " + teacherDepartment + "</html>");
        chip.setOpaque(true);
        chip.setBackground(new Color(226, 244, 237));
        chip.setForeground(new Color(29, 114, 86));
        chip.setBorder(new EmptyBorder(10, 12, 10, 12));
        chip.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(titleWrap, BorderLayout.WEST);
        header.add(chip, BorderLayout.EAST);

        JPanel addCard = new JPanel(new GridBagLayout());
        addCard.setBackground(Color.WHITE);
        addCard.setBorder(new EmptyBorder(22, 22, 22, 22));

        GridBagConstraints addGbc = new GridBagConstraints();
        addGbc.insets = new Insets(10, 10, 10, 10);
        addGbc.anchor = GridBagConstraints.WEST;

        JTextField addStudentIdField = createInputField();
        JTextField addCourseIdField = createInputField();
        JTextField addMarksField = createInputField();
        JLabel addInfo = createInfoLabel();

        JLabel addHeading = new JLabel("Add New Marks");
        addHeading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        addHeading.setForeground(new Color(45, 62, 98));

        addGbc.gridx = 0;
        addGbc.gridy = 0;
        addGbc.gridwidth = 2;
        addGbc.insets = new Insets(0, 10, 12, 10);
        addCard.add(addHeading, addGbc);
        addGbc.gridwidth = 1;
        addGbc.insets = new Insets(10, 10, 10, 10);

        addFormRow(addCard, addGbc, 1, "Student ID", addStudentIdField);
        addFormRow(addCard, addGbc, 2, "Course ID", addCourseIdField);
        addFormRow(addCard, addGbc, 3, "Marks (0-100)", addMarksField);

        JPanel addButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        addButtons.setOpaque(false);
        JButton addClearBtn = createButton("Clear", new Color(109, 121, 146));
        JButton submitBtn = createButton("Submit Marks", new Color(31, 130, 102));
        addButtons.add(addClearBtn);
        addButtons.add(submitBtn);

        addGbc.gridx = 0;
        addGbc.gridy = 4;
        addGbc.gridwidth = 2;
        addGbc.fill = GridBagConstraints.HORIZONTAL;
        addGbc.weightx = 1;
        addGbc.insets = new Insets(6, 10, 2, 10);
        addCard.add(addInfo, addGbc);

        addGbc.gridy = 5;
        addGbc.insets = new Insets(8, 10, 0, 10);
        addCard.add(addButtons, addGbc);

        addGbc.gridwidth = 1;
        addGbc.fill = GridBagConstraints.NONE;
        addGbc.weightx = 0;
        addGbc.insets = new Insets(10, 10, 10, 10);

        JPanel updateCard = new JPanel(new GridBagLayout());
        updateCard.setBackground(Color.WHITE);
        updateCard.setBorder(new EmptyBorder(22, 22, 22, 22));

        GridBagConstraints updateGbc = new GridBagConstraints();
        updateGbc.insets = new Insets(10, 10, 10, 10);
        updateGbc.anchor = GridBagConstraints.WEST;

        JTextField updateStudentIdField = createInputField();
        JTextField updateCourseIdField = createInputField();
        JTextField updateMarksField = createInputField();
        JLabel updateInfo = createInfoLabel();

        JLabel updateHeading = new JLabel("Update Existing Marks");
        updateHeading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        updateHeading.setForeground(new Color(45, 62, 98));

        updateGbc.gridx = 0;
        updateGbc.gridy = 0;
        updateGbc.gridwidth = 2;
        updateGbc.insets = new Insets(0, 10, 12, 10);
        updateCard.add(updateHeading, updateGbc);
        updateGbc.gridwidth = 1;
        updateGbc.insets = new Insets(10, 10, 10, 10);

        addFormRow(updateCard, updateGbc, 1, "Student ID", updateStudentIdField);
        addFormRow(updateCard, updateGbc, 2, "Course ID", updateCourseIdField);
        addFormRow(updateCard, updateGbc, 3, "Marks (0-100)", updateMarksField);

        JPanel updateButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        updateButtons.setOpaque(false);
        JButton updateClearBtn = createButton("Clear", new Color(109, 121, 146));
        JButton updateBtn = createButton("Update Marks", new Color(23, 122, 170));
        updateButtons.add(updateClearBtn);
        updateButtons.add(updateBtn);

        updateGbc.gridx = 0;
        updateGbc.gridy = 4;
        updateGbc.gridwidth = 2;
        updateGbc.fill = GridBagConstraints.HORIZONTAL;
        updateGbc.weightx = 1;
        updateGbc.insets = new Insets(6, 10, 2, 10);
        updateCard.add(updateInfo, updateGbc);

        updateGbc.gridy = 5;
        updateGbc.insets = new Insets(8, 10, 0, 10);
        updateCard.add(updateButtons, updateGbc);

        addClearBtn.addActionListener(e -> {
            addStudentIdField.setText("");
            addCourseIdField.setText("");
            addMarksField.setText("");
            addInfo.setText(" ");
        });

        submitBtn.addActionListener(e -> {
            String studentId = addStudentIdField.getText().trim();
            String courseId = addCourseIdField.getText().trim();
            String marks = addMarksField.getText().trim();

            if (studentId.isEmpty() || courseId.isEmpty() || marks.isEmpty()) {
                setError(addInfo, "Please fill Student ID, Course ID, and Marks.");
                return;
            }

            Integer marksValue = parseMarks(marks, addInfo);
            if (marksValue == null) {
                return;
            }

            try {
                check.insertdata(studentId, courseId, String.valueOf(marksValue));
                setSuccess(addInfo, "Marks inserted successfully.");
                addStudentIdField.setText("");
                addCourseIdField.setText("");
                addMarksField.setText("");
            } catch (Exception ex) {
                setError(addInfo, "Insert failed: " + ex.getMessage());
            }
        });

        Runnable autoLoad = () -> {
            String studentId = updateStudentIdField.getText().trim();
            String courseId = updateCourseIdField.getText().trim();

            if (studentId.isEmpty() || courseId.isEmpty()) {
                updateMarksField.setText("");
                updateInfo.setText("Enter Student ID and Course ID to auto-load marks.");
                return;
            }

            try {
                String existingMarks = check.fetchMarks(studentId, courseId);
                if (existingMarks == null) {
                    updateMarksField.setText("");
                    setError(updateInfo, "No marks found for this student/course under your records.");
                    return;
                }
                updateMarksField.setText(existingMarks);
                setSuccess(updateInfo, "Marks auto-loaded. Edit and click Update Marks.");
            } catch (Exception ex) {
                setError(updateInfo, "Auto-load failed: " + ex.getMessage());
            }
        };

        attachAutoLoad(updateStudentIdField, autoLoad);
        attachAutoLoad(updateCourseIdField, autoLoad);

        updateClearBtn.addActionListener(e -> {
            updateStudentIdField.setText("");
            updateCourseIdField.setText("");
            updateMarksField.setText("");
            updateInfo.setText(" ");
        });

        updateBtn.addActionListener(e -> {
            String studentId = updateStudentIdField.getText().trim();
            String courseId = updateCourseIdField.getText().trim();
            String marks = updateMarksField.getText().trim();

            if (studentId.isEmpty() || courseId.isEmpty() || marks.isEmpty()) {
                setError(updateInfo, "Please fill Student ID, Course ID, and Marks to update.");
                return;
            }

            Integer marksValue = parseMarks(marks, updateInfo);
            if (marksValue == null) {
                return;
            }

            try {
                boolean updated = check.updateMarks(studentId, courseId, String.valueOf(marksValue));
                if (updated) {
                    setSuccess(updateInfo, "Marks updated successfully.");
                } else {
                    setError(updateInfo, "No matching record found to update.");
                }
            } catch (Exception ex) {
                setError(updateInfo, "Update failed: " + ex.getMessage());
            }
        });

        JPanel modules = new JPanel(new GridLayout(1, 2, 12, 0));
        modules.setOpaque(false);
        modules.add(addCard);
        modules.add(updateCard);

        JButton logout = createButton("Logout", new Color(186, 58, 44));
        logout.addActionListener(e -> {
            frame.dispose();
            new LoginFrame();
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(logout);

        root.add(header, BorderLayout.NORTH);
        root.add(modules, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        frame.add(root, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private Integer parseMarks(String marks, JLabel info) {
        int marksValue;
        try {
            marksValue = Integer.parseInt(marks);
        } catch (NumberFormatException ex) {
            setError(info, "Marks must be a numeric value.");
            return null;
        }

        if (marksValue < 0 || marksValue > 100) {
            setError(info, "Marks should be between 0 and 100.");
            return null;
        }
        return marksValue;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent input) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel rowLabel = new JLabel(label);
        rowLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rowLabel.setForeground(new Color(67, 84, 118));
        panel.add(rowLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(input, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }

    private JTextField createInputField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(280, 36));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 211, 233)),
                new EmptyBorder(7, 9, 7, 9)
        ));
        return field;
    }

    private JLabel createInfoLabel() {
        JLabel info = new JLabel(" ");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.setForeground(new Color(183, 63, 63));
        return info;
    }

    private void setError(JLabel label, String text) {
        label.setForeground(new Color(183, 63, 63));
        label.setText(text);
    }

    private void setSuccess(JLabel label, String text) {
        label.setForeground(new Color(29, 130, 99));
        label.setText(text);
    }

    private void attachAutoLoad(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        });
    }

    private JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(130, 40));
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
