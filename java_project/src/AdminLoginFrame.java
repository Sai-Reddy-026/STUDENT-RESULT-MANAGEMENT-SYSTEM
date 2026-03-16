import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminLoginFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(AdminLoginFrame.class.getName());
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_MILLIS = 30_000;
    private static final int FAIL_COOLDOWN_MILLIS = 1200;

    private int failedAttempts = 0;
    private long lockoutUntil = 0L;
    private JLabel infoLabel;
    private JButton loginBtn;

    public AdminLoginFrame() {
        setTitle("Admin Login");
        setSize(780, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(22, 0));
        root.setBackground(new Color(244, 247, 255));
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(26, 71, 148));
        leftPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(290, 0));

        JLabel panelTitle = new JLabel("Admin Portal");
        panelTitle.setForeground(Color.WHITE);
        panelTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel panelSub = new JLabel("<html>Secure access to dashboards,<br/>teacher/student management,<br/>and system controls.</html>");
        panelSub.setForeground(new Color(220, 230, 250));
        panelSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(panelTitle);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        leftPanel.add(panelSub);
        leftPanel.add(Box.createVerticalGlue());

        JButton backBtn = new JButton("Back to Role Selection");
        styleGhostButton(backBtn);
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(backBtn);

        JPanel loginCard = new JPanel();
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(new EmptyBorder(30, 32, 30, 32));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(34, 48, 78));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your credentials to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(102, 116, 146));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("Admin ID");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(new Color(76, 92, 126));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userField = new JTextField();
        styleInput(userField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setForeground(new Color(76, 92, 126));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passField = new JPasswordField();
        styleInput(passField);

        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPassword.setForeground(new Color(102, 116, 146));
        showPassword.setOpaque(false);
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        char defaultEcho = passField.getEchoChar();
        showPassword.addActionListener(e -> passField.setEchoChar(showPassword.isSelected() ? (char) 0 : defaultEcho));

        infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(190, 73, 73));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginBtn = new JButton("Login");
        stylePrimaryButton(loginBtn);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginCard.add(title);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(subtitle);
        loginCard.add(Box.createRigidArea(new Dimension(0, 24)));
        loginCard.add(userLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(userField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 14)));
        loginCard.add(passLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(passField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));
        loginCard.add(showPassword);
        loginCard.add(Box.createRigidArea(new Dimension(0, 12)));
        loginCard.add(infoLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(loginBtn);

        loginBtn.addActionListener(e -> {
            long now = System.currentTimeMillis();
            if (now < lockoutUntil) {
                long waitSeconds = Math.max(1, (lockoutUntil - now + 999) / 1000);
                infoLabel.setText("Too many failed attempts. Try again in " + waitSeconds + " seconds.");
                return;
            }

            String id = userField.getText();
            char[] pass = passField.getPassword();
            Adminfunction admin = new Adminfunction(id, pass);
            boolean valid = admin.verify();
            Arrays.fill(pass, '\0');
            passField.setText("");

            if (valid) {
                LOGGER.log(Level.INFO, "Admin login success for id={0}", admin.getId());
                failedAttempts = 0;
                lockoutUntil = 0L;
                infoLabel.setText(" ");
                dispose();
                new AdminFrame(admin);
                return;
            }

            failedAttempts++;
            LOGGER.log(Level.WARNING, "Admin login failure count={0} for id={1}", new Object[]{failedAttempts, id});
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                lockoutUntil = System.currentTimeMillis() + LOCKOUT_MILLIS;
                failedAttempts = 0;
                LOGGER.log(Level.WARNING, "Admin login temporarily locked for id={0}", id);
                infoLabel.setText("Too many failed attempts. Login is locked for 30 seconds.");
            } else {
                loginBtn.setEnabled(false);
                Timer timer = new Timer(FAIL_COOLDOWN_MILLIS, evt -> loginBtn.setEnabled(true));
                timer.setRepeats(false);
                timer.start();
                int remaining = MAX_FAILED_ATTEMPTS - failedAttempts;
                infoLabel.setText("Invalid credentials. Attempts left: " + remaining);
            }
        });

        getRootPane().setDefaultButton(loginBtn);

        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        root.add(leftPanel, BorderLayout.WEST);
        root.add(loginCard, BorderLayout.CENTER);
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
        button.setBackground(new Color(31, 105, 204));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleGhostButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(38, 88, 168));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
