-- Azrou Center App Mock Data Generation Script
-- IMPORTANT: This drops your existing schema to freshly rebuild it!
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

-- Initialize tables structure from the DatabaseManager definitions or simply assume the app creates them if we start after dropping.
-- WAIT! If we drop the schema, we must recreate the tables before doing INSERTs!

CREATE TABLE IF NOT EXISTS groups (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    capacity INTEGER NOT NULL DEFAULT 25,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS teachers (
    id SERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT UNIQUE,
    phone TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    group_id INTEGER NOT NULL,
    full_name TEXT NOT NULL,
    cin TEXT UNIQUE NOT NULL,
    qualifications TEXT,
    date_of_birth DATE,
    phone TEXT,
    photo_path TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subjects (
    id SERIAL PRIMARY KEY,
    group_id INTEGER NOT NULL,
    teacher_id INTEGER,
    name TEXT NOT NULL,
    code TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL,
    UNIQUE(group_id, name)
);

CREATE TABLE IF NOT EXISTS assessments (
    id SERIAL PRIMARY KEY,
    subject_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    date DATE,
    max_score REAL NOT NULL DEFAULT 100.0,
    weight REAL NOT NULL DEFAULT 1.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS grades (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    assessment_id INTEGER NOT NULL,
    score REAL,
    recorded_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (assessment_id) REFERENCES assessments(id) ON DELETE CASCADE,
    UNIQUE(student_id, assessment_id)
);

CREATE TABLE IF NOT EXISTS admins (
    id SERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    full_name TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS absences (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    subject_id INTEGER NOT NULL,
    date DATE NOT NULL,
    justified BOOLEAN NOT NULL DEFAULT false,
    reason TEXT,
    recorded_by INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES admins(id),
    UNIQUE(student_id, subject_id, date)
);


-- Inserting 5 Groups
INSERT INTO groups (id, name, description, capacity) VALUES (1, 'Group A', 'Standard description for Group A', 30);
INSERT INTO groups (id, name, description, capacity) VALUES (2, 'Group B', 'Standard description for Group B', 30);
INSERT INTO groups (id, name, description, capacity) VALUES (3, 'Group C', 'Standard description for Group C', 30);
INSERT INTO groups (id, name, description, capacity) VALUES (4, 'Group D', 'Standard description for Group D', 30);
INSERT INTO groups (id, name, description, capacity) VALUES (5, 'Group E', 'Standard description for Group E', 30);

-- Inserting 15 Teachers
INSERT INTO teachers (id, full_name, email, phone) VALUES (1, 'Tariq Fassi', 'teacher1@azrou.edu', '0600000001');
INSERT INTO teachers (id, full_name, email, phone) VALUES (2, 'Sara Alaoui', 'teacher2@azrou.edu', '0600000002');
INSERT INTO teachers (id, full_name, email, phone) VALUES (3, 'Khalid Bennis', 'teacher3@azrou.edu', '0600000003');
INSERT INTO teachers (id, full_name, email, phone) VALUES (4, 'Amina Guessous', 'teacher4@azrou.edu', '0600000004');
INSERT INTO teachers (id, full_name, email, phone) VALUES (5, 'Omar Tazi', 'teacher5@azrou.edu', '0600000005');
INSERT INTO teachers (id, full_name, email, phone) VALUES (6, 'Youssef Idrissi', 'teacher6@azrou.edu', '0600000006');
INSERT INTO teachers (id, full_name, email, phone) VALUES (7, 'Nadia Hachimi', 'teacher7@azrou.edu', '0600000007');
INSERT INTO teachers (id, full_name, email, phone) VALUES (8, 'Hassan Chraibi', 'teacher8@azrou.edu', '0600000008');
INSERT INTO teachers (id, full_name, email, phone) VALUES (9, 'Salma Bennani', 'teacher9@azrou.edu', '0600000009');
INSERT INTO teachers (id, full_name, email, phone) VALUES (10, 'Yassine Mansouri', 'teacher10@azrou.edu', '0600000010');
INSERT INTO teachers (id, full_name, email, phone) VALUES (11, 'Rachid Benjelloun', 'teacher11@azrou.edu', '0600000011');
INSERT INTO teachers (id, full_name, email, phone) VALUES (12, 'Karim Lazrak', 'teacher12@azrou.edu', '0600000012');
INSERT INTO teachers (id, full_name, email, phone) VALUES (13, 'Mouna Lahlou', 'teacher13@azrou.edu', '0600000013');
INSERT INTO teachers (id, full_name, email, phone) VALUES (14, 'Adil Ziani', 'teacher14@azrou.edu', '0600000014');
INSERT INTO teachers (id, full_name, email, phone) VALUES (15, 'Houda Mernissi', 'teacher15@azrou.edu', '0600000015');

-- Inserting 30 Subjects (6 per group, mapped to a random teacher)
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (1, 1, 15, 'Physical Education', 'SUB-001');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (2, 1, 13, 'Art', 'SUB-002');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (3, 1, 8, 'CS', 'SUB-003');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (4, 1, 2, 'Physics', 'SUB-004');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (5, 1, 6, 'French', 'SUB-005');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (6, 1, 4, 'English', 'SUB-006');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (7, 2, 5, 'Biology', 'SUB-007');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (8, 2, 8, 'Art', 'SUB-008');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (9, 2, 4, 'Arabic', 'SUB-009');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (10, 2, 10, 'Physical Education', 'SUB-010');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (11, 2, 11, 'History', 'SUB-011');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (12, 2, 2, 'English', 'SUB-012');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (13, 3, 10, 'Chemistry', 'SUB-013');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (14, 3, 13, 'History', 'SUB-014');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (15, 3, 2, 'English', 'SUB-015');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (16, 3, 13, 'Biology', 'SUB-016');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (17, 3, 6, 'Art', 'SUB-017');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (18, 3, 9, 'Physics', 'SUB-018');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (19, 4, 13, 'CS', 'SUB-019');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (20, 4, 1, 'Physical Education', 'SUB-020');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (21, 4, 2, 'French', 'SUB-021');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (22, 4, 13, 'History', 'SUB-022');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (23, 4, 8, 'Arabic', 'SUB-023');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (24, 4, 5, 'Art', 'SUB-024');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (25, 5, 9, 'Geography', 'SUB-025');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (26, 5, 15, 'Art', 'SUB-026');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (27, 5, 10, 'History', 'SUB-027');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (28, 5, 12, 'Mathematics', 'SUB-028');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (29, 5, 1, 'French', 'SUB-029');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (30, 5, 5, 'Biology', 'SUB-030');

-- Inserting 150 Students (30 per group)
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (1, 1, 'Student_001', 'CIN00001', 'Baccalaureate', '2005-07-15', '0612345001');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (2, 1, 'Student_002', 'CIN00002', 'Baccalaureate', '2005-03-13', '0612345002');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (3, 1, 'Student_003', 'CIN00003', 'Baccalaureate', '2005-04-25', '0612345003');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (4, 1, 'Student_004', 'CIN00004', 'Baccalaureate', '2005-01-19', '0612345004');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (5, 1, 'Student_005', 'CIN00005', 'Baccalaureate', '2005-07-24', '0612345005');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (6, 1, 'Student_006', 'CIN00006', 'Baccalaureate', '2005-07-16', '0612345006');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (7, 1, 'Student_007', 'CIN00007', 'Baccalaureate', '2005-03-22', '0612345007');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (8, 1, 'Student_008', 'CIN00008', 'Baccalaureate', '2005-06-25', '0612345008');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (9, 1, 'Student_009', 'CIN00009', 'Baccalaureate', '2005-01-21', '0612345009');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (10, 1, 'Student_010', 'CIN00010', 'Baccalaureate', '2005-06-14', '0612345010');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (11, 1, 'Student_011', 'CIN00011', 'Baccalaureate', '2005-09-16', '0612345011');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (12, 1, 'Student_012', 'CIN00012', 'Baccalaureate', '2005-02-23', '0612345012');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (13, 1, 'Student_013', 'CIN00013', 'Baccalaureate', '2005-03-22', '0612345013');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (14, 1, 'Student_014', 'CIN00014', 'Baccalaureate', '2005-02-12', '0612345014');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (15, 1, 'Student_015', 'CIN00015', 'Baccalaureate', '2005-02-13', '0612345015');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (16, 1, 'Student_016', 'CIN00016', 'Baccalaureate', '2005-02-18', '0612345016');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (17, 1, 'Student_017', 'CIN00017', 'Baccalaureate', '2005-01-27', '0612345017');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (18, 1, 'Student_018', 'CIN00018', 'Baccalaureate', '2005-01-15', '0612345018');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (19, 1, 'Student_019', 'CIN00019', 'Baccalaureate', '2005-06-28', '0612345019');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (20, 1, 'Student_020', 'CIN00020', 'Baccalaureate', '2005-02-24', '0612345020');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (21, 1, 'Student_021', 'CIN00021', 'Baccalaureate', '2005-09-12', '0612345021');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (22, 1, 'Student_022', 'CIN00022', 'Baccalaureate', '2005-06-15', '0612345022');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (23, 1, 'Student_023', 'CIN00023', 'Baccalaureate', '2005-06-22', '0612345023');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (24, 1, 'Student_024', 'CIN00024', 'Baccalaureate', '2005-05-16', '0612345024');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (25, 1, 'Student_025', 'CIN00025', 'Baccalaureate', '2005-05-14', '0612345025');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (26, 1, 'Student_026', 'CIN00026', 'Baccalaureate', '2005-03-24', '0612345026');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (27, 1, 'Student_027', 'CIN00027', 'Baccalaureate', '2005-07-22', '0612345027');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (28, 1, 'Student_028', 'CIN00028', 'Baccalaureate', '2005-08-12', '0612345028');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (29, 1, 'Student_029', 'CIN00029', 'Baccalaureate', '2005-07-25', '0612345029');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (30, 1, 'Student_030', 'CIN00030', 'Baccalaureate', '2005-02-17', '0612345030');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (31, 2, 'Student_031', 'CIN00031', 'Baccalaureate', '2005-09-17', '0612345031');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (32, 2, 'Student_032', 'CIN00032', 'Baccalaureate', '2005-04-10', '0612345032');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (33, 2, 'Student_033', 'CIN00033', 'Baccalaureate', '2005-03-11', '0612345033');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (34, 2, 'Student_034', 'CIN00034', 'Baccalaureate', '2005-06-23', '0612345034');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (35, 2, 'Student_035', 'CIN00035', 'Baccalaureate', '2005-05-14', '0612345035');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (36, 2, 'Student_036', 'CIN00036', 'Baccalaureate', '2005-01-14', '0612345036');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (37, 2, 'Student_037', 'CIN00037', 'Baccalaureate', '2005-01-11', '0612345037');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (38, 2, 'Student_038', 'CIN00038', 'Baccalaureate', '2005-06-12', '0612345038');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (39, 2, 'Student_039', 'CIN00039', 'Baccalaureate', '2005-02-16', '0612345039');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (40, 2, 'Student_040', 'CIN00040', 'Baccalaureate', '2005-08-28', '0612345040');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (41, 2, 'Student_041', 'CIN00041', 'Baccalaureate', '2005-09-24', '0612345041');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (42, 2, 'Student_042', 'CIN00042', 'Baccalaureate', '2005-03-24', '0612345042');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (43, 2, 'Student_043', 'CIN00043', 'Baccalaureate', '2005-08-28', '0612345043');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (44, 2, 'Student_044', 'CIN00044', 'Baccalaureate', '2005-04-24', '0612345044');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (45, 2, 'Student_045', 'CIN00045', 'Baccalaureate', '2005-09-20', '0612345045');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (46, 2, 'Student_046', 'CIN00046', 'Baccalaureate', '2005-06-21', '0612345046');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (47, 2, 'Student_047', 'CIN00047', 'Baccalaureate', '2005-01-27', '0612345047');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (48, 2, 'Student_048', 'CIN00048', 'Baccalaureate', '2005-07-23', '0612345048');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (49, 2, 'Student_049', 'CIN00049', 'Baccalaureate', '2005-08-28', '0612345049');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (50, 2, 'Student_050', 'CIN00050', 'Baccalaureate', '2005-08-16', '0612345050');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (51, 2, 'Student_051', 'CIN00051', 'Baccalaureate', '2005-03-10', '0612345051');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (52, 2, 'Student_052', 'CIN00052', 'Baccalaureate', '2005-09-18', '0612345052');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (53, 2, 'Student_053', 'CIN00053', 'Baccalaureate', '2005-06-19', '0612345053');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (54, 2, 'Student_054', 'CIN00054', 'Baccalaureate', '2005-01-16', '0612345054');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (55, 2, 'Student_055', 'CIN00055', 'Baccalaureate', '2005-08-27', '0612345055');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (56, 2, 'Student_056', 'CIN00056', 'Baccalaureate', '2005-06-24', '0612345056');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (57, 2, 'Student_057', 'CIN00057', 'Baccalaureate', '2005-02-28', '0612345057');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (58, 2, 'Student_058', 'CIN00058', 'Baccalaureate', '2005-03-20', '0612345058');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (59, 2, 'Student_059', 'CIN00059', 'Baccalaureate', '2005-05-23', '0612345059');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (60, 2, 'Student_060', 'CIN00060', 'Baccalaureate', '2005-06-23', '0612345060');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (61, 3, 'Student_061', 'CIN00061', 'Baccalaureate', '2005-06-22', '0612345061');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (62, 3, 'Student_062', 'CIN00062', 'Baccalaureate', '2005-04-16', '0612345062');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (63, 3, 'Student_063', 'CIN00063', 'Baccalaureate', '2005-09-22', '0612345063');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (64, 3, 'Student_064', 'CIN00064', 'Baccalaureate', '2005-05-22', '0612345064');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (65, 3, 'Student_065', 'CIN00065', 'Baccalaureate', '2005-09-26', '0612345065');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (66, 3, 'Student_066', 'CIN00066', 'Baccalaureate', '2005-09-11', '0612345066');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (67, 3, 'Student_067', 'CIN00067', 'Baccalaureate', '2005-05-21', '0612345067');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (68, 3, 'Student_068', 'CIN00068', 'Baccalaureate', '2005-02-20', '0612345068');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (69, 3, 'Student_069', 'CIN00069', 'Baccalaureate', '2005-09-24', '0612345069');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (70, 3, 'Student_070', 'CIN00070', 'Baccalaureate', '2005-07-11', '0612345070');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (71, 3, 'Student_071', 'CIN00071', 'Baccalaureate', '2005-06-21', '0612345071');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (72, 3, 'Student_072', 'CIN00072', 'Baccalaureate', '2005-05-12', '0612345072');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (73, 3, 'Student_073', 'CIN00073', 'Baccalaureate', '2005-07-15', '0612345073');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (74, 3, 'Student_074', 'CIN00074', 'Baccalaureate', '2005-04-18', '0612345074');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (75, 3, 'Student_075', 'CIN00075', 'Baccalaureate', '2005-08-20', '0612345075');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (76, 3, 'Student_076', 'CIN00076', 'Baccalaureate', '2005-08-11', '0612345076');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (77, 3, 'Student_077', 'CIN00077', 'Baccalaureate', '2005-08-28', '0612345077');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (78, 3, 'Student_078', 'CIN00078', 'Baccalaureate', '2005-05-25', '0612345078');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (79, 3, 'Student_079', 'CIN00079', 'Baccalaureate', '2005-05-25', '0612345079');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (80, 3, 'Student_080', 'CIN00080', 'Baccalaureate', '2005-01-14', '0612345080');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (81, 3, 'Student_081', 'CIN00081', 'Baccalaureate', '2005-03-15', '0612345081');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (82, 3, 'Student_082', 'CIN00082', 'Baccalaureate', '2005-02-25', '0612345082');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (83, 3, 'Student_083', 'CIN00083', 'Baccalaureate', '2005-01-21', '0612345083');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (84, 3, 'Student_084', 'CIN00084', 'Baccalaureate', '2005-07-11', '0612345084');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (85, 3, 'Student_085', 'CIN00085', 'Baccalaureate', '2005-07-11', '0612345085');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (86, 3, 'Student_086', 'CIN00086', 'Baccalaureate', '2005-01-14', '0612345086');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (87, 3, 'Student_087', 'CIN00087', 'Baccalaureate', '2005-05-23', '0612345087');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (88, 3, 'Student_088', 'CIN00088', 'Baccalaureate', '2005-07-12', '0612345088');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (89, 3, 'Student_089', 'CIN00089', 'Baccalaureate', '2005-04-20', '0612345089');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (90, 3, 'Student_090', 'CIN00090', 'Baccalaureate', '2005-09-15', '0612345090');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (91, 4, 'Student_091', 'CIN00091', 'Baccalaureate', '2005-09-11', '0612345091');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (92, 4, 'Student_092', 'CIN00092', 'Baccalaureate', '2005-02-24', '0612345092');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (93, 4, 'Student_093', 'CIN00093', 'Baccalaureate', '2005-07-19', '0612345093');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (94, 4, 'Student_094', 'CIN00094', 'Baccalaureate', '2005-02-10', '0612345094');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (95, 4, 'Student_095', 'CIN00095', 'Baccalaureate', '2005-02-14', '0612345095');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (96, 4, 'Student_096', 'CIN00096', 'Baccalaureate', '2005-08-20', '0612345096');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (97, 4, 'Student_097', 'CIN00097', 'Baccalaureate', '2005-02-21', '0612345097');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (98, 4, 'Student_098', 'CIN00098', 'Baccalaureate', '2005-06-27', '0612345098');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (99, 4, 'Student_099', 'CIN00099', 'Baccalaureate', '2005-09-17', '0612345099');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (100, 4, 'Student_100', 'CIN00100', 'Baccalaureate', '2005-09-12', '0612345100');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (101, 4, 'Student_101', 'CIN00101', 'Baccalaureate', '2005-08-12', '0612345101');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (102, 4, 'Student_102', 'CIN00102', 'Baccalaureate', '2005-06-16', '0612345102');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (103, 4, 'Student_103', 'CIN00103', 'Baccalaureate', '2005-03-12', '0612345103');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (104, 4, 'Student_104', 'CIN00104', 'Baccalaureate', '2005-03-17', '0612345104');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (105, 4, 'Student_105', 'CIN00105', 'Baccalaureate', '2005-02-26', '0612345105');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (106, 4, 'Student_106', 'CIN00106', 'Baccalaureate', '2005-02-11', '0612345106');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (107, 4, 'Student_107', 'CIN00107', 'Baccalaureate', '2005-02-27', '0612345107');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (108, 4, 'Student_108', 'CIN00108', 'Baccalaureate', '2005-04-26', '0612345108');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (109, 4, 'Student_109', 'CIN00109', 'Baccalaureate', '2005-06-16', '0612345109');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (110, 4, 'Student_110', 'CIN00110', 'Baccalaureate', '2005-04-16', '0612345110');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (111, 4, 'Student_111', 'CIN00111', 'Baccalaureate', '2005-02-16', '0612345111');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (112, 4, 'Student_112', 'CIN00112', 'Baccalaureate', '2005-05-28', '0612345112');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (113, 4, 'Student_113', 'CIN00113', 'Baccalaureate', '2005-06-17', '0612345113');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (114, 4, 'Student_114', 'CIN00114', 'Baccalaureate', '2005-09-19', '0612345114');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (115, 4, 'Student_115', 'CIN00115', 'Baccalaureate', '2005-08-12', '0612345115');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (116, 4, 'Student_116', 'CIN00116', 'Baccalaureate', '2005-03-10', '0612345116');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (117, 4, 'Student_117', 'CIN00117', 'Baccalaureate', '2005-06-22', '0612345117');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (118, 4, 'Student_118', 'CIN00118', 'Baccalaureate', '2005-03-13', '0612345118');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (119, 4, 'Student_119', 'CIN00119', 'Baccalaureate', '2005-05-25', '0612345119');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (120, 4, 'Student_120', 'CIN00120', 'Baccalaureate', '2005-09-24', '0612345120');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (121, 5, 'Student_121', 'CIN00121', 'Baccalaureate', '2005-04-25', '0612345121');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (122, 5, 'Student_122', 'CIN00122', 'Baccalaureate', '2005-02-21', '0612345122');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (123, 5, 'Student_123', 'CIN00123', 'Baccalaureate', '2005-02-14', '0612345123');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (124, 5, 'Student_124', 'CIN00124', 'Baccalaureate', '2005-01-15', '0612345124');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (125, 5, 'Student_125', 'CIN00125', 'Baccalaureate', '2005-01-17', '0612345125');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (126, 5, 'Student_126', 'CIN00126', 'Baccalaureate', '2005-02-18', '0612345126');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (127, 5, 'Student_127', 'CIN00127', 'Baccalaureate', '2005-02-20', '0612345127');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (128, 5, 'Student_128', 'CIN00128', 'Baccalaureate', '2005-09-20', '0612345128');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (129, 5, 'Student_129', 'CIN00129', 'Baccalaureate', '2005-03-23', '0612345129');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (130, 5, 'Student_130', 'CIN00130', 'Baccalaureate', '2005-09-11', '0612345130');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (131, 5, 'Student_131', 'CIN00131', 'Baccalaureate', '2005-03-23', '0612345131');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (132, 5, 'Student_132', 'CIN00132', 'Baccalaureate', '2005-02-28', '0612345132');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (133, 5, 'Student_133', 'CIN00133', 'Baccalaureate', '2005-01-20', '0612345133');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (134, 5, 'Student_134', 'CIN00134', 'Baccalaureate', '2005-02-20', '0612345134');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (135, 5, 'Student_135', 'CIN00135', 'Baccalaureate', '2005-03-25', '0612345135');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (136, 5, 'Student_136', 'CIN00136', 'Baccalaureate', '2005-05-14', '0612345136');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (137, 5, 'Student_137', 'CIN00137', 'Baccalaureate', '2005-06-15', '0612345137');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (138, 5, 'Student_138', 'CIN00138', 'Baccalaureate', '2005-06-27', '0612345138');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (139, 5, 'Student_139', 'CIN00139', 'Baccalaureate', '2005-09-24', '0612345139');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (140, 5, 'Student_140', 'CIN00140', 'Baccalaureate', '2005-02-25', '0612345140');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (141, 5, 'Student_141', 'CIN00141', 'Baccalaureate', '2005-07-11', '0612345141');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (142, 5, 'Student_142', 'CIN00142', 'Baccalaureate', '2005-07-11', '0612345142');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (143, 5, 'Student_143', 'CIN00143', 'Baccalaureate', '2005-06-10', '0612345143');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (144, 5, 'Student_144', 'CIN00144', 'Baccalaureate', '2005-08-24', '0612345144');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (145, 5, 'Student_145', 'CIN00145', 'Baccalaureate', '2005-03-12', '0612345145');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (146, 5, 'Student_146', 'CIN00146', 'Baccalaureate', '2005-02-25', '0612345146');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (147, 5, 'Student_147', 'CIN00147', 'Baccalaureate', '2005-09-15', '0612345147');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (148, 5, 'Student_148', 'CIN00148', 'Baccalaureate', '2005-05-26', '0612345148');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (149, 5, 'Student_149', 'CIN00149', 'Baccalaureate', '2005-04-10', '0612345149');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (150, 5, 'Student_150', 'CIN00150', 'Baccalaureate', '2005-07-25', '0612345150');

-- Resetting Sequences so new insertions from Java App start at Correct ID
SELECT setval('groups_id_seq', (SELECT MAX(id) FROM groups));
SELECT setval('teachers_id_seq', (SELECT MAX(id) FROM teachers));
SELECT setval('subjects_id_seq', (SELECT MAX(id) FROM subjects));
SELECT setval('students_id_seq', (SELECT MAX(id) FROM students));
