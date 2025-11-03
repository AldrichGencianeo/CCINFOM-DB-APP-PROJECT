-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema dbApp
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema dbApp
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dbApp` DEFAULT CHARACTER SET utf8 ;
USE `dbApp` ;

-- -----------------------------------------------------
-- Table `dbApp`.`Events`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbApp`.`Events` ;

CREATE TABLE IF NOT EXISTS `dbApp`.`Events` (
  `eventID` INT NOT NULL AUTO_INCREMENT,
  `eventname` VARCHAR(50) NOT NULL,
  `eventtype` VARCHAR(20) NOT NULL,
  `bookingfee` DECIMAL(7,2) NOT NULL,
  PRIMARY KEY (`eventID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbApp`.`Merchandise`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbApp`.`Merchandise` ;

CREATE TABLE IF NOT EXISTS `dbApp`.`Merchandise` (
  `merchandiseID` INT NOT NULL AUTO_INCREMENT,
  `merchandisename` VARCHAR(50) NOT NULL,
  `category` VARCHAR(20) NOT NULL,
  `price` DECIMAL(7,2) NOT NULL,
  `qty` INT NOT NULL,
  PRIMARY KEY (`merchandiseID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbApp`.`Venue and Section`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbApp`.`Venue_and_Section` ;

CREATE TABLE IF NOT EXISTS `dbApp`.`Venue_and_Section` (
  `sectionID` INT NOT NULL AUTO_INCREMENT,
  `venuename` VARCHAR(50) NOT NULL,
  `sectionname` VARCHAR(10) NOT NULL,
  `price` DECIMAL(7,2) NOT NULL,
  `availableslots` INT NOT NULL,
  PRIMARY KEY (`sectionID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbApp`.`Event_Merch`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbApp`.`Event_Merch` ;

CREATE TABLE IF NOT EXISTS `dbApp`.`Event_Merch` (
  `eventID` INT NOT NULL,
  `merchandiseID` INT NOT NULL,
  `merchtype` ENUM('Package', 'Addon') NOT NULL,
  PRIMARY KEY (`eventID`, `merchandiseID`),
  INDEX `fk_Events_has_Merchandise_Merchandise1_idx` (`merchandiseID` ASC) VISIBLE,
  INDEX `fk_Events_has_Merchandise_Events_idx` (`eventID` ASC) VISIBLE,
  CONSTRAINT `fk_Events_has_Merchandise_Events`
    FOREIGN KEY (`eventID`)
    REFERENCES `dbApp`.`Events` (`eventID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Events_has_Merchandise_Merchandise1`
    FOREIGN KEY (`merchandiseID`)
    REFERENCES `dbApp`.`Merchandise` (`merchandiseID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbApp`.`Schedules`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbApp`.`Schedules` ;

CREATE TABLE IF NOT EXISTS `dbApp`.`Schedules` (
  `eventID` INT NOT NULL,
  `sectionID` INT NOT NULL,
  `scheduleID` INT NOT NULL AUTO_INCREMENT,
  `scheduleDate` DATE NOT NULL,
  `startTime` TIME NOT NULL,
  `endTime` TIME NOT NULL,
  `status` ENUM('F', 'A') NOT NULL,
  INDEX `fk_Schedules_Events1_idx` (`eventID` ASC) VISIBLE,
  PRIMARY KEY (`scheduleID`),
  INDEX `fk_Schedules_Venue and Section1_idx` (`sectionID` ASC) VISIBLE,
  CONSTRAINT `fk_Schedules_Events1`
    FOREIGN KEY (`eventID`)
    REFERENCES `dbApp`.`Events` (`eventID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Schedules_Venue and Section1`
    FOREIGN KEY (`sectionID`)
    REFERENCES `dbApp`.`Venue and Section` (`sectionID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbApp`.`Customers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbApp`.`Customers` ;

CREATE TABLE IF NOT EXISTS `dbApp`.`Customers` (
  `customerID` INT NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(50) NULL,
  `lastName` VARCHAR(50) NULL,
  `email` VARCHAR(100) NULL,
  `phoneNumber` VARCHAR(12) NULL,
  `balance` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`customerID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbApp`.`Tickets`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbApp`.`Tickets` ;

CREATE TABLE IF NOT EXISTS `dbApp`.`Tickets` (
  `ticketID` INT NOT NULL AUTO_INCREMENT,
  `scheduleID` INT NOT NULL,
  `customerID` INT NOT NULL,
  `purchaseDate` DATETIME NULL,
  `ticketPrice` DECIMAL(7,2) NOT NULL,
  `status` ENUM('P', 'CO', 'CA') NOT NULL,
  PRIMARY KEY (`ticketID`),
  INDEX `fk_Tickets_Schedules1_idx` (`scheduleID` ASC) VISIBLE,
  INDEX `fk_Tickets_Customers1_idx` (`customerID` ASC) VISIBLE,
  CONSTRAINT `fk_Tickets_Schedules1`
    FOREIGN KEY (`scheduleID`)
    REFERENCES `dbApp`.`Schedules` (`scheduleID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Tickets_Customers1`
    FOREIGN KEY (`customerID`)
    REFERENCES `dbApp`.`Customers` (`customerID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dbApp`.`Merch_Receipt`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dbApp`.`Merch_Receipt` ;

CREATE TABLE IF NOT EXISTS `dbApp`.`Merch_Receipt` (
  `customerID` INT NOT NULL,
  `merchandiseID` INT NOT NULL,
  `qty` INT NOT NULL,
  `totalprice` DATE NOT NULL,
  INDEX `fk_Merch_Receipt_Customers1_idx` (`customerID` ASC) VISIBLE,
  PRIMARY KEY (`customerID`, `merchandiseID`),
  INDEX `fk_Merch_Receipt_Merchandise1_idx` (`merchandiseID` ASC) VISIBLE,
  CONSTRAINT `fk_Merch_Receipt_Customers1`
    FOREIGN KEY (`customerID`)
    REFERENCES `dbApp`.`Customers` (`customerID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Merch_Receipt_Merchandise1`
    FOREIGN KEY (`merchandiseID`)
    REFERENCES `dbApp`.`Merchandise` (`merchandiseID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
