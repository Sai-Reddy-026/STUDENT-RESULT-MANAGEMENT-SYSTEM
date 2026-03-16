USE result_management;

-- Non-destructive seed script:
-- 1) Keeps all existing records safe
-- 2) Adds 4 semesters x 8 subjects (32 courses total)
-- 3) Adds one student with results in all 32 subjects
-- 4) Uses INSERT IGNORE so reruns are safe

START TRANSACTION;

-- Ensure teacher records exist for subject ownership
INSERT IGNORE INTO teacher (teacher_id, teacher_name, email, department, password) VALUES
(301, 'Dr. Nitin Arora', 'nitin.arora@college.edu', 'CSE', 'teach301'),
(302, 'Dr. Pooja Menon', 'pooja.menon@college.edu', 'CSE', 'teach302'),
(303, 'Dr. Sameer Kulkarni', 'sameer.kulkarni@college.edu', 'ECE', 'teach303'),
(304, 'Dr. Harish Iyer', 'harish.iyer@college.edu', 'ME', 'teach304');

-- Create one student who will have results in all 32 subjects
INSERT IGNORE INTO student (student_id, student_name, email, password, department) VALUES
(3001, 'Aarav Mehta', 'aarav.mehta@student.edu', 'stud3001', 'CSE');

-- Semester 1 (8 subjects)
INSERT IGNORE INTO course (course_id, course_name, teacher_id, semester) VALUES
(4001, 'Mathematics I', 301, 1),
(4002, 'Physics', 304, 1),
(4003, 'Basic Electrical Engineering', 303, 1),
(4004, 'Programming in C', 301, 1),
(4005, 'Engineering Graphics', 304, 1),
(4006, 'Communication Skills', 302, 1),
(4007, 'Environmental Science', 302, 1),
(4008, 'Workshop Practice', 304, 1);

-- Semester 2 (8 subjects)
INSERT IGNORE INTO course (course_id, course_name, teacher_id, semester) VALUES
(4009, 'Mathematics II', 301, 2),
(4010, 'Data Structures', 301, 2),
(4011, 'Digital Logic', 303, 2),
(4012, 'OOP with Java', 302, 2),
(4013, 'Discrete Mathematics', 301, 2),
(4014, 'Computer Organization', 303, 2),
(4015, 'Technical Writing', 302, 2),
(4016, 'Engineering Economics', 304, 2);

-- Semester 3 (8 subjects)
INSERT IGNORE INTO course (course_id, course_name, teacher_id, semester) VALUES
(4017, 'Database Management Systems', 302, 3),
(4018, 'Operating Systems', 301, 3),
(4019, 'Computer Networks', 303, 3),
(4020, 'Design and Analysis of Algorithms', 301, 3),
(4021, 'Software Engineering', 302, 3),
(4022, 'Microprocessors', 303, 3),
(4023, 'Probability and Statistics', 301, 3),
(4024, 'Web Technologies', 302, 3);

-- Semester 4 (8 subjects)
INSERT IGNORE INTO course (course_id, course_name, teacher_id, semester) VALUES
(4025, 'Artificial Intelligence', 301, 4),
(4026, 'Machine Learning', 302, 4),
(4027, 'Compiler Design', 301, 4),
(4028, 'Cloud Computing', 302, 4),
(4029, 'Information Security', 303, 4),
(4030, 'Internet of Things', 303, 4),
(4031, 'Project Management', 304, 4),
(4032, 'Major Project', 304, 4);

-- Result entries for student 3001 in all 32 subjects
INSERT IGNORE INTO result (student_id, course_id, marks, awardedby) VALUES
(3001, 4001, 86, '301'),
(3001, 4002, 78, '304'),
(3001, 4003, 74, '303'),
(3001, 4004, 92, '301'),
(3001, 4005, 81, '304'),
(3001, 4006, 88, '302'),
(3001, 4007, 84, '302'),
(3001, 4008, 76, '304'),

(3001, 4009, 83, '301'),
(3001, 4010, 90, '301'),
(3001, 4011, 79, '303'),
(3001, 4012, 91, '302'),
(3001, 4013, 85, '301'),
(3001, 4014, 77, '303'),
(3001, 4015, 89, '302'),
(3001, 4016, 73, '304'),

(3001, 4017, 87, '302'),
(3001, 4018, 82, '301'),
(3001, 4019, 80, '303'),
(3001, 4020, 88, '301'),
(3001, 4021, 86, '302'),
(3001, 4022, 75, '303'),
(3001, 4023, 84, '301'),
(3001, 4024, 90, '302'),

(3001, 4025, 92, '301'),
(3001, 4026, 89, '302'),
(3001, 4027, 81, '301'),
(3001, 4028, 88, '302'),
(3001, 4029, 79, '303'),
(3001, 4030, 85, '303'),
(3001, 4031, 91, '304'),
(3001, 4032, 94, '304');

COMMIT;

