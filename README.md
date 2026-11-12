# Assignment 3 — Question 1: Java + PostgreSQL Student CRUD

## Objective

A command-line Java application that connects to PostgreSQL with JDBC and performs the four functions required by the assignment:

1. `getAllStudents()` — read and display every student.
2. `addStudent(firstName, lastName, email, enrollmentDate)` — insert a student.
3. `updateStudentEmail(studentId, newEmail)` — update the email for a given student ID.
4. `deleteStudent(studentId)` — remove a student for a given student ID.

SQL queries use `PreparedStatement` parameters to prevent unsafe SQL string concatenation. Database resources are closed with try-with-resources; SQL errors are reported without ending the menu.

## Repository layout

```text
assignment3_java/
├── database/
│   └── setup.sql
├── src/main/java/
│   └── StudentApp.java
├── .gitignore
├── pom.xml
└── README.md
```

## Prerequisites

- PostgreSQL running locally and pgAdmin installed
- JDK 17 or newer
- Apache Maven installed and accessible from your terminal
- Internet connection on first Maven run (to download the PostgreSQL JDBC driver)

## Step 1: Set up the PostgreSQL database

1. Open pgAdmin and connect to your PostgreSQL server.
2. Right-click **Databases → Create → Database**.
3. Name the new database **`students_db`** and save.
4. Select **students_db**, open **Tools → Query Tool**.
5. Copy and execute the contents of **`database/setup.sql`**.
6. The final `SELECT` should show the three initial students: John Doe, Jane Smith, and Jim Beam.

Run `setup.sql` only in **students_db**, not the default postgres database.

## Step 2: Configure the database connection

The application supports these environment variables:

| Variable | Default | Description |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/students_db` | PostgreSQL JDBC connection URL |
| `DB_USER` | `postgres` | PostgreSQL username |
| `DB_PASSWORD` | empty string | PostgreSQL password (usually required) |

Set them **in the terminal where you will run Maven**. Examples:

**Windows PowerShell:**

```powershell
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "YOUR_POSTGRES_PASSWORD"
$env:DB_URL = "jdbc:postgresql://localhost:5432/students_db"
```

**macOS / Linux (Bash):**

```bash
export DB_USER='postgres'
export DB_PASSWORD='YOUR_POSTGRES_PASSWORD'
export DB_URL='jdbc:postgresql://localhost:5432/students_db'
```

Do not commit passwords or `.env` files to a public repository.

## Step 3: Compile and run

Open a terminal in the repository root (the folder containing `pom.xml`):

```bash
mvn compile
mvn exec:java
```

Use the menu to call all four functions. `getAllStudents()` displays student IDs, which you can enter when updating or deleting.

## Example demonstration sequence

1. In pgAdmin show `students_db`, the `students` schema/constraints, and three starter rows (`SELECT * FROM students ORDER BY student_id;`).
2. Start `mvn exec:java`; choose **1** to display the three initial records.
3. Choose **2** and enter `Alex`, `Taylor`, `alex.taylor@example.com`, `2026-11-09`. Record the new student ID printed by the application.
4. In pgAdmin run `SELECT * FROM students ORDER BY student_id;` to show the inserted row.
5. Choose **3**, enter that new ID, and `alex.updated@example.com`. Rerun the SELECT in pgAdmin to demonstrate the updated email.
6. Choose **4**, enter the same student ID. Rerun the SELECT in pgAdmin to demonstrate the deleted row.
7. Choose **1** to show the remaining students; choose **5** to exit.

The generated ID may be different from 4 if you've inserted students previously. Use the ID actually printed by the application.

## Video and submission checklist

- [ ] Record a **maximum 5-minute** video showing database setup, each of the four Java functions, and changes in pgAdmin for INSERT, UPDATE, and DELETE.
- [ ] Upload the recording to YouTube/Vimeo/cloud storage with link-sharing enabled.
- [ ] Add a working demonstration video link below.
- [x] Create a **public GitHub repository** and push `StudentApp.java`, `setup.sql`, `pom.xml`, and `README.md`.
- [ ] Submit your **GitHub repository URL** in Brightspace.

**Demonstration video:** Pending recording and upload. The required video has not yet been produced.

## Verified local execution

The project compiled with Java 23 and Maven 3.10.0. All four menu operations passed against PostgreSQL 18.6. CREATE, UPDATE, and DELETE were independently checked through separate SQL queries. The test student was removed and the final table contained the three initial students. See [verification results](docs/verification.txt).

The local test used a separate PostgreSQL instance on port 5433. To use that port, set `DB_URL` to `jdbc:postgresql://127.0.0.1:5433/students_db` and provide your own database login through the environment variables above. No database credentials or local database files are included in this repository.

## Notes

- PostgreSQL rejects duplicate emails because of the `UNIQUE` constraint.
- `first_name`, `last_name`, and `email` cannot be null. `enrollment_date` may be null.
- Updates/deletes with nonexistent student IDs report that no student was found.
- Running `setup.sql` again preserves existing records and ignores duplicate starter emails. It does **not** reset the database after the demonstration.
