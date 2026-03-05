# Student Result Management System (Java Swing)

Desktop application for managing and viewing student academic results with separate access for Admin, Teacher, and Student roles.

## Project Structure

- `src/`: Java source files
- `lib/`: optional external libraries (if needed)
- `.idea/`, `.vscode/`: IDE/editor settings

## Main Modules

- `LoginFrame`: role selection screen
- `AdminLoginFrame` -> `AdminFrame` / `Adminfunction`
- `TeacherLoginFrame` -> `TeacherFrame` / `teacherverify`
- `StudentLoginFrame` -> `StudentDashboardFrame` / `studentfunctions`
- `DBConnection`: MySQL database connection helper

## Requirements

- Java JDK 8+
- MySQL Server
- Database: `result_management`

## Database Configuration

`DBConnection` reads values from environment variables and falls back to defaults:

- `DB_URL` (default: `jdbc:mysql://localhost:3306/result_management`)
- `DB_USER` (default: `root`)
- `DB_PASSWORD` (default: `root`)

Set these before running if your setup differs.

## Build

From `java_project/src`:

```powershell
javac *.java
```

## Run

```powershell
java index
```

You can also start directly with:

```powershell
java LoginFrame
```

## Notes

- Keep generated `*.class` files out of source control.
- Ensure MySQL is running before logging into role dashboards that access data.
