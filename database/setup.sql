-- Execute in pgAdmin Query Tool while connected to database students_db.
-- Create students_db once using pgAdmin's Create > Database menu first.

-- SERIAL creates an auto-incrementing integer primary key.
CREATE TABLE IF NOT EXISTS students (
    student_id SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    enrollment_date DATE
);

-- Initial records required by the assignment.
-- ON CONFLICT avoids duplicate emails if this script is run more than once.
INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES
    ('John', 'Doe', 'john.doe@example.com', '2023-09-01'),
    ('Jane', 'Smith', 'jane.smith@example.com', '2023-09-01'),
    ('Jim', 'Beam', 'jim.beam@example.com', '2023-09-02')
ON CONFLICT (email) DO NOTHING;

-- Verify database setup.
SELECT * FROM students ORDER BY student_id;
