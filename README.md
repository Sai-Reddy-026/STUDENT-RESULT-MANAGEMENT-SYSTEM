# Student Result Management System

A Java Swing desktop application for managing and viewing academic results with separate portals for Admin, Teacher, and Student users. The project uses MySQL for persistence and includes SQL scripts for both full schema setup and safe sample-data seeding.

## Features

- Role-based entry screen for Admin, Teacher, and Student login
- Admin dashboard to add teacher records, add student records, and review result data
- Teacher dashboard to insert new marks and update existing marks
- Student dashboard to view subject-wise results, semester-wise filtering, pass/fail summaries, and search
- MySQL-backed data storage with configurable connection settings through environment variables

## Project Structure

```text
.
|-- database/
|   |-- schema_with_sample_data.sql
|   |-- safe_semester_seed.sql
|   `-- safe_additional_subjects_3001.sql
|-- java_project/
|   |-- src/
|   |   |-- index.java
|   |   |-- LoginFrame.java
|   |   |-- AdminLoginFrame.java
|   |   |-- AdminFrame.java
|   |   |-- Adminfunction.java
|   |   |-- TeacherLoginFrame.java
|   |   |-- TeacherFrame.java
|   |   |-- teacherverify.java
|   |   |-- StudentLoginFrame.java
|   |   |-- StudentDashboardFrame.java
|   |   |-- studentfunctions.java
|   |   `-- DBConnection.java
|   `-- README.md
`-- README.md
```

## Tech Stack

- Java Swing
- JDBC
- MySQL

## Requirements

- JDK 8 or later
- MySQL Server running locally or on a reachable host
- MySQL Connector/J available at runtime

## Database Setup

The application connects to the `result_management` database.

### 1. Make sure MySQL is installed and running

Verify that the MySQL client is available:

```powershell
mysql --version
```

Then start your MySQL server service if it is not already running.

### 2. Log in to MySQL

Open a MySQL session with a user that can create databases and tables:

```powershell
mysql -u root -p
```

If your local setup uses the password shown in this project, you can also use:

```powershell
mysql -u root -proot
```

### 3. Create and load the database

You have two supported ways to set up the project database.

#### Option 1: Full setup with starter data

This creates the database, tables, and a small set of sample records.

```powershell
mysql -u root -proot < .\database\schema_with_sample_data.sql
```

This script will:

- Create the `result_management` database if it does not exist
- Create the `admin`, `teacher`, `student`, `course`, and `result` tables
- Insert starter users, courses, and marks

#### Option 2: Add larger semester-based sample data safely

These scripts do not delete existing records and can be rerun safely because they use `INSERT IGNORE`.

```powershell
mysql -u root -proot -D result_management -e "source .\database\safe_semester_seed.sql"
mysql -u root -proot -D result_management -e "source .\database\safe_additional_subjects_3001.sql"
```

Use these after the main schema script if you want richer semester-wise demo data.

### 4. Verify the database setup

Check that the database exists:

```powershell
mysql -u root -proot -e "SHOW DATABASES;"
```

Check that the project tables were created:

```powershell
mysql -u root -proot -D result_management -e "SHOW TABLES;"
```

You should see tables such as:

- `admin`
- `teacher`
- `student`
- `course`
- `result`

### 5. Optional: verify sample records

You can confirm that sample users were inserted:

```powershell
mysql -u root -proot -D result_management -e "SELECT * FROM admin;"
mysql -u root -proot -D result_management -e "SELECT student_id, student_name FROM student;"
mysql -u root -proot -D result_management -e "SELECT teacher_id, teacher_name FROM teacher;"
```

## Database Configuration

`java_project/src/DBConnection.java` reads connection settings from environment variables and falls back to defaults if they are not set:

- `DB_URL` -> default `jdbc:mysql://localhost:3306/result_management`
- `DB_USER` -> default `root`
- `DB_PASSWORD` -> default `root`

Example PowerShell session:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/result_management"
$env:DB_USER="root"
$env:DB_PASSWORD="root"
```

## Build

From the source directory:

```powershell
Set-Location .\java_project\src
javac *.java
```

## Run

After compiling, start the application from `java_project/src`:

```powershell
java index
```

You can also launch the role selector directly:

```powershell
java LoginFrame
```

## Sample Login Credentials

The main schema script inserts these starter accounts:

### Admin

- ID: `1`
- Password: `admin123`

### Teachers

- ID: `101` | Password: `teach101`
- ID: `102` | Password: `teach102`
- ID: `103` | Password: `teach103`

### Students

- ID: `1001` | Password: `stud1001`
- ID: `1002` | Password: `stud1002`
- ID: `1003` | Password: `stud1003`

Additional seeded user from `safe_semester_seed.sql`:

- Student ID: `3001` | Password: `stud3001`

## Main Application Flow

1. Launch `index` or `LoginFrame`
2. Choose a role from the welcome screen
3. Log in using the corresponding ID and password
4. Open the role-specific dashboard

## Notes

- The login screens include failed-attempt lockouts for basic protection.
- The teacher and student dashboards depend on database records being present.
- If MySQL is running with different credentials or on a different port, update the environment variables before launching the app.
- If your MySQL password is not `root`, replace `-proot` in the commands above with your actual password or use `-p`.
- Keep generated `.class` files out of source control.
