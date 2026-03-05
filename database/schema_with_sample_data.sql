CREATE DATABASE IF NOT EXISTS result_management;
USE result_management;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS result;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS teacher;
DROP TABLE IF EXISTS admin;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE admin (
    admin_id INT NOT NULL,
    admin_name VARCHAR(100),
    password VARCHAR(100) NOT NULL,
    PRIMARY KEY (admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE teacher (
    teacher_id INT NOT NULL,
    teacher_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    department VARCHAR(100) NOT NULL,
    password VARCHAR(25),
    PRIMARY KEY (teacher_id),
    UNIQUE KEY email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE student (
    student_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    password VARCHAR(100) NOT NULL,
    department VARCHAR(20),
    PRIMARY KEY (student_id),
    UNIQUE KEY email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE course (
    course_id INT NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    teacher_id INT,
    semester INT,
    PRIMARY KEY (course_id),
    KEY teacher_id (teacher_id),
    CONSTRAINT course_ibfk_1 FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE result (
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    marks INT,
    awardedby VARCHAR(25),
    PRIMARY KEY (student_id, course_id),
    KEY fk_course (course_id),
    CONSTRAINT result_ibfk_1 FOREIGN KEY (student_id) REFERENCES student (student_id),
    CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO teacher (teacher_id, teacher_name, email, department, password) VALUES
(101, 'Amit Sharma', 'amit.sharma@college.edu', 'CSE', 'teach101'),
(102, 'Neha Verma', 'neha.verma@college.edu', 'ECE', 'teach102'),
(103, 'Ravi Kumar', 'ravi.kumar@college.edu', 'ME', 'teach103');

INSERT INTO admin (admin_id, admin_name, password) VALUES
(1, 'System Administrator', 'admin123');

INSERT INTO student (student_id, student_name, email, password, department) VALUES
(1001, 'Rahul Singh', 'rahul.singh@student.edu', 'stud1001', 'CSE'),
(1002, 'Priya Nair', 'priya.nair@student.edu', 'stud1002', 'ECE'),
(1003, 'Arjun Patel', 'arjun.patel@student.edu', 'stud1003', 'ME');

INSERT INTO course (course_id, course_name, teacher_id, semester) VALUES
(201, 'Data Structures', 101, 3),
(202, 'Digital Electronics', 102, 4),
(203, 'Thermodynamics', 103, 4);

INSERT INTO result (student_id, course_id, marks, awardedby) VALUES
(1001, 201, 88, '101'),
(1002, 202, 91, '102'),
(1003, 203, 79, '103');
