import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;



public class teacherverify extends DBConnection {
    private static final Logger LOGGER = Logger.getLogger(teacherverify.class.getName());
    private boolean status=false;
    private final String id;
    private final char[] password;
    private String teacherName;
    private String teacherDepartment;

    teacherverify(String id,char[] password) {
        this.id = id == null ? "" : id.trim();
        this.password = password;
        this.teacherName = this.id;
        this.teacherDepartment = "N/A";
        try (Connection con = getDbConnection();
             PreparedStatement pt = con.prepareStatement("select password from teacher where teacher_id=?")) {
            pt.setString(1, this.id);
            try (ResultSet res = pt.executeQuery()) {
                if (!res.next()) {
                    status = false;
                    return;
                }
                String storedPassword = res.getString(1);
                status = matchesPassword(this.password, storedPassword);
                if (status) {
                    loadTeacherProfile(con);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Teacher verification failed for id=" + this.id, e);
            status = false;
        }
    }

    public  boolean valid()
    {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTeacherDepartment() {
        return teacherDepartment;
    }

    public void insertdata(String roll, String course, String marks) {
        try (Connection con = getDbConnection()) {
            boolean hasInsertedAt = hasResultColumn(con, "inserted_at");
            String sql = hasInsertedAt
                    ? "INSERT INTO result (student_id,course_id,marks,awardedby,inserted_at) VALUES (?,?,?,?,NOW())"
                    : "INSERT INTO result (student_id,course_id,marks,awardedby) VALUES (?,?,?,?)";

            try (PreparedStatement pt = con.prepareStatement(sql)) {
                pt.setString(1, roll);
                pt.setString(2, course);
                pt.setString(3, marks);
                pt.setString(4, this.id);
                pt.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String fetchMarks(String roll, String course) {
        try (Connection con = getDbConnection()) {
            boolean hasInsertedAt = hasResultColumn(con, "inserted_at");
            String sql = hasInsertedAt
                    ? "SELECT marks FROM result WHERE student_id=? AND course_id=? AND awardedby=? ORDER BY inserted_at DESC LIMIT 1"
                    : "SELECT marks FROM result WHERE student_id=? AND course_id=? AND awardedby=? LIMIT 1";

            try (PreparedStatement pt = con.prepareStatement(sql)) {
                pt.setString(1, roll);
                pt.setString(2, course);
                pt.setString(3, this.id);
                try (ResultSet rs = pt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(1);
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateMarks(String roll, String course, String marks) {
        try (Connection con = getDbConnection()) {
            boolean hasInsertedAt = hasResultColumn(con, "inserted_at");
            String sql = hasInsertedAt
                    ? "UPDATE result SET marks=? WHERE student_id=? AND course_id=? AND awardedby=? ORDER BY inserted_at DESC LIMIT 1"
                    : "UPDATE result SET marks=? WHERE student_id=? AND course_id=? AND awardedby=? LIMIT 1";

            try (PreparedStatement pt = con.prepareStatement(sql)) {
                pt.setString(1, marks);
                pt.setString(2, roll);
                pt.setString(3, course);
                pt.setString(4, this.id);
                return pt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean matchesPassword(char[] rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        String input = new String(rawPassword);

        // Supports both plaintext and SHA-256 (hex) for migration compatibility.
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

    private boolean hasResultColumn(Connection con, String column) {
        try {
            DatabaseMetaData meta = con.getMetaData();
            try (ResultSet cols = meta.getColumns(con.getCatalog(), null, "result", column)) {
                return cols.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void loadTeacherProfile(Connection con) {
        String nameColumn = resolveFirstExistingColumn(con, "teacher", new String[]{"teacher_name", "name"});
        String departmentColumn = resolveFirstExistingColumn(con, "teacher", new String[]{"department", "dept"});
        if (nameColumn == null && departmentColumn == null) {
            return;
        }
        String selectName = nameColumn == null ? "NULL as teacher_name" : nameColumn + " as teacher_name";
        String selectDept = departmentColumn == null ? "NULL as teacher_department" : departmentColumn + " as teacher_department";
        String sql = "select " + selectName + ", " + selectDept + " from teacher where teacher_id=?";
        try (PreparedStatement pt = con.prepareStatement(sql)) {
            pt.setString(1, this.id);
            try (ResultSet rs = pt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("teacher_name");
                    if (name != null && !name.trim().isEmpty()) {
                        teacherName = name.trim();
                    }
                    String dept = rs.getString("teacher_department");
                    if (dept != null && !dept.trim().isEmpty()) {
                        teacherDepartment = dept.trim();
                    }
                }
            }
        } catch (Exception ignored) {
            // Keep fallback values if profile lookup fails.
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
}
