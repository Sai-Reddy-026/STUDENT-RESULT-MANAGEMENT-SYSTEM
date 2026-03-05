import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentLoginFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(StudentLoginFrame.class.getName());
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_MILLIS = 30_000;

    private int failedAttempts = 0;
    private long lockoutUntil = 0L;

    public StudentLoginFrame() {
        setTitle("Student Login");
        setSize(760, 470);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(20, 0));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.setBackground(new Color(244, 248, 255));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(new Color(37, 102, 176));
        left.setBorder(new EmptyBorder(24, 24, 24, 24));
        left.setPreferredSize(new Dimension(280, 0));

        JLabel leftTitle = new JLabel("Student Portal");
        leftTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        leftTitle.setForeground(Color.WHITE);
        leftTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel leftSub = new JLabel("<html>View marks, grades,<br/>course results and<br/>progress details.</html>");
        leftSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftSub.setForeground(new Color(220, 235, 255));
        leftSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backBtn = new JButton("Back to Role Selection");
        styleSecondaryButton(backBtn);
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(leftTitle);
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(leftSub);
        left.add(Box.createVerticalGlue());
        left.add(backBtn);

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(28, 30, 28, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Student Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(34, 50, 84));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your credentials to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(102, 117, 145));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rollLabel = new JLabel("Student ID");
        rollLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rollLabel.setForeground(new Color(67, 83, 117));
        rollLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField rollField = new JTextField();
        styleInput(rollField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setForeground(new Color(67, 83, 117));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passField = new JPasswordField();
        styleInput(passField);

        JLabel infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(188, 65, 65));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = new JButton("Login");
        stylePrimaryButton(loginBtn);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 22)));
        card.add(rollLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(rollField);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(passField);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(infoLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(loginBtn);

        loginBtn.addActionListener(e -> {
            long now = System.currentTimeMillis();
            if (now < lockoutUntil) {
                long waitSeconds = Math.max(1, (lockoutUntil - now + 999) / 1000);
                infoLabel.setText("Too many failed attempts. Try again in " + waitSeconds + " seconds.");
                return;
            }

            String studentId = rollField.getText();
            char[] pass = passField.getPassword();
            studentfunctions student = new studentfunctions(studentId, pass);
            Arrays.fill(pass, '\0');
            passField.setText("");

            if (student.valid()) {
                LOGGER.log(Level.INFO, "Student login success for id={0}", student.getId());
                failedAttempts = 0;
                lockoutUntil = 0L;
                infoLabel.setText(" ");
                dispose();
                new StudentDashboardFrame(student);
            } else {
                failedAttempts++;
                LOGGER.log(Level.WARNING, "Student login failure count={0} for id={1}", new Object[]{failedAttempts, studentId});
                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    lockoutUntil = System.currentTimeMillis() + LOCKOUT_MILLIS;
                    failedAttempts = 0;
                    infoLabel.setText("Too many failed attempts. Login is locked for 30 seconds.");
                } else {
                    infoLabel.setText("Invalid Student Credentials");
                }
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        getRootPane().setDefaultButton(loginBtn);
        root.add(left, BorderLayout.WEST);
        root.add(card, BorderLayout.CENTER);
        add(root, BorderLayout.CENTER);
        setVisible(true);
    }

    private void styleInput(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(0, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 214, 232)),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private void stylePrimaryButton(JButton button) {
        button.setPreferredSize(new Dimension(120, 42));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setFocusPainted(false);
        button.setBackground(new Color(32, 115, 214));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(51, 119, 198));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
