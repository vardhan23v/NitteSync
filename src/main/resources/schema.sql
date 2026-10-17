-- ---------------------------------------------------------------------------
-- NitteSync database schema
--
-- Run it with:
--   mysql -u root -p < src/main/resources/schema.sql
--
-- It creates the database, then the three tables used by the application.
-- WARNING: the DROP TABLE statements below delete existing NitteSync data.
-- ---------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS nitte_sync
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE nitte_sync;

DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;

-- Users of the system: students, clubs and administrators.
CREATE TABLE users (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(100) NOT NULL,
  email         VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(64)  NOT NULL,                                       -- SHA-256 hex digest (64 characters)
  role          ENUM('STUDENT','CLUB','ADMIN') NOT NULL DEFAULT 'STUDENT',
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Events created by clubs. organizer_id points at the club that created it.
CREATE TABLE events (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  title        VARCHAR(150) NOT NULL,
  description  TEXT,
  venue        VARCHAR(150) NOT NULL,
  event_date   DATE NOT NULL,
  organizer_id INT NOT NULL,
  status       ENUM('DRAFT','PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'DRAFT',
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_events_organizer FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_events_status (status)
) ENGINE=InnoDB;

-- Student registrations. The UNIQUE constraint makes a duplicate
-- registration for the same student and event impossible.
CREATE TABLE registrations (
  id                INT AUTO_INCREMENT PRIMARY KEY,
  student_id        INT NOT NULL,
  event_id          INT NOT NULL,
  registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reg_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_reg_event   FOREIGN KEY (event_id)   REFERENCES events(id) ON DELETE CASCADE,
  CONSTRAINT uq_student_event UNIQUE (student_id, event_id)
) ENGINE=InnoDB;