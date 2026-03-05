import javax.swing.table.DefaultTableModel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class studentfunctions extends DBConnection {
    private static final Logger LOGGER = Logger.getLogger(studentfunctions.class.getName());
    private boolean status = false;
    private final String id;
    private final char[] password;
    private String studentName;

    studentfunctions(String id, char[] password) {
        this.id = id == null ? "" : id.trim();
        this.password = password == null ? new char[0] : password;
        this.studentName = this.id;
        verifyCredentials();
    }

    boolean valid() {
        return status;
    }

    String getId() {
        return id;
    }

    String getStudentName() {
        return studentName;
    }

    DefaultTableModel getResultModel() {
        try (Connection con = DBConnection.getDbConnection()) {
            String courseNameColumn = resolveFirstExistingColumn(con, "course", new String[]{"course_name", "name"});
            String resolvedCourseName = courseNameColumn == null ? "course_name" : courseNameColumn;

            String sql =
                    "Select c." + resolvedCourseName + " as course_name, r.marks, r.grade " +
                    "from result r " +
                    "join course c on r.course_id = c.course_id " +
                    "where r.student_id=? " +
                    "order by c." + resolvedCourseName;

            try (PreparedStatement pt = con.prepareStatement(sql)) {
                pt.setString(1, id);
                try (ResultSet res = pt.executeQuery()) {
                    DefaultTableModel model = new DefaultTableModel(
                            new String[]{"Course", "Marks", "Grade"}, 0
                    );
                    while (res.next()) {
                        model.addRow(new Object[]{
                                res.getString("course_name"),
                                res.getObject("marks"),
                                res.getObject("grade")
                        });
                    }
                    return model;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load result model for student id=" + id, e);
            return new DefaultTableModel(new String[]{"Course", "Marks", "Grade"}, 0);
        }
    }

    private void verifyCredentials() {
        if (id.isBlank() || password.length == 0) {
            status = false;
            return;
        }

        try (Connection con = DBConnection.getDbConnection();
             PreparedStatement pt = con.prepareStatement("select * from student where student_id=?")) {
            pt.setString(1, id);
            try (ResultSet res = pt.executeQuery()) {
                if (!res.next()) {
                    status = false;
                    return;
                }
                String storedPassword = res.getString("password");
                status = matchesPassword(password, storedPassword);
                if (status) {
                    loadStudentName(res);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Student verification failed for id=" + id, e);
            status = false;
        }
    }

    private boolean matchesPassword(char[] rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        String input = new String(rawPassword);
        if (storedPassword.matches("^[0-9a-fA-F]{64}$")) {
            return storedPassword.equalsIgnoreCase(sha256Hex(input));
        }
        return storedPassword.equals(input);
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format(Locale.ROOT, "%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String resolveFirstExistingColumn(Connection con, String table, String[] candidates) {
        try {
            DatabaseMetaData meta = con.getMetaData();
            for (String candidate : candidates) {
                try (ResultSet cols = meta.getColumns(con.getCatalog(), null, table, candidate)) {
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

    private void loadStudentName(ResultSet res) {
        try {
            String name = null;
            try {
                name = res.getString("student_name");
            } catch (Exception ignored) {
                try {
                    name = res.getString("name");
                } catch (Exception ignoredAgain) {
                    // no-op
                }
            }
            if (name != null && !name.trim().isEmpty()) {
                studentName = name.trim();
            }
        } catch (Exception ignored) {
            // Keep fallback to ID.
        }
    }
}
