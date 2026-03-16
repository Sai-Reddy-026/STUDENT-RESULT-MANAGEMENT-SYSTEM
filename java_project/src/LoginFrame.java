import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame  {
    JFrame frame = new JFrame();


    public LoginFrame() {
        frame.setTitle("Student Result System - Login");
        frame.setSize(900, 540);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(0, 24));
        root.setBorder(new EmptyBorder(26, 30, 26, 30));
        root.setBackground(new Color(245, 248, 255));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Welcome Back");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(new Color(31, 42, 68));

        JLabel subtitle = new JLabel("Choose your role and continue to your dedicated login screen");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(98, 112, 140));

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 8)));
        header.add(subtitle);

        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setOpaque(false);

        cards.add(createRoleCard(
                "Admin",
                "Manage teachers, students, and results with full control.",
                "Open Admin Login",
                new Color(232, 242, 255),
                new Color(24, 103, 197),
                () -> {
                    frame.dispose();
                    new AdminLoginFrame();
                }
        ));

        cards.add(createRoleCard(
                "Teacher",
                "Access marks entry and class-level academic tools quickly.",
                "Open Teacher Login",
                new Color(235, 247, 240),
                new Color(34, 139, 87),
                () -> {
                    frame.dispose();
                    new TeacherLoginFrame();
                }
        ));

        cards.add(createRoleCard(
                "Student",
                "Check your dashboard, marks, and academic progress securely.",
                "Open Student Login",
                new Color(255, 243, 233),
                new Color(203, 120, 36),
                () -> {
                    frame.dispose();
                    new StudentLoginFrame();
                }
        ));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setOpaque(false);
        JLabel helper = new JLabel("Tip: Each role has a separate login box for faster access.");
        helper.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        helper.setForeground(new Color(109, 121, 146));
        footer.add(helper);

        root.add(header, BorderLayout.NORTH);
        root.add(cards, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        frame.add(root, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createRoleCard(String role, String description, String buttonText, Color accentSoft, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setBackground(Color.WHITE);

        JPanel roleBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        roleBadge.setBackground(accentSoft);
        roleBadge.setBorder(new EmptyBorder(2, 8, 2, 8));

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setForeground(accent);
        roleBadge.add(roleLabel);

        JLabel heading = new JLabel(role + " Portal");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(31, 42, 68));

        JTextArea desc = new JTextArea(description);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setFocusable(false);
        desc.setOpaque(false);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(new Color(89, 102, 130));

        JButton openBtn = new JButton(buttonText);
        openBtn.setFocusPainted(false);
        openBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        openBtn.setBackground(accent);
        openBtn.setForeground(Color.WHITE);
        openBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        openBtn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        openBtn.addActionListener(e -> action.run());

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        roleBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(roleBadge);
        top.add(Box.createRigidArea(new Dimension(0, 12)));
        top.add(heading);
        top.add(Box.createRigidArea(new Dimension(0, 10)));
        top.add(desc);

        card.add(top, BorderLayout.CENTER);
        card.add(openBtn, BorderLayout.SOUTH);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
