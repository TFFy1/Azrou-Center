-- ============================================================
-- Azrou Center App — Realistic Mock Data
-- Groups: Patisserie, Energies Renouvelables, Couture, Informatique
-- IDs: randomised 5-digit integers  |  CINs: realistic Moroccan format
-- ============================================================

TRUNCATE TABLE absences, grades, assessments, subjects, students, teachers, admins, groups RESTART IDENTITY CASCADE;
-- ======================== TABLE DEFINITIONS ========================

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
    max_score REAL NOT NULL DEFAULT 20.0,
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

-- ======================== GROUPS ========================
INSERT INTO groups (id, name, description, capacity) VALUES (13278, 'Patisserie', 'Formation en arts culinaires, pâtisserie et confiserie marocaine', 20);
INSERT INTO groups (id, name, description, capacity) VALUES (24592, 'Energies Renouvelables', 'Formation en installation et maintenance des systèmes solaires et éoliens', 22);
INSERT INTO groups (id, name, description, capacity) VALUES (46048, 'Couture', 'Formation en coupe, couture et stylisme vestimentaire', 20);
INSERT INTO groups (id, name, description, capacity) VALUES (93810, 'Informatique', 'Formation en bureautique, réseaux et développement web', 22);

-- ======================== ADMINS ========================
INSERT INTO admins (id, username, password_hash, full_name) VALUES (79514, 'admin', 'admin', 'Administrateur Principal');
INSERT INTO admins (id, username, password_hash, full_name) VALUES (80697, 'coordo_azrou', 'admin', 'Fatima Ezzahra Moumen');

-- ======================== TEACHERS ========================
INSERT INTO teachers (id, full_name, email, phone) VALUES (13905, 'Rachida Senhaji', 'r.senhaji@azrou-centre.ma', '0661234501');
INSERT INTO teachers (id, full_name, email, phone) VALUES (14165, 'Bouchra Ouali', 'b.ouali@azrou-centre.ma', '0661234502');
INSERT INTO teachers (id, full_name, email, phone) VALUES (21395, 'Khalid Darouich', 'k.darouich@azrou-centre.ma', '0661234503');
INSERT INTO teachers (id, full_name, email, phone) VALUES (22280, 'Youssef Naciri', 'y.naciri@azrou-centre.ma', '0661234504');
INSERT INTO teachers (id, full_name, email, phone) VALUES (23434, 'Hassan Benkaddour', 'h.benkaddour@azrou-centre.ma', '0661234505');
INSERT INTO teachers (id, full_name, email, phone) VALUES (28289, 'Nadia Berrada', 'n.berrada@azrou-centre.ma', '0661234506');
INSERT INTO teachers (id, full_name, email, phone) VALUES (39256, 'Kenza El Mansouri', 'k.elmansouri@azrou-centre.ma', '0661234507');
INSERT INTO teachers (id, full_name, email, phone) VALUES (42098, 'Anas Tazi', 'a.tazi@azrou-centre.ma', '0661234508');
INSERT INTO teachers (id, full_name, email, phone) VALUES (65302, 'Soufiane Amrani', 's.amrani@azrou-centre.ma', '0661234509');
INSERT INTO teachers (id, full_name, email, phone) VALUES (81482, 'Imane Cherkaoui', 'i.cherkaoui@azrou-centre.ma', '0661234510');
INSERT INTO teachers (id, full_name, email, phone) VALUES (87397, 'Omar El Fassi', 'o.elfassi@azrou-centre.ma', '0661234511');
INSERT INTO teachers (id, full_name, email, phone) VALUES (98696, 'Laila Bensouda', 'l.bensouda@azrou-centre.ma', '0661234512');

-- ======================== SUBJECTS ========================
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (18675, 13278, 13905, 'Pâtisserie Marocaine', 'PAT-101');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (28301, 13278, 13905, 'Pâtisserie Française', 'PAT-102');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (28726, 13278, 14165, 'Hygiène et Sécurité Alimentaire', 'PAT-103');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (37653, 13278, 14165, 'Décoration et Présentation', 'PAT-104');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (37869, 13278, 87397, 'Français Professionnel', 'PAT-105');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (38746, 13278, 98696, 'Gestion et Entrepreneuriat', 'PAT-106');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (42325, 24592, 21395, 'Énergie Solaire Photovoltaïque', 'ENR-101');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (44438, 24592, 21395, 'Énergie Solaire Thermique', 'ENR-102');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (44718, 24592, 22280, 'Énergie Éolienne', 'ENR-103');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (45093, 24592, 23434, 'Électricité et Câblage', 'ENR-104');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (51245, 24592, 23434, 'Maintenance des Installations', 'ENR-105');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (57447, 24592, 98696, 'Gestion et Entrepreneuriat', 'ENR-106');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (61856, 46048, 28289, 'Coupe et Patronage', 'COU-101');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (62350, 46048, 28289, 'Couture à la Machine', 'COU-102');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (66155, 46048, 39256, 'Broderie et Finitions', 'COU-103');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (70142, 46048, 39256, 'Stylisme et Mode', 'COU-104');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (75435, 46048, 87397, 'Français Professionnel', 'COU-105');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (80644, 46048, 98696, 'Gestion et Entrepreneuriat', 'COU-106');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (83579, 93810, 42098, 'Bureautique (Word, Excel)', 'INFO-101');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (84341, 93810, 42098, 'Réseaux Informatiques', 'INFO-102');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (86484, 93810, 65302, 'Développement Web (HTML/CSS)', 'INFO-103');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (86622, 93810, 65302, 'Programmation de Base', 'INFO-104');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (94259, 93810, 81482, 'Maintenance PC et Périphériques', 'INFO-105');
INSERT INTO subjects (id, group_id, teacher_id, name, code) VALUES (95909, 93810, 98696, 'Gestion et Entrepreneuriat', 'INFO-106');

-- ======================== ASSESSMENTS ========================
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (10425,   18675, 'Contrôle Continu', '2024-11-15',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (11504, 18675, 'Examen Final',      '2025-01-20', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (16175,   28301, 'Contrôle Continu', '2024-11-16',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (18326, 28301, 'Examen Final',      '2025-01-21', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (21915,   28726, 'Contrôle Continu', '2024-11-18',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (23947, 28726, 'Examen Final',      '2025-01-22', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (24371,   37653, 'Contrôle Continu', '2024-11-20',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (24621, 37653, 'Examen Final',      '2025-01-23', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (25014,   37869, 'Contrôle Continu', '2024-11-22',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (28131, 37869, 'Examen Final',      '2025-01-24', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (30032,   38746, 'Contrôle Continu', '2024-11-25',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (30033, 38746, 'Examen Final',      '2025-01-27', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (30730,   42325, 'Contrôle Continu', '2024-11-15',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (30969, 42325, 'Examen Final',      '2025-01-20', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (31174,   44438, 'Contrôle Continu', '2024-11-16',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (33416, 44438, 'Examen Final',      '2025-01-21', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (36071,   44718, 'Contrôle Continu', '2024-11-18',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (42953, 44718, 'Examen Final',      '2025-01-22', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (44522,   45093, 'Contrôle Continu', '2024-11-20',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (44973, 45093, 'Examen Final',      '2025-01-23', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (48469,   51245, 'Contrôle Continu', '2024-11-22',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (49117, 51245, 'Examen Final',      '2025-01-24', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (54587,   57447, 'Contrôle Continu', '2024-11-25',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (59009, 57447, 'Examen Final',      '2025-01-27', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (60019,   61856, 'Contrôle Continu', '2024-11-15',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (60432, 61856, 'Examen Final',      '2025-01-20', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (65333,   62350, 'Contrôle Continu', '2024-11-16',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (66985, 62350, 'Examen Final',      '2025-01-21', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (69470,   66155, 'Contrôle Continu', '2024-11-18',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (71348, 66155, 'Examen Final',      '2025-01-22', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (74686,   70142, 'Contrôle Continu', '2024-11-20',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (75612, 70142, 'Examen Final',      '2025-01-23', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (76540,   75435, 'Contrôle Continu', '2024-11-22',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (76542, 75435, 'Examen Final',      '2025-01-24', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (76784,   80644, 'Contrôle Continu', '2024-11-25',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (79352, 80644, 'Examen Final',      '2025-01-27', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (80381,   83579, 'Contrôle Continu', '2024-11-15',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (82512, 83579, 'Examen Final',      '2025-01-20', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (88104,   84341, 'Contrôle Continu', '2024-11-16',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (88172, 84341, 'Examen Final',      '2025-01-21', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (89818,   86484, 'Contrôle Continu', '2024-11-18',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (91959, 86484, 'Examen Final',      '2025-01-22', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (92240,   86622, 'Contrôle Continu', '2024-11-20',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (93748, 86622, 'Examen Final',      '2025-01-23', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (94012,   94259, 'Contrôle Continu', '2024-11-22',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (99166, 94259, 'Examen Final',      '2025-01-24', 20.0, 0.60);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (99192,   95909, 'Contrôle Continu', '2024-11-25',   20.0, 0.40);
INSERT INTO assessments (id, subject_id, name, date, max_score, weight) VALUES (99353, 95909, 'Examen Final',      '2025-01-27', 20.0, 0.60);

-- ======================== STUDENTS ========================
-- CIN format: regional prefix (AZ=Azrou/Ifrane, G=Fès, U=Fès ancien, BH=Beni Hmed) + 6 digits
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (10851, 13278, 'Khadija Ait Brahim', 'BH465787', 'Brevet', '2001-03-14', '0612001001');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (13478, 13278, 'Fatima Zahra Moussaid', 'AZ331509', 'Brevet', '2000-07-22', '0612001002');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (14207, 13278, 'Hasnae Zouak', 'G393010', 'Brevet', '2002-01-09', '0712001003');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (15695, 13278, 'Samira El Idrissi', 'AZ105183', 'Brevet', '1999-11-30', '0612001004');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (16006, 13278, 'Latifa Ouchen', 'AZ738299', 'Brevet', '2001-05-18', '0712001005');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (17331, 13278, 'Meryem Haddou', 'G376311', 'Brevet', '2003-08-03', '0612001006');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (19116, 13278, 'Zineb Oulhaj', 'U656670', 'Brevet', '2000-02-25', '0612001007');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (19358, 13278, 'Naima Bouziane', 'U106513', 'Brevet', '1998-12-10', '0712001008');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (20328, 13278, 'Rajae Benali', 'AZ387262', 'Brevet', '2002-06-14', '0612001009');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (20458, 13278, 'Souad El Harrachi', 'AZ731781', 'Brevet', '2001-09-27', '0712001010');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (22156, 13278, 'Imane Tlemçani', 'AZ801326', 'Brevet', '2000-04-05', '0612001011');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (22676, 13278, 'Houda Aït Lhaj', 'G736026', 'Brevet', '2003-03-19', '0612001012');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (23238, 13278, 'Amina Lemkaddem', 'AZ647468', 'Brevet', '1999-07-08', '0712001013');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (23396, 13278, 'Ghizlane Boutaleb', 'U723430', 'Brevet', '2002-10-31', '0612001014');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (26361, 13278, 'Nadia Benkirane', 'U805009', 'Brevet', '2001-01-17', '0612001015');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (30379, 13278, 'Widad Ezziani', 'G882081', 'Brevet', '2000-08-23', '0712001016');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (30926, 13278, 'Asma Taoussi', 'AZ191361', 'Brevet', '2002-05-06', '0612001017');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (31319, 13278, 'Sanaa El Khatib', 'U399091', 'Brevet', '1998-09-15', '0712001018');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (31417, 13278, 'Loubna Jebbar', 'G998543', 'Brevet', '2003-12-02', '0612001019');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (32431, 13278, 'Ilham Bouazza', 'U534624', 'Brevet', '2001-06-28', '0712001020');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (35203, 24592, 'Amine El Omari', 'G510799', 'Baccalauréat', '2000-04-12', '0612002001');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (36062, 24592, 'Youssef Bouchikhi', 'AZ183842', 'Baccalauréat', '2001-09-03', '0712002002');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (37460, 24592, 'Hamza Tahiri', 'AZ135427', 'Baccalauréat', '1999-06-21', '0612002003');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (38221, 24592, 'Mehdi Lahrichi', 'G498084', 'Baccalauréat', '2002-02-14', '0712002004');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (38657, 24592, 'Khalil Benabdellah', 'U124118', 'Baccalauréat', '2000-11-08', '0612002005');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (38785, 24592, 'Omar Ait Taleb', 'AZ449353', 'Baccalauréat', '2001-03-29', '0612002006');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (38893, 24592, 'Ismail Zeroual', 'U487401', 'Baccalauréat', '2000-07-17', '0712002007');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (39871, 24592, 'Anas Belkasmi', 'U640052', 'Baccalauréat', '1998-01-25', '0612002008');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (40021, 24592, 'Tarik Jouahri', 'U427868', 'Baccalauréat', '2002-08-11', '0712002009');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (40495, 24592, 'Bilal Chaoui', 'AZ112805', 'Baccalauréat', '2001-05-30', '0612002010');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (40512, 24592, 'Rachid Mouhib', 'U826204', 'Baccalauréat', '1999-10-07', '0612002011');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (42087, 24592, 'Soufiane Lamarti', 'AZ053315', 'Baccalauréat', '2000-12-19', '0712002012');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (44671, 24592, 'Karim El Ouazzani', 'G692322', 'Baccalauréat', '2002-04-04', '0612002013');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (44993, 24592, 'Badr Elaaouad', 'G025634', 'Baccalauréat', '2001-08-22', '0712002014');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (45382, 24592, 'Noureddine Benlahcen', 'AZ160733', 'Baccalauréat', '1997-03-13', '0612002015');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (46421, 24592, 'Abdellatif Sahraoui', 'G543303', 'Baccalauréat', '2000-06-01', '0612002016');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (46434, 24592, 'Mouad Haidour', 'G541458', 'Baccalauréat', '2003-01-18', '0712002017');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (46463, 24592, 'Saad Belfquih', 'G850142', 'Baccalauréat', '2001-11-09', '0612002018');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (47930, 24592, 'Hicham Nachat', 'U401965', 'Baccalauréat', '1999-07-26', '0712002019');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (48427, 24592, 'Younes Benomar', 'BH569816', 'Baccalauréat', '2002-09-15', '0612002020');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (51347, 24592, 'Imane Assimi', 'U340608', 'Baccalauréat', '2000-02-07', '0712002021');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (52504, 24592, 'Hajar Rouissi', 'G356159', 'Baccalauréat', '2001-10-24', '0612002022');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (54118, 46048, 'Fatima Ait Hmad', 'AZ148465', 'Brevet', '2000-05-16', '0612003001');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (54597, 46048, 'Malika Bouchikhi', 'G482366', 'Brevet', '2001-02-09', '0712003002');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (55082, 46048, 'Naoual El Azzouzi', 'U299468', 'Brevet', '1999-08-27', '0612003003');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (56566, 46048, 'Karima Lafraoui', 'AZ443699', 'Brevet', '2002-11-03', '0712003004');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (57052, 46048, 'Bouchra Cherradi', 'U577738', 'Brevet', '2001-04-14', '0612003005');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (57400, 46048, 'Hanane Ouarga', 'G214895', 'Brevet', '2000-09-21', '0612003006');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (57819, 46048, 'Saida El Fassi', 'AZ343320', 'Brevet', '2003-01-30', '0712003007');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (58520, 46048, 'Mariame Tahiri', 'AZ379176', 'Brevet', '1998-06-12', '0612003008');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (59615, 46048, 'Touria Benghazi', 'U936763', 'Brevet', '2002-03-05', '0712003009');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (59735, 46048, 'Nadia Jabri', 'AZ016328', 'Brevet', '2001-07-18', '0612003010');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (59797, 46048, 'Hayat El Mourabit', 'G083172', 'Brevet', '2000-12-28', '0612003011');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (59823, 46048, 'Salma Rachidi', 'G889579', 'Brevet', '2003-05-07', '0712003012');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (62581, 46048, 'Wafae Bensoussan', 'BH868727', 'Brevet', '1999-10-16', '0612003013');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (64987, 46048, 'Latifa Talsint', 'G434873', 'Brevet', '2001-08-31', '0712003014');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (65392, 46048, 'Mouna Amaymou', 'AZ714345', 'Brevet', '2002-02-20', '0612003015');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (68878, 46048, 'Aicha El Khattabi', 'AZ812236', 'Brevet', '2000-04-11', '0612003016');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (69429, 46048, 'Kenza Lahlou', 'BH231665', 'Brevet', '2003-09-04', '0712003017');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (70217, 46048, 'Chaimae Sedki', 'G760366', 'Brevet', '2001-06-23', '0612003018');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (70589, 46048, 'Hind Benmokhtar', 'U096705', 'Brevet', '1998-11-10', '0712003019');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (76237, 46048, 'Asmaa Bouzid', 'AZ668893', 'Brevet', '2002-07-01', '0612003020');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (80010, 93810, 'Adam Guennoun', 'G346706', 'Baccalauréat', '2001-05-12', '0612004001');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (80284, 93810, 'Kenza Mezzour', 'AZ627298', 'Baccalauréat', '2001-10-08', '0712004002');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (81426, 93810, 'Mohammed Alkettani', 'AZ699016', 'Baccalauréat', '2000-03-25', '0612004003');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (82357, 93810, 'Mohamed Rayane Benjelloun', 'AZ720465', 'Baccalauréat', '2001-07-19', '0712004004');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (83000, 93810, 'Salma El Harrak', 'AZ755646', 'Baccalauréat', '2002-01-06', '0612004005');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (83563, 93810, 'Yassine Lahnaoui', 'AZ170805', 'Baccalauréat', '2000-09-14', '0612004006');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (85674, 93810, 'Oussama Benkirane', 'AZ100330', 'Baccalauréat', '2001-12-03', '0712004007');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (87236, 93810, 'Ghita Jaouhari', 'U232719', 'Baccalauréat', '2002-06-28', '0612004008');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (88907, 93810, 'Ayoub El Maarouf', 'AZ745299', 'Baccalauréat', '1999-04-17', '0712004009');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (89131, 93810, 'Nour El Houda Zaki', 'BH124190', 'Baccalauréat', '2002-08-22', '0612004010');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (89840, 93810, 'Abdelkader Bertal', 'AZ966319', 'Baccalauréat', '2000-11-11', '0612004011');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (91070, 93810, 'Safaa Oueld Mohand', 'BH314919', 'Baccalauréat', '2001-03-30', '0712004012');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (92397, 93810, 'Ibrahim Ait Benhaddou', 'AZ586518', 'Baccalauréat', '1998-07-09', '0612004013');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (93227, 93810, 'Mariam Tachfine', 'U506716', 'Baccalauréat', '2003-02-14', '0712004014');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (93320, 93810, 'Zakaria Hasbane', 'AZ726284', 'Baccalauréat', '2001-10-27', '0612004015');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (93886, 93810, 'Rania Bennis', 'U877694', 'Baccalauréat', '2002-05-03', '0612004016');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (94939, 93810, 'Yassir Mokhtari', 'AZ314737', 'Baccalauréat', '2000-08-19', '0712004017');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (95181, 93810, 'Houda Elazizi', 'U965075', 'Baccalauréat', '2001-01-31', '0612004018');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (96673, 93810, 'Walid Chafai', 'AZ735454', 'Baccalauréat', '2003-04-08', '0712004019');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (97841, 93810, 'Meriem Bousfiha', 'U480831', 'Baccalauréat', '2000-06-24', '0612004020');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (99593, 93810, 'Hamid El Baz', 'AZ678377', 'Baccalauréat', '2002-12-16', '0712004021');
INSERT INTO students (id, group_id, full_name, cin, qualifications, date_of_birth, phone) VALUES (99733, 93810, 'Hiba Lachkar', 'G014363', 'Baccalauréat', '2001-09-05', '0612004022');

-- ======================== GRADES ========================
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (10851, 10425, 14.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (10851, 11504, 15.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (13478, 10425, 13.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (13478, 11504, 12.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (14207, 10425, 17.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (14207, 11504, 16.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (15695, 10425, 11.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (15695, 11504, 12.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (16006, 10425, 15.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (16006, 11504, 14.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (17331, 10425, 16.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (17331, 11504, 18.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (19116, 10425, 10.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (19116, 11504, 11.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (19358, 10425, 13.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (19358, 11504, 14.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (20328, 10425, 12.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (20328, 11504, 13.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (20458, 10425, 18.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (20458, 11504, 19.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (10851, 16175, 14.0, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (10851, 18326, 15.5, '2025-01-22 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (13478, 16175, 12.5, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (13478, 18326, 13.0, '2025-01-22 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (14207, 16175, 16.0, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (14207, 18326, 17.5, '2025-01-22 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (15695, 16175, 11.0, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (15695, 18326, 10.5, '2025-01-22 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (16006, 16175, 15.0, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (16006, 18326, 14.5, '2025-01-22 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (35203, 30730, 13.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (35203, 30969, 14.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (36062, 30730, 15.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (36062, 30969, 16.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (37460, 30730, 12.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (37460, 30969, 11.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (38221, 30730, 17.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (38221, 30969, 18.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (38657, 30730, 10.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (38657, 30969, 11.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (38785, 30730, 14.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (38785, 30969, 15.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (38893, 30730, 16.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (38893, 30969, 17.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (39871, 30730, 13.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (39871, 30969, 12.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (40021, 30730, 11.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (40021, 30969, 12.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (40495, 30730, 15.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (40495, 30969, 14.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (54118, 60019, 14.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (54118, 60432, 15.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (54597, 60019, 16.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (54597, 60432, 17.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (55082, 60019, 12.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (55082, 60432, 11.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (56566, 60019, 13.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (56566, 60432, 14.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (57052, 60019, 18.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (57052, 60432, 19.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (57400, 60019, 10.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (57400, 60432, 11.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (57819, 60019, 15.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (57819, 60432, 16.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (58520, 60019, 13.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (58520, 60432, 12.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (59615, 60019, 17.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (59615, 60432, 18.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (59735, 60019, 14.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (59735, 60432, 15.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (80010, 80381, 16.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (80010, 82512, 17.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (80284, 80381, 15.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (80284, 82512, 16.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (81426, 80381, 14.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (81426, 82512, 13.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (82357, 80381, 17.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (82357, 82512, 18.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (83000, 80381, 12.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (83000, 82512, 13.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (83563, 80381, 11.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (83563, 82512, 12.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (85674, 80381, 15.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (85674, 82512, 14.5, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (87236, 80381, 18.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (87236, 82512, 19.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (88907, 80381, 13.0, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (88907, 82512, 14.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (89131, 80381, 10.5, '2024-11-16 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (89131, 82512, 11.0, '2025-01-21 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (80010, 88104, 15.0, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (80010, 88172, 16.5, '2025-01-22 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (80284, 88104, 14.5, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (80284, 88172, 15.0, '2025-01-22 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (81426, 88104, 13.0, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (81426, 88172, 12.5, '2025-01-22 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (82357, 88104, 17.5, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (82357, 88172, 18.0, '2025-01-22 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (83000, 88104, 11.5, '2024-11-17 10:00:00');
INSERT INTO grades (student_id, assessment_id, score, recorded_at) VALUES (83000, 88172, 12.0, '2025-01-22 10:00:00');

-- ======================== ABSENCES ========================
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (14207, 18675, '2024-10-08', true, 'Maladie (certificat médical fourni)', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (19116, 28301, '2024-10-15', false, NULL, 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (22676, 18675, '2024-11-05', true, 'Raison familiale', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (16006, 28726, '2024-10-22', false, NULL, 80697);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (20328, 37653, '2024-11-12', true, 'Rendez-vous médical', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (26361, 28301, '2024-10-29', false, NULL, 80697);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (13478, 37869, '2024-11-19', true, 'Intempéries (route coupée)', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (31319, 18675, '2024-10-08', false, NULL, 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (36062, 42325, '2024-10-10', false, NULL, 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (38657, 44438, '2024-10-17', true, 'Maladie', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (39871, 44718, '2024-11-07', false, NULL, 80697);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (40512, 45093, '2024-10-24', true, 'Raison familiale', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (45382, 51245, '2024-11-14', false, NULL, 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (46463, 42325, '2024-11-21', true, 'Rendez-vous médical', 80697);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (48427, 44438, '2024-10-31', false, NULL, 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (54597, 61856, '2024-10-09', true, 'Maladie', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (57400, 62350, '2024-10-16', false, NULL, 80697);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (59735, 66155, '2024-11-06', true, 'Raison familiale', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (62581, 61856, '2024-10-23', false, NULL, 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (69429, 70142, '2024-11-13', true, 'Rendez-vous médical', 80697);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (70589, 62350, '2024-11-20', false, NULL, 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (81426, 83579, '2024-10-11', false, NULL, 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (83563, 84341, '2024-10-18', true, 'Maladie (certificat médical fourni)', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (88907, 86484, '2024-11-08', false, NULL, 80697);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (91070, 86622, '2024-10-25', true, 'Intempéries', 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (93320, 83579, '2024-11-15', false, NULL, 79514);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (95181, 84341, '2024-11-22', true, 'Raison familiale', 80697);
INSERT INTO absences (student_id, subject_id, date, justified, reason, recorded_by) VALUES (99593, 86484, '2024-10-18', false, NULL, 79514);

-- ======================== RESET SEQUENCES ========================
SELECT setval('groups_id_seq',      93810);
SELECT setval('teachers_id_seq',    98696);
SELECT setval('subjects_id_seq',    95909);
SELECT setval('assessments_id_seq', 99353);
SELECT setval('students_id_seq',    99733);
SELECT setval('admins_id_seq',      80697);
