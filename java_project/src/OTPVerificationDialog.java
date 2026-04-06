import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class OTPVerificationDialog extends JDialog {
    private final String userType; // "teacher" or "student"
    private final String userId;
    private final JFrame parentFrame;

    public OTPVerificationDialog(JFrame parent, String userType, String userId) {
        super(parent, "Verify OTP", true);
        this.parentFrame = parent;
        this.userType = userType;
        this.userId = userId;

        setSize(450, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Enter OTP");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(34, 50, 84));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter the 6-digit code sent to your email");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(102, 117, 145));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel otpLabel = new JLabel("OTP Code");
        otpLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        otpLabel.setForeground(new Color(67, 83, 117));
        otpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField otpField = new JTextField();
        styleInput(otpField);

        JLabel newPassLabel = new JLabel("New Password");
        newPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        newPassLabel.setForeground(new Color(67, 83, 117));
        newPassLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField newPassField = new JPasswordField();
        styleInput(newPassField);

        JLabel confirmPassLabel = new JLabel("Confirm Password");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmPassLabel.setForeground(new Color(67, 83, 117));
        confirmPassLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField confirmPassField = new JPasswordField();
        styleInput(confirmPassField);

        JButton resetBtn = new JButton("Reset Password");
        stylePrimaryButton(resetBtn);

        JButton cancelBtn = new JButton("Cancel");
        styleSecondaryButton(cancelBtn);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(resetBtn);
        buttonPanel.add(cancelBtn);

        panel.add(otpLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(otpField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(newPassLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(newPassField);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(confirmPassLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(confirmPassField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        resetBtn.addActionListener(e -> {
            String otp = otpField.getText().trim();
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (otp.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verify OTP
            if (!OTPManager.verifyOTP(userId, otp)) {
                JOptionPane.showMessageDialog(this, "Invalid or expired OTP", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update password
            if (updatePassword(userId, newPass)) {
                JOptionPane.showMessageDialog(this, "Password reset successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private boolean updatePassword(String userId, String newPassword) {
        String tableName;
        String idColumn;
        String passwordColumn;

        if (userType.equals("admin")) {
            tableName = "admin";
            idColumn = "admin_id";
            passwordColumn = "password";
        } else {
            tableName = userType.equals("teacher") ? "teacher" : "student";
            idColumn = tableName + "_id";
            passwordColumn = "password";
        }

        String query = "UPDATE " + tableName + " SET " + passwordColumn + " = ? WHERE " + idColumn + " = ?";

        try (Connection con = DBConnection.getDbConnection();
             PreparedStatement pt = con.prepareStatement(query)) {
            pt.setString(1, newPassword);
            pt.setString(2, userId);
            int rowsAffected = pt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
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
        button.setPreferredSize(new Dimension(120, 36));
        button.setFocusPainted(false);
        button.setBackground(userType.equals("teacher") ? new Color(29, 130, 99) : new Color(32, 115, 214));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setPreferredSize(new Dimension(100, 36));
        button.setFocusPainted(false);
        button.setBackground(new Color(108, 117, 125));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}