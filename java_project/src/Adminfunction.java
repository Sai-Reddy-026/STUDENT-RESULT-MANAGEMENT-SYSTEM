import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Adminfunction {
    private static final Logger LOGGER = Logger.getLogger(Adminfunction.class.getName());
    private final String id;
    private final char[] password;
    private String displayName;
    private String adminDepartment;

    Adminfunction(String id, char[] password) {
        this.id = id == null ? "" : id.trim();
        this.password = password == null ? new char[0] : password;
        this.displayName = this.id.isBlank() ? "Administrator" : ("Admin " + this.id);
        this.adminDepartment = "N/A";
    }

    boolean verify() {
        if (id.isBlank() || password.length == 0) {
            LOGGER.log(Level.WARNING, "Admin login rejected due to empty credentials for id={0}", id);
            return false;
        }

        final String sql = "select password from admin where admin_id=?";
        try (Connection con = DBConnection.getDbConnection();
             PreparedStatement pt = con.prepareStatement(sql)) {
            pt.setString(1, id);
            try (ResultSet res = pt.executeQuery()) {
                if (!res.next()) {
                    LOGGER.log(Level.WARNING, "Admin login failed. Unknown admin id={0}", id);
                    return false;
                }

                String storedPassword = res.getString(1);
                boolean ok = matchesPassword(password, storedPassword);
                if (!ok) {
                    LOGGER.log(Level.WARNING, "Admin login failed. Invalid password for id={0}", id);
                } else {
                    loadProfile();
                }
                return ok;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Admin login verification failed for id=" + id, e);
            return false;
        }
    }

    String getId() {
        return id;
    }

    String getDisplayName() {
        return displayName;
    }

    String getDepartment() {
        return adminDepartment;
    }

    private boolean matchesPassword(char[] rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }

        String input = new String(rawPassword);

        // Support both plaintext and SHA-256 (hex) values during migration.
        if (isSha256Hex(storedPassword)) {
            return storedPassword.equalsIgnoreCase(sha256Hex(input));
        }
        return storedPassword.equals(input);
    }

    private boolean isSha256Hex(String value) {
        if (value.length() != 64) {
            return false;
        }
        return value.matches("^[0-9a-fA-F]{64}$");
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

    private void loadProfile() {
        try (Connection con = DBConnection.getDbConnection()) {
            String nameColumn = resolveFirstExistingAdminNameColumn(con);
            String departmentColumn = resolveFirstExistingColumn(con, "admin", new String[]{"department", "dept"});
            if (nameColumn == null && departmentColumn == null) {
                return;
            }
            String selectName = nameColumn == null ? "NULL as admin_name" : nameColumn + " as admin_name";
            String selectDept = departmentColumn == null ? "NULL as admin_department" : departmentColumn + " as admin_department";
            String sql = "select " + selectName + ", " + selectDept + " from admin where admin_id=?";
            try (PreparedStatement pt = con.prepareStatement(sql)) {
                pt.setString(1, id);
                try (ResultSet rs = pt.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("admin_name");
                        if (name != null && !name.trim().isEmpty()) {
                            displayName = name.trim();
                        }
                        String department = rs.getString("admin_department");
                        if (department != null && !department.trim().isEmpty()) {
                            adminDepartment = department.trim();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // keep fallback profile values
        }
    }

    private String resolveFirstExistingAdminNameColumn(Connection con) {
        String[] candidates = {"admin_name", "name", "full_name"};
        return resolveFirstExistingColumn(con, "admin", candidates);
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
