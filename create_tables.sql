-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema dbapp
-- -----------------------------------------------------

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
  `price` DECIMAL(7,2) NOT NULL,
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
  `availableSlots` INT NULL,
  INDEX `fk_Schedule_Section_Section1_idx` (`sectionID` ASC) VISIBLE,
  INDEX `fk_Schedule_Section_Schedules1_idx` (`scheduleID` ASC) VISIBLE,
  PRIMARY KEY (`scheduleID`, `sectionID`),
  UNIQUE INDEX `scheduleID_UNIQUE` (`scheduleID` ASC) VISIBLE,
  UNIQUE INDEX `sectionID_UNIQUE` (`sectionID` ASC) VISIBLE,
  CONSTRAINT `fk_Schedule_Section_Section1`
    FOREIGN KEY (`sectionID`)
    REFERENCES `dbapp`.`Section` (`sectionID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Schedule_Section_Schedules1`
    FOREIGN KEY (`scheduleID`)
    REFERENCES `dbapp`.`Schedules` (`scheduleID`)
    ON DELETE NO ACTION
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
  `customerID` INT NOT NULL,
  `eventID` INT NOT NULL,
  `merchandiseID` INT NOT NULL,
  `quantity` INT NOT NULL,
  `totalprice` DECIMAL(7,2) NOT NULL,
  `receiptID` INT NOT NULL AUTO_INCREMENT,
  INDEX `fk_Merch_Receipt_Customers1_idx` (`customerID` ASC) VISIBLE,
  INDEX `fk_Merch_Receipt_Event_Merch1_idx` (`eventID` ASC, `merchandiseID` ASC) VISIBLE,
  PRIMARY KEY (`receiptID`),
  UNIQUE INDEX `receiptID_UNIQUE` (`receiptID` ASC) VISIBLE,
  CONSTRAINT `fk_Merch_Receipt_Customers1`
    FOREIGN KEY (`customerID`)
    REFERENCES `dbapp`.`Customers` (`customerID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Merch_Receipt_Event_Merch1`
    FOREIGN KEY (`eventID` , `merchandiseID`)
    REFERENCES `dbapp`.`Event_Merch` (`eventID` , `merchandiseID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
