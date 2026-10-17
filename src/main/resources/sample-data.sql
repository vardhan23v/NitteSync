-- ---------------------------------------------------------------------------
-- NitteSync sample data
--
-- Run it AFTER schema.sql:
--   mysql -u root -p < src/main/resources/sample-data.sql
--
-- All five demo accounts use the same password:  password
-- The password_hash column stores the SHA-256 hex digest of that password.
-- Event dates are in 2027 so that the "event date cannot be in the past"
-- rule stays satisfied while you demonstrate the project.
-- ---------------------------------------------------------------------------

USE nitte_sync;

DELETE FROM registrations;
DELETE FROM events;
DELETE FROM users;

ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE events AUTO_INCREMENT = 1;
ALTER TABLE registrations AUTO_INCREMENT = 1;

-- password_hash below = SHA-256("password")
INSERT INTO users (name, email, password_hash, role) VALUES
('Admin User',    'admin@nitte.edu.in',         '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'ADMIN'),
('Coding Club',   'coding.club@nitte.edu.in',   '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'CLUB'),
('Cultural Club', 'cultural.club@nitte.edu.in', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'CLUB'),
('Rahul Shetty',  'rahul@nitte.edu.in',         '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'STUDENT'),
('Ananya Rao',    'ananya@nitte.edu.in',        '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'STUDENT');

INSERT INTO events (title, description, venue, event_date, organizer_id, status) VALUES
('Hackathon 2027',
 'A 12-hour coding marathon for teams of three. Bring your laptop and your ideas.',
 'CS Seminar Hall', '2027-01-22', 2, 'APPROVED'),
('Code Sprint Workshop',
 'Hands-on workshop on Java and JDBC: build a small CRUD application from scratch.',
 'Lab 3, CSE Block', '2027-02-12', 2, 'APPROVED'),
('Cultural Fest Auditions',
 'Auditions for dance, music and drama performances for the annual cultural fest.',
 'Open Air Theatre', '2027-03-05', 3, 'PENDING'),
('Photography Walk',
 'A guided photo walk around the campus. Open to beginners; bring any camera.',
 'Nitte Campus Grounds', '2027-03-20', 3, 'DRAFT'),
('Quiz Night',
 'General knowledge quiz for teams of two. Prizes for the top three teams.',
 'Main Auditorium', '2027-02-28', 2, 'REJECTED');

INSERT INTO registrations (student_id, event_id, registration_date) VALUES
(4, 1, '2027-01-05 10:15:00'),
(4, 2, '2027-01-06 09:30:00'),
(5, 1, '2027-01-07 14:45:00');