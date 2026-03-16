USE result_management;

-- Add 4 more subjects per semester (1-4) and map them to student 3001.
-- Non-destructive and rerunnable.

START TRANSACTION;

-- Semester 1 additional subjects
INSERT IGNORE INTO course (course_id, course_name, teacher_id, semester) VALUES
(4033, 'Engineering Chemistry', 304, 1),
(4034, 'Linear Algebra', 301, 1),
(4035, 'Problem Solving Techniques', 302, 1),
(4036, 'Fundamentals of Electronics', 303, 1);

-- Semester 2 additional subjects
INSERT IGNORE INTO course (course_id, course_name, teacher_id, semester) VALUES
(4037, 'Computer Graphics', 302, 2),
(4038, 'Numerical Methods', 301, 2),
(4039, 'Electronic Circuits', 303, 2),
(4040, 'Professional Ethics', 304, 2);

-- Semester 3 additional subjects
INSERT IGNORE INTO course (course_id, course_name, teacher_id, semester) VALUES
(4041, 'Theory of Computation', 301, 3),
(4042, 'Mobile Application Development', 302, 3),
(4043, 'Embedded Systems', 303, 3),
(4044, 'Research Methodology', 304, 3);

-- Semester 4 additional subjects
INSERT IGNORE INTO course (course_id, course_name, teacher_id, semester) VALUES
(4045, 'Data Mining', 301, 4),
(4046, 'Deep Learning', 302, 4),
(4047, 'DevOps and Automation', 302, 4),
(4048, 'Robotics Fundamentals', 304, 4);

-- Results for student 3001 in all newly added subjects
INSERT IGNORE INTO result (student_id, course_id, marks, awardedby) VALUES
(3001, 4033, 82, '304'),
(3001, 4034, 88, '301'),
(3001, 4035, 91, '302'),
(3001, 4036, 79, '303'),

(3001, 4037, 86, '302'),
(3001, 4038, 84, '301'),
(3001, 4039, 77, '303'),
(3001, 4040, 93, '304'),

(3001, 4041, 85, '301'),
(3001, 4042, 90, '302'),
(3001, 4043, 81, '303'),
(3001, 4044, 89, '304'),

(3001, 4045, 87, '301'),
(3001, 4046, 92, '302'),
(3001, 4047, 88, '302'),
(3001, 4048, 83, '304');

COMMIT;

