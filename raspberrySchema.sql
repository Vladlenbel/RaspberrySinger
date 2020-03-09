CREATE DATABASE  IF NOT EXISTS `raspberry_schema`;
USE `raspberry_schema`;
-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: localhost    Database: raspberry_schema
--
-- Table structure for table `check_information`
--

DROP TABLE IF EXISTS `check_information`;
CREATE TABLE `check_information` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `workerCardNum` varchar(50) DEFAULT NULL,
  `idStatus` int(11) DEFAULT NULL,
  `serialNo` int(11) DEFAULT NULL,
  `personalNumber` int(11) DEFAULT NULL,
  `timeCheck` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isUnload` tinyint(1) DEFAULT NULL,
  `uuid` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_check_information_workerCardNum` (`workerCardNum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `workerHourType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `workerHourType` (`workerHourType`),
  CONSTRAINT `department_ibfk_1` FOREIGN KEY (`workerHourType`) REFERENCES `working_hour` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `personalNumber` int(11) DEFAULT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  `isCompleted` int(11) DEFAULT NULL,
  `notificationType` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `settings`
--

DROP TABLE IF EXISTS `settings`;
CREATE TABLE `settings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) DEFAULT NULL,
  `value` varchar(45) DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO settings (code,value,description) VALUES('versionApp','0.0','Версия приложения java приложения');
INSERT INTO settings (code,value,description) VALUES('versionBase','0.0','Версия базы данных');
INSERT INTO settings (code,value,description) VALUES('versionServer','0.0','Версия PHP сервера');

--
-- Table structure for table `sound_info`
--

DROP TABLE IF EXISTS `sound_info`;
CREATE TABLE `sound_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `personalNumber` int(11) DEFAULT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sound_info_personalNumber` (`personalNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `worker_personal_data`
--

DROP TABLE IF EXISTS `worker_personal_data`;
CREATE TABLE `worker_personal_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `personalNumber` int(11) DEFAULT NULL,
  `workerCardNum` bigint(15) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `workerHourId` int(11) DEFAULT NULL,
  `departmentId` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `isAdmin` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_worker_personal_data_workerCardNum` (`workerCardNum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `working_hour`
--

DROP TABLE IF EXISTS `working_hour`;
CREATE TABLE `working_hour` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `startWork` time DEFAULT NULL,
  `finishWork` time DEFAULT NULL,
  `startDinner` time DEFAULT NULL,
  `finishDinner` time DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;