import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ForgotPasswordDialog extends JDialog {
    private final String userType; // "teacher" or "student"
    private final JFrame parentFrame;

    public ForgotPasswordDialog(JFrame parent, String userType) {
        super(parent, "Forgot Password", true);
        this.parentFrame = parent;
        this.userType = userType;

        setSize(450, 350);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Reset Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(34, 50, 84));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your " + userType + " ID to receive OTP");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(102, 117, 145));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel idLabel = new JLabel(userType.substring(0, 1).toUpperCase() + userType.substring(1) + " ID");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        idLabel.setForeground(new Color(67, 83, 117));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField idField = new JTextField();
        styleInput(idField);

        JButton sendOTPBtn = new JButton("Send OTP");
        stylePrimaryButton(sendOTPBtn);

        JButton cancelBtn = new JButton("Cancel");
        styleSecondaryButton(cancelBtn);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(sendOTPBtn);
        buttonPanel.add(cancelBtn);

        panel.add(idLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(idField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        sendOTPBtn.addActionListener(e -> {
            String userId = idField.getText().trim();
            if (userId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your " + userType + " ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if user exists and get email
            String email = getUserEmail(userId);
            if (email == null) {
                JOptionPane.showMessageDialog(this, "No " + userType + " found with this ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate and store OTP
            String otp = OTPManager.generateOTP();
            OTPManager.storeOTP(userId, otp, email);

            // Show OTP sent message
            String message = userType.equals("admin") ?
                "OTP generated for admin account. Please check with system administrator for the code." :
                "OTP sent to your email: " + maskEmail(email) + "\n\nPlease check your email for the 6-digit code.";

            JOptionPane.showMessageDialog(this, message,
                "OTP Generated",
                JOptionPane.INFORMATION_MESSAGE);

            // Open OTP verification dialog
            dispose();
            new OTPVerificationDialog(parentFrame, userType, userId);
        });

        cancelBtn.addActionListener(e -> dispose());

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private String getUserEmail(String userId) {
        if (userType.equals("admin")) {
            // Admin table doesn't have email column, return a placeholder
            return "admin@system.local";
        }

        String tableName = userType.equals("teacher") ? "teacher" : "student";
        String query = "SELECT email FROM " + tableName + " WHERE " + tableName + "_id = ?";

        try (Connection con = DBConnection.getDbConnection();
             PreparedStatement pt = con.prepareStatement(query)) {
            pt.setString(1, userId);
            ResultSet rs = pt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String maskEmail(String email) {
        if (email == null || email.equals("admin@system.local")) {
            return "system administrator";
        }
        if (email.length() < 5) return email;
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) return email;

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domainPart;
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
        button.setPreferredSize(new Dimension(100, 36));
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