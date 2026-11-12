import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Assignment 3, Question 1: PostgreSQL student CRUD application.
 * Uses JDBC prepared statements for safe, parameterized SQL operations.
 */
public class StudentApp {
    // Settings can be supplied through environment variables instead of hardcoding credentials.
    private static final String DB_URL = envOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/students_db");
    private static final String DB_USER = envOrDefault("DB_USER", "postgres");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    // Opens a database connection for an operation; try-with-resources closes it afterward.
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /** Retrieves and displays all students, sorted by student ID. */
    public static void getAllStudents() throws SQLException {
        String sql = "SELECT student_id, first_name, last_name, email, enrollment_date "
                + "FROM students ORDER BY student_id";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            System.out.printf("%-5s %-16s %-16s %-32s %-12s%n",
                    "ID", "First name", "Last name", "Email", "Enrolled");
            System.out.println("-".repeat(87));
            boolean found = false;
            while (results.next()) {
                found = true;
                Date date = results.getDate("enrollment_date");
                System.out.printf("%-5d %-16s %-16s %-32s %-12s%n",
                        results.getInt("student_id"),
                        results.getString("first_name"),
                        results.getString("last_name"),
                        results.getString("email"),
                        date == null ? "NULL" : date.toString());
            }
            if (!found) {
                System.out.println("No students found.");
            }
        }
    }

    /** Adds a student and displays the automatically generated primary key. */
    public static void addStudent(String firstName, String lastName,
                                  String email, LocalDate enrollmentDate) throws SQLException {
        String sql = "INSERT INTO students (first_name, last_name, email, enrollment_date) "
                + "VALUES (?, ?, ?, ?) RETURNING student_id";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            if (enrollmentDate == null) {
                statement.setNull(4, java.sql.Types.DATE);
            } else {
                statement.setDate(4, Date.valueOf(enrollmentDate));
            }
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    System.out.println("Student added with ID: " + result.getInt("student_id"));
                }
            }
        }
    }

    /** Updates the email of a student identified by student_id. */
    public static void updateStudentEmail(int studentId, String newEmail) throws SQLException {
        String sql = "UPDATE students SET email = ? WHERE student_id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newEmail);
            statement.setInt(2, studentId);
            int affected = statement.executeUpdate();
            System.out.println(affected == 0 ? "No student found with that ID."
                    : "Student email updated successfully.");
        }
    }

    /** Deletes the student identified by student_id. */
    public static void deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            int affected = statement.executeUpdate();
            System.out.println(affected == 0 ? "No student found with that ID."
                    : "Student deleted successfully.");
        }
    }

    // Reads input as text so invalid numbers/dates can be handled without crashing the menu.
    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int number = Integer.parseInt(scanner.nextLine().trim());
                if (number > 0) return number;
            } catch (NumberFormatException ignored) {
                // Ask again when the input is not a valid integer.
            }
            System.out.println("Please enter a positive whole number.");
        }
    }

    private static String readRequiredText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field is required.");
        }
    }

    private static LocalDate readOptionalDate(Scanner scanner) {
        while (true) {
            System.out.print("Enrollment date (YYYY-MM-DD, or blank for NULL): ");
            String value = scanner.nextLine().trim();
            if (value.isEmpty()) return null;
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException ignored) {
                System.out.println("Invalid date. Use YYYY-MM-DD.");
            }
        }
    }

    /** Presents a menu to demonstrate each of the four required functions. */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("PostgreSQL Student Management (Assignment 3 Q1)");
            // Check the connection first to give users a useful startup error.
            try (Connection ignored = connect()) {
                System.out.println("Connected to PostgreSQL successfully.");
            } catch (SQLException e) {
                System.err.println("Could not connect to PostgreSQL: " + e.getMessage());
                System.err.println("Check DB_URL, DB_USER, DB_PASSWORD, and the database setup.");
                return;
            }

            while (true) {
                System.out.println("\n1. Get all students");
                System.out.println("2. Add student");
                System.out.println("3. Update student email");
                System.out.println("4. Delete student");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");
                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> getAllStudents();
                        case "2" -> {
                            String firstName = readRequiredText(scanner, "First name: ");
                            String lastName = readRequiredText(scanner, "Last name: ");
                            String email = readRequiredText(scanner, "Email: ");
                            LocalDate date = readOptionalDate(scanner);
                            addStudent(firstName, lastName, email, date);
                        }
                        case "3" -> {
                            int id = readPositiveInt(scanner, "Student ID: ");
                            String email = readRequiredText(scanner, "New email: ");
                            updateStudentEmail(id, email);
                        }
                        case "4" -> {
                            int id = readPositiveInt(scanner, "Student ID: ");
                            deleteStudent(id);
                        }
                        case "5" -> {
                            System.out.println("Goodbye!");
                            return;
                        }
                        default -> System.out.println("Invalid option. Choose 1 through 5.");
                    }
                } catch (SQLException e) {
                    // PostgreSQL enforces uniqueness and not-null constraints; report failures cleanly.
                    System.err.println("Database operation failed: " + e.getMessage());
                }
            }
        }
    }
}
