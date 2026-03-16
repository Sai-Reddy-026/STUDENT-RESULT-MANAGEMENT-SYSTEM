import javax.swing.table.DefaultTableModel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class studentfunctions extends DBConnection {
    private static final Logger LOGGER = Logger.getLogger(studentfunctions.class.getName());
    private boolean status = false;
    private final String id;
    private final char[] password;
    private String studentName;
    private String studentDepartment;

    studentfunctions(String id, char[] password) {
        this.id = id == null ? "" : id.trim();
        this.password = password == null ? new char[0] : password;
        this.studentName = this.id;
        this.studentDepartment = "N/A";
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

    String getStudentDepartment() {
        return studentDepartment;
    }

    DefaultTableModel getResultModel() {
        Integer latestSemester = getLatestSemester();
        return getResultModel(latestSemester);
    }

    DefaultTableModel getResultModel(Integer semester) {
        try (Connection con = DBConnection.getDbConnection()) {
            String courseNameColumn = resolveFirstExistingColumn(con, "course", new String[]{"course_name", "name"});
            String resolvedCourseName = courseNameColumn == null ? "course_name" : courseNameColumn;
            String gradeColumn = resolveFirstExistingColumn(con, "result", new String[]{"grade"});
            String gradeExpression = gradeColumn != null
                    ? "COALESCE(r." + gradeColumn + ", " + gradeCaseExpression("r.marks") + ")"
                    : gradeCaseExpression("r.marks");

            String sql =
                    "Select c." + resolvedCourseName + " as course_name, r.marks, " + gradeExpression + " as grade " +
                    "from result r " +
                    "join course c on r.course_id = c.course_id " +
                    "where r.student_id=? " +
                    (semester != null ? "and c.semester=? " : "") +
                    "order by c." + resolvedCourseName;

            try (PreparedStatement pt = con.prepareStatement(sql)) {
                pt.setString(1, id);
                if (semester != null) {
                    pt.setInt(2, semester);
                }
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

    Integer getLatestSemester() {
        try (Connection con = DBConnection.getDbConnection();
             PreparedStatement pt = con.prepareStatement(
                     "SELECT MAX(c.semester) AS latest_sem " +
                             "FROM result r " +
                             "JOIN course c ON r.course_id = c.course_id " +
                             "WHERE r.student_id=?")) {
            pt.setString(1, id);
            try (ResultSet res = pt.executeQuery()) {
                if (res.next()) {
                    int sem = res.getInt("latest_sem");
                    if (!res.wasNull()) {
                        return sem;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load latest semester for student id=" + id, e);
        }
        return null;
    }

    List<Integer> getAvailableSemesters() {
        List<Integer> semesters = new ArrayList<>();
        try (Connection con = DBConnection.getDbConnection();
             PreparedStatement pt = con.prepareStatement(
                     "SELECT DISTINCT c.semester " +
                             "FROM result r " +
                             "JOIN course c ON r.course_id = c.course_id " +
                             "WHERE r.student_id=? AND c.semester IS NOT NULL " +
                             "ORDER BY c.semester DESC")) {
            pt.setString(1, id);
            try (ResultSet res = pt.executeQuery()) {
                while (res.next()) {
                    semesters.add(res.getInt("semester"));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load semester list for student id=" + id, e);
        }
        return semesters;
    }

    private String gradeCaseExpression(String marksExpr) {
        return "CASE " +
                "WHEN " + marksExpr + " IS NULL THEN NULL " +
                "WHEN " + marksExpr + " >= 90 THEN 'A+' " +
                "WHEN " + marksExpr + " >= 80 THEN 'A' " +
                "WHEN " + marksExpr + " >= 70 THEN 'B' " +
                "WHEN " + marksExpr + " >= 60 THEN 'C' " +
                "WHEN " + marksExpr + " >= 50 THEN 'D' " +
                "WHEN " + marksExpr + " >= 40 THEN 'E' " +
                "ELSE 'F' END";
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
                    loadStudentDepartment(res);
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

    private void loadStudentDepartment(ResultSet res) {
        try {
            String dept = null;
            try {
                dept = res.getString("department");
            } catch (Exception ignored) {
                try {
                    dept = res.getString("dept");
                } catch (Exception ignoredAgain) {
                    // no-op
                }
            }
            if (dept != null && !dept.trim().isEmpty()) {
                studentDepartment = dept.trim();
            }
        } catch (Exception ignored) {
            // Keep fallback.
        }
    }
}
