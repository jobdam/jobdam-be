-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: jobdam.cbgscymc4lh9.ap-northeast-2.rds.amazonaws.com    Database: jobdam
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `ai_resume_question`
--

DROP TABLE IF EXISTS `ai_resume_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_resume_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL,
  `question` text NOT NULL,
  `interview_type` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_resume_id` (`resume_id`),
  CONSTRAINT `fk_resume_id` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1204 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_verification`
--

DROP TABLE IF EXISTS `email_verification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_verification` (
  `email` varchar(30) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feedback_content`
--

DROP TABLE IF EXISTS `feedback_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback_content` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_user_id` bigint NOT NULL,
  `content` text,
  `interview_question_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `feedback_content_user_id_fk` (`target_user_id`),
  KEY `feedback_content_interview_question_id_fk` (`interview_question_id`),
  CONSTRAINT `feedback_content_interview_question_id_fk` FOREIGN KEY (`interview_question_id`) REFERENCES `interview_question` (`id`),
  CONSTRAINT `feedback_content_user_id_fk` FOREIGN KEY (`target_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=242 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `interview`
--

DROP TABLE IF EXISTS `interview`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interview` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `interview_type` enum('PERSONALITY','JOB','TECHNICAL') NOT NULL,
  `interview_day` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `job_code` varchar(10) NOT NULL,
  `well_done` varchar(200) DEFAULT NULL,
  `to_improve` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `interview_user_id_fk` (`user_id`),
  KEY `interview_job_group_job_code_fk` (`job_code`),
  CONSTRAINT `interview_job_group_job_code_fk` FOREIGN KEY (`job_code`) REFERENCES `job_group` (`job_code`),
  CONSTRAINT `interview_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=487 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `interview_question`
--

DROP TABLE IF EXISTS `interview_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interview_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_id` bigint NOT NULL,
  `context` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `interview_question_interview_id_fk` (`interview_id`),
  CONSTRAINT `interview_question_interview_id_fk` FOREIGN KEY (`interview_id`) REFERENCES `interview` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2067 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job_detail`
--

DROP TABLE IF EXISTS `job_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job_detail` (
  `job_detail_code` varchar(10) NOT NULL,
  `job_detail` varchar(20) DEFAULT NULL,
  `job_code` varchar(10) NOT NULL,
  PRIMARY KEY (`job_detail_code`),
  KEY `fk_job_detail_to_group` (`job_code`),
  CONSTRAINT `fk_job_detail_to_group` FOREIGN KEY (`job_code`) REFERENCES `job_group` (`job_code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job_group`
--

DROP TABLE IF EXISTS `job_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job_group` (
  `job_code` varchar(10) NOT NULL,
  `job_group` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`job_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `refresh_token` varchar(512) DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `refresh_token_user_Id_fk` (`user_id`),
  CONSTRAINT `refresh_token_user_Id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1416 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resume`
--

DROP TABLE IF EXISTS `resume`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resume` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `url` varchar(2048) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resume_pk` (`user_id`),
  CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp_token`
--

DROP TABLE IF EXISTS `temp_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `temp_token` (
  `temp_token_id` varchar(255) NOT NULL,
  `user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`temp_token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(30) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `birthday` timestamp NULL DEFAULT NULL,
  `target_company_size` varchar(10) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `blacklist` char(1) DEFAULT NULL,
  `profile_img_url` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `job_code` varchar(10) DEFAULT NULL,
  `job_detail_code` varchar(10) DEFAULT NULL,
  `experience_type` varchar(12) DEFAULT NULL,
  `education_level` varchar(20) DEFAULT NULL,
  `education_status` varchar(20) DEFAULT NULL,
  `provider_id` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_to_job_group` (`job_code`),
  KEY `fk_user_to_job_detail` (`job_detail_code`),
  CONSTRAINT `fk_user_to_job_detail` FOREIGN KEY (`job_detail_code`) REFERENCES `job_detail` (`job_detail_code`) ON DELETE SET NULL,
  CONSTRAINT `fk_user_to_job_group` FOREIGN KEY (`job_code`) REFERENCES `job_group` (`job_code`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-30 16:52:54
