-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema dbapp
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dbapp` DEFAULT CHARACTER SET utf8 ;
USE `dbapp` ;

-- -----------------------------------------------------
-- Table `dbapp`.`Events`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbapp`.`Events` ;

CREATE TABLE IF NOT EXISTS `dbapp`.`Events` (
  `eventID` INT NOT NULL AUTO_INCREMENT,
  `eventname` VARCHAR(50) NOT NULL,
  `eventtype` VARCHAR(20) NOT NULL,
  `bookingfee` DECIMAL(7,2) NOT NULL,
  PRIMARY KEY (`eventID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbapp`.`Merchandise`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbapp`.`Merchandise` ;

CREATE TABLE IF NOT EXISTS `dbapp`.`Merchandise` (
  `merchandiseID` INT NOT NULL AUTO_INCREMENT,
  `merchandisename` VARCHAR(50) NOT NULL,
  `category` ENUM('Clothing', 'Collectible', 'Music', 'Accessory') NOT NULL,
  `price` DECIMAL(7,2) NOT NULL,
  `stock` INT NOT NULL,
  PRIMARY KEY (`merchandiseID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbapp`.`Section`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbapp`.`Section` ;

CREATE TABLE IF NOT EXISTS `dbapp`.`Section` (
  `sectionID` INT NOT NULL AUTO_INCREMENT,
  `sectionname` VARCHAR(10) NOT NULL,
  `capacity` INT NOT NULL,
  PRIMARY KEY (`sectionID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbapp`.`Event_Merch`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbapp`.`Event_Merch` ;

CREATE TABLE IF NOT EXISTS `dbapp`.`Event_Merch` (
  `eventID` INT NOT NULL,
  `merchandiseID` INT NOT NULL,
  `merchtype` ENUM('Package', 'Addon') NOT NULL,
  PRIMARY KEY (`eventID`, `merchandiseID`),
  INDEX `fk_Events_has_Merchandise_Merchandise1_idx` (`merchandiseID` ASC) VISIBLE,
  INDEX `fk_Events_has_Merchandise_Events_idx` (`eventID` ASC) VISIBLE,
  CONSTRAINT `fk_Events_has_Merchandise_Events`
    FOREIGN KEY (`eventID`)
    REFERENCES `dbapp`.`Events` (`eventID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Events_has_Merchandise_Merchandise1`
    FOREIGN KEY (`merchandiseID`)
    REFERENCES `dbapp`.`Merchandise` (`merchandiseID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbapp`.`Schedules`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbapp`.`Schedules` ;

CREATE TABLE IF NOT EXISTS `dbapp`.`Schedules` (
  `eventID` INT NOT NULL,
  `scheduleID` INT NOT NULL AUTO_INCREMENT,
  `scheduleDate` DATE NOT NULL,
  `startTime` TIME NOT NULL,
  `endTime` TIME NOT NULL,
  INDEX `fk_Schedules_Events1_idx` (`eventID` ASC) VISIBLE,
  PRIMARY KEY (`scheduleID`),
  CONSTRAINT `fk_Schedules_Events1`
    FOREIGN KEY (`eventID`)
    REFERENCES `dbapp`.`Events` (`eventID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbapp`.`Customers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbapp`.`Customers` ;

CREATE TABLE IF NOT EXISTS `dbapp`.`Customers` (
  `customerID` INT NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(50) NULL,
  `lastName` VARCHAR(50) NULL,
  `email` VARCHAR(100) NULL,
  `phoneNumber` VARCHAR(12) NULL,
  `balance` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`customerID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbapp`.`Schedule_Section`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbapp`.`Schedule_Section` ;

CREATE TABLE IF NOT EXISTS `dbapp`.`Schedule_Section` (
  `scheduleID` INT NOT NULL,
  `sectionID` INT NOT NULL,
  `availableSlots` INT NOT NULL,
  `price` DECIMAL(7,2) NOT NULL,
  INDEX `fk_Schedule_Section_Section1_idx` (`sectionID` ASC) VISIBLE,
  INDEX `fk_Schedule_Section_Schedules1_idx` (`scheduleID` ASC) VISIBLE,
  PRIMARY KEY (`scheduleID`, `sectionID`),
  -- UNIQUE INDEX `scheduleID_UNIQUE` (`scheduleID` ASC) VISIBLE,
  -- UNIQUE INDEX `sectionID_UNIQUE` (`sectionID` ASC) VISIBLE,
  CONSTRAINT `fk_Schedule_Section_Section1`
    FOREIGN KEY (`sectionID`)
    REFERENCES `dbapp`.`Section` (`sectionID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Schedule_Section_Schedules1`
    FOREIGN KEY (`scheduleID`)
    REFERENCES `dbapp`.`Schedules` (`scheduleID`)
    ON DELETE CASCADE -- try cascade if not work
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbapp`.`Tickets`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbapp`.`Tickets` ;

CREATE TABLE IF NOT EXISTS `dbapp`.`Tickets` (
  `ticketID` INT NOT NULL AUTO_INCREMENT,
  `customerID` INT NOT NULL,
  `scheduleID` INT NOT NULL,
  `sectionID` INT NOT NULL,
  `purchaseDate` DATETIME NOT NULL,
  `ticketPrice` DECIMAL(7,2) NOT NULL,
  `status` ENUM('P', 'CO', 'CA') NOT NULL COMMENT 'P = Pending\nCO = Confirmed\nCA = Cancelled',
  PRIMARY KEY (`ticketID`),
  INDEX `fk_Tickets_Customers1_idx` (`customerID` ASC) VISIBLE,
  INDEX `fk_Tickets_Schedule_Section1_idx` (`scheduleID` ASC, `sectionID` ASC) VISIBLE,
  CONSTRAINT `fk_Tickets_Customers1`
    FOREIGN KEY (`customerID`)
    REFERENCES `dbapp`.`Customers` (`customerID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Tickets_Schedule_Section1`
    FOREIGN KEY (`scheduleID` , `sectionID`)
    REFERENCES `dbapp`.`Schedule_Section` (`scheduleID` , `sectionID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbapp`.`Merch_Receipt`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbapp`.`Merch_Receipt` ;

CREATE TABLE IF NOT EXISTS `dbapp`.`Merch_Receipt` (
  `ticketID` INT NULL,
  `customerID` INT NOT NULL,
  `eventID` INT NOT NULL,
  `merchandiseID` INT NOT NULL,
  `quantity` INT NOT NULL,
  `totalprice` DECIMAL(7,2) NOT NULL,
  `receiptID` INT NOT NULL AUTO_INCREMENT,
  `purchaseDate` DATE NOT NULL,
  INDEX `fk_Merch_Receipt_Customers1_idx` (`customerID` ASC) VISIBLE,
  INDEX `fk_Merch_Receipt_Event_Merch1_idx` (`eventID` ASC, `merchandiseID` ASC) VISIBLE,
  PRIMARY KEY (`receiptID`),
  UNIQUE INDEX `receiptID_UNIQUE` (`receiptID` ASC) VISIBLE,
  INDEX `fk_Merch_Receipt_Tickets1_idx` (`ticketID` ASC) VISIBLE,
  CONSTRAINT `fk_Merch_Receipt_Customers1`
    FOREIGN KEY (`customerID`)
    REFERENCES `dbapp`.`Customers` (`customerID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Merch_Receipt_Event_Merch1`
    FOREIGN KEY (`eventID` , `merchandiseID`)
    REFERENCES `dbapp`.`Event_Merch` (`eventID` , `merchandiseID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Merch_Receipt_Tickets1`
    FOREIGN KEY (`ticketID`)
    REFERENCES `dbapp`.`Tickets` (`ticketID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

USE dbapp;

-- ===============================
-- CUSTOMER TABLE
-- ===============================
INSERT INTO customers (customerID, firstName, lastName, email, phoneNumber, balance)
VALUES
(1000, 'Aldrich', 'Gencianeo', 'aldrich_gencianeo@dlsu.edu.ph', '09171234567', 8500.00),
(1001, 'Eoghan', 'Follero', 'eoghan_follero@dlsu.edu.ph', '09182345678', 9000.00),
(1002, 'Keila', 'Geslani', 'keila_geslani@dlsu.edu.ph', '09193456789', 10500.00),
(1003, 'Lara', 'Santos', 'lara_santos@gmail.com', '09204567890', 8000.00),
(1004, 'Miguel', 'Tan', 'miguel_tan@gmail.com', '09215678901', 11000.00),
(1005, 'Napoleon', null, null, null, 10000.00),
(1006, 'Muhammad', null, null, null, 5500.00),
(1007, 'Abraham', 'Lincoln', 'a_lincoln@gmail.com', null, 14500.00),
(1008, 'George', 'Washington', 'g_washington@gmail.com', null, 5000.00),
(1009, 'Aristotle', null, null, null, 14000.00);

-- ===============================
-- EVENT TABLE
-- ===============================
INSERT INTO events (eventID, eventname, eventtype, bookingfee)
VALUES
(2000, 'Harmony Nights', 'Concert', 9000.00),
(2001, 'Phantom of the Opera', 'Theater', 7000.00),
(2002, 'RockFest 2025', 'Concert', 1500.00),
(2003, 'Broadway Reimagined', 'Theater', 3000.00),
(2004, 'Symphony of Lights', 'Concert', 5000.00),
(2005, 'Elvis Comeback Special', 'Concert', 9500.00),
(2006, "You Can't Take It With You", 'Theater', 2500.00),
(2007, 'Cream Farewell Tour', 'Concert', 9000.00),
(2008, 'Arsenic and Old Lace', 'Theater', 4000.00),
(2009, 'Led Zeppelin World Tour', 'Concert', 7500.00);

-- ===============================
-- VENUE & SECTION TABLE
-- ===============================
INSERT INTO section (sectionID, sectionname, capacity)
VALUES
(3000, 'VIP', 100),
(3001, 'Regular', 400),
(3002, 'Balcony', 300),
(3003, 'Upper Box', 600);

-- ===============================
-- MERCHANDISE TABLE
-- ===============================
INSERT INTO merchandise (merchandiseID, merchandisename, category, price, stock)
VALUES
(4000, 'Official Event T-Shirt', 'Clothing', 750.00, 150),
(4001, 'Event Poster', 'Collectible', 250.00, 200),
(4002, 'VIP Lanyard Pass', 'Accessory', 500.00, 100),
(4003, 'Signed Album', 'Music', 1200.00, 50),
(4004, 'Light Stick', 'Accessory', 900.00, 80),
(4005, 'Event Tote Bag', 'Clothing', 400.00, 120);

-- ===============================
-- SCHEDULES TABLE
-- ===============================
INSERT INTO schedules (scheduleID, eventID, scheduleDate, startTime, endTime)
VALUES
(5000, 2000, '2025-12-15', '19:00:00', '22:00:00'),
(5001, 2000, '2025-12-16', '19:00:00', '22:00:00'),
(5002, 2001, '2025-12-20', '20:00:00', '23:00:00'),
(5003, 2002, '2025-11-25', '18:00:00', '23:00:00'),
(5004, 2003, '2025-12-10', '19:30:00', '22:30:00'),
(5005, 2009, '2023-06-15', '18:00:00', '21:00:00'),
(5006, 2005, '2024-08-20', '19:00:00', '22:00:00'),
(5007, 2007, '2025-02-14', '20:00:00', '23:00:00');

-- ===============================
-- SCHEDULE_SECTION TABLE
-- ===============================
INSERT INTO schedule_section (scheduleID, sectionID, availableSlots, price)
VALUES
(5000, 3000, 100, 500.00),
(5000, 3001, 400, 250.00),
(5001, 3000, 100, 500.00),
(5001, 3001, 400, 250.00),
(5002, 3002, 150, 180.00),
(5002, 3003, 300, 120.00),
(5003, 3001, 200, 150.00),
(5004, 3000, 0, 500.00),
(5004, 3003, 600, 300.00),
(5005, 3000, 50, 1000.00),
(5005, 3001, 200, 500.00),
(5006, 3000, 80, 1200.00),
(5006, 3002, 150, 600.00),
(5007, 3001, 300, 400.00),
(5007, 3003, 500, 200.00);

-- ===============================
-- EVENT_MERCH TABLE
-- ===============================
INSERT INTO event_merch (eventID, merchandiseID, merchtype)
VALUES
-- Harmony Nights (Concert) - Event 2000
(2000, 4000, 'Package'),   -- T-Shirt as Package
(2000, 4001, 'Addon'),     -- Poster as Addon
(2000, 4003, 'Package'),   -- Signed Album as Package
(2000, 4004, 'Addon'),     -- Light Stick as Addon

-- Phantom of the Opera (Theater) - Event 2001
(2001, 4000, 'Package'),   -- T-Shirt as Package
(2001, 4001, 'Addon'),     -- Poster as Addon
(2001, 4005, 'Addon'),     -- Tote Bag as Addon

-- RockFest 2025 (Concert) - Event 2002
(2002, 4000, 'Package'),   -- T-Shirt as Package
(2002, 4003, 'Package'),   -- Signed Album as Package
(2002, 4004, 'Addon'),     -- Light Stick as Addon

-- Broadway Reimagined (Theater) - Event 2003
(2003, 4001, 'Addon'),     -- Poster as Addon
(2003, 4002, 'Package'),   -- VIP Lanyard as Package
(2003, 4005, 'Addon'),     -- Tote Bag as Addon

-- Symphony of Lights (Concert) - Event 2004
(2004, 4000, 'Package'),   -- T-Shirt as Package
(2004, 4004, 'Addon'),     -- Light Stick as Addon

-- Elvis Comeback Special (Concert) - Event 2005
(2005, 4000, 'Package'),   -- T-Shirt as Package
(2005, 4003, 'Package'),   -- Signed Album as Package

-- You Can't Take It With You (Theater) - Event 2006
(2006, 4001, 'Addon'),     -- Poster as Addon
(2006, 4005, 'Addon'),     -- Tote Bag as Addon

-- Cream Farewell Tour (Concert) - Event 2007
(2007, 4000, 'Package'),   -- T-Shirt as Package
(2007, 4003, 'Package'),   -- Signed Album as Package

-- Arsenic and Old Lace (Theater) - Event 2008
(2008, 4001, 'Addon'),     -- Poster as Addon
(2008, 4002, 'Package'),   -- VIP Lanyard as Package

-- Led Zeppelin World Tour (Concert) - Event 2009
(2009, 4000, 'Package'),   -- T-Shirt as Package
(2009, 4003, 'Package'),   -- Signed Album as Package
(2009, 4004, 'Addon');     -- Light Stick as Addon

-- ===============================
-- MERCH_RECEIPTS TABLE
-- ===============================
INSERT INTO merch_receipt (ticketID, customerID, eventID, merchandiseID, quantity, totalprice, purchaseDate)
VALUES
-- Event 2000 (Harmony Nights)
(NULL, 1000, 2000, 4000, 2, 1500.00, '2025-12-15'),  -- 2 T-Shirts
(NULL, 1001, 2000, 4001, 1, 250.00, '2025-12-15'),   -- 1 Poster
(NULL, 1002, 2000, 4003, 1, 1200.00, '2025-12-15'),  -- 1 Signed 
(NULL, 1007, 2000, 4000, 1, 750.00, '2025-12-16'),   
(NULL, 1007, 2000, 4004, 2, 1800.00, '2025-12-16'),  
(NULL, 1008, 2000, 4001, 3, 750.00, '2025-12-16'),   

-- Event 2001 (Phantom of the Opera)
(NULL, 1003, 2001, 4000, 1, 750.00, '2025-12-20'),    -- 1 T-Shirt
(NULL, 1004, 2001, 4005, 2, 800.00, '2025-12-20'),   -- 2 Tote Bags

-- Event 2002 (RockFest 2025)
(NULL, 1005, 2002, 4000, 3, 2250.00, '2025-11-25'),  -- 3 T-Shirts
(NULL, 1006, 2002, 4004, 1, 900.00, '2025-11-25'),   -- 1 Light Stick
(NULL, 1009, 2002, 4003, 1, 1200.00, '2025-11-25'),  
(NULL, 1000, 2002, 4004, 1, 900.00, '2025-11-25'),   
(NULL, 1001, 2002, 4000, 2, 1500.00, '2025-11-25'),  

-- Event 2003 (Broadway Reimagined)
(NULL, 1002, 2003, 4002, 1, 500.00, '2025-12-10'),   
(NULL, 1003, 2003, 4005, 1, 400.00, '2025-12-10'),   

-- Event 2004 (Symphony of Lights)
(NULL, 1004, 2004, 4000, 2, 1500.00, '2025-12-12'),  
(NULL, 1005, 2004, 4004, 1, 900.00, '2025-12-12'),   

-- Event 2001 (Phantom of the Opera)
(NULL, 1006, 2001, 4000, 1, 750.00, '2024-11-20'),   
(NULL, 1007, 2001, 4001, 2, 500.00, '2024-11-20'),   
(NULL, 1008, 2001, 4005, 1, 400.00, '2024-11-20'),   

-- Event 2005 (Elvis Comeback Special)
(NULL, 1000, 2005, 4000, 1, 750.00, '2023-01-15'),   
(NULL, 1001, 2005, 4003, 2, 2400.00, '2023-01-15'),  

-- Event 2009 (Led Zeppelin World Tour)
(NULL, 1002, 2009, 4000, 3, 2250.00, '2022-03-10'),   
(NULL, 1003, 2009, 4003, 1, 1200.00, '2022-03-10'),   
(NULL, 1004, 2009, 4004, 2, 1800.00, '2022-03-10'); 

-- ===============================
-- TICKETS_TABLE
-- ===============================
INSERT INTO tickets (customerID, scheduleID, sectionID, purchaseDate, ticketPrice, status)
VALUES
(1000, 5005, 3000, '2023-05-01 10:00:00', 1000.00, 'CO'),
(1001, 5005, 3000, '2023-05-02 11:30:00', 1000.00, 'CO'),
(1002, 5005, 3001, '2023-06-01 09:00:00', 500.00, 'CO'),
(1003, 5005, 3001, '2023-06-10 14:15:00', 500.00, 'CA'),
(1004, 5006, 3000, '2024-07-15 08:00:00', 1200.00, 'CO'),
(1005, 5006, 3002, '2024-08-01 16:45:00', 600.00, 'CO'),
(1006, 5006, 3002, '2024-08-05 12:00:00', 600.00, 'CO'),
(1007, 5007, 3001, '2025-01-10 09:30:00', 400.00, 'CO'),
(1008, 5007, 3003, '2025-01-15 10:00:00', 200.00, 'CO'),
(1009, 5007, 3003, '2025-02-01 13:20:00', 200.00, 'CO'),
(1000, 5007, 3001, '2025-02-10 15:00:00', 400.00, 'P'),
(1001, 5000, 3000, '2025-11-01 10:00:00', 500.00, 'CO'),
(1002, 5000, 3001, '2025-11-05 11:00:00', 250.00, 'CO'),
(1003, 5000, 3001, '2025-12-01 09:00:00', 250.00, 'CO'),
(1004, 5000, 3001, '2025-12-02 14:00:00', 250.00, 'CO'),
(1005, 5002, 3002, '2025-12-05 16:00:00', 180.00, 'CO'),
(1006, 5002, 3003, '2025-12-06 17:00:00', 120.00, 'CO'),
(1007, 5002, 3003, '2025-12-07 18:00:00', 120.00, 'CO'),
(1008, 5003, 3001, '2025-10-20 12:00:00', 150.00, 'CO'),
(1009, 5003, 3001, '2025-11-20 13:00:00', 150.00, 'CO'),
(1000, 5003, 3001, '2025-11-21 14:00:00', 150.00, 'CA');