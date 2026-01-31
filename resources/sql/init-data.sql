CREATE DATABASE  IF NOT EXISTS `iims` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `iims`;
-- MySQL dump 10.13  Distrib 8.0.22, for Win64 (x86_64)
--
-- Host: localhost    Database: iims
-- ------------------------------------------------------
-- Server version	8.0.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `iims_ai_agent`
--

DROP TABLE IF EXISTS `iims_ai_agent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_ai_agent` (
  `id` bigint unsigned NOT NULL COMMENT '智能体ID',
  `name` varchar(30) DEFAULT NULL COMMENT '名称',
  `tip` text COMMENT '智能体-提示词',
  `model_id` bigint NOT NULL COMMENT '模型ID',
  `description` varchar(120) DEFAULT NULL COMMENT '智能体-描述',
  `status` tinyint(1) NOT NULL COMMENT '是否禁用：1 禁用、0 正常',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：1 删除、0 保存',
  `is_deletable` tinyint unsigned DEFAULT NULL COMMENT '是否可以被删除：1 可以、0 不可以',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='智能体';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_ai_agent`
--

LOCK TABLES `iims_ai_agent` WRITE;
/*!40000 ALTER TABLE `iims_ai_agent` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_ai_agent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_ai_chat_dialogue`
--

DROP TABLE IF EXISTS `iims_ai_chat_dialogue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_ai_chat_dialogue` (
  `id` bigint unsigned NOT NULL COMMENT '对话ID',
  `last_id` bigint DEFAULT NULL COMMENT '对话记录下一个的ID',
  `topic_id` bigint DEFAULT NULL COMMENT '话题ID',
  `sender` char(9) DEFAULT NULL COMMENT '发送人：user、assistant',
  `content` json DEFAULT NULL COMMENT '对话内容',
  `metadata` json DEFAULT NULL COMMENT '元数据',
  `tools` json DEFAULT NULL COMMENT '使用工具',
  `file_ids` json DEFAULT NULL COMMENT '存放聊天文件IDS',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：1 删除、0 保存',
  `feedback_status` bigint DEFAULT '0' COMMENT '0：无状态、-1：负反馈、1：正反馈',
  `is_star` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否收藏：收藏 1、未收藏 0',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='对话记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_ai_chat_dialogue`
--

LOCK TABLES `iims_ai_chat_dialogue` WRITE;
/*!40000 ALTER TABLE `iims_ai_chat_dialogue` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_ai_chat_dialogue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_ai_chat_models`
--

DROP TABLE IF EXISTS `iims_ai_chat_models`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_ai_chat_models` (
  `id` bigint NOT NULL,
  `rename` varchar(45) DEFAULT NULL COMMENT '重命名',
  `name` varchar(90) NOT NULL COMMENT '模型名称',
  `key` varchar(360) DEFAULT NULL COMMENT '模型调用密钥',
  `token` int DEFAULT NULL COMMENT '模型最大令牌数',
  `type` tinyint DEFAULT NULL COMMENT '模型接口类型：\n1. Agent: 0\n2. OpenAI：1\n3. Ollama：2',
  `url` varchar(360) DEFAULT NULL COMMENT '模型访问地址',
  `description` varchar(120) DEFAULT NULL COMMENT '描述',
  `model_type` bigint DEFAULT NULL COMMENT '模型类型：0 向量化、1 大语言、2 视觉模型、3多模态模型',
  `is_online` int DEFAULT NULL COMMENT '是否在线：0 离线、1 在线',
  `is_deleted` tinyint(1) NOT NULL COMMENT '是否删除',
  `is_deletable` tinyint(1) NOT NULL COMMENT '是否可删除',
  `detection_time` timestamp NULL DEFAULT NULL COMMENT '检测模型连通性时间',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `iims_ai_models_name_uindex` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='模型仓库';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_ai_chat_models`
--

LOCK TABLES `iims_ai_chat_models` WRITE;
/*!40000 ALTER TABLE `iims_ai_chat_models` DISABLE KEYS */;
INSERT INTO `iims_ai_chat_models` VALUES (7,'自主规划智能体','react-agent',NULL,NULL,0,NULL,'能够自主规划任务流程的智能体',3,1,0,0,'2025-11-21 06:52:33',17,'2025-07-27 08:19:58',17,'2025-07-27 08:20:00');
/*!40000 ALTER TABLE `iims_ai_chat_models` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_ai_chat_settings`
--

DROP TABLE IF EXISTS `iims_ai_chat_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_ai_chat_settings` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `language_model` bigint DEFAULT NULL COMMENT '大语言模型',
  `vision_model` bigint DEFAULT NULL COMMENT '视觉大模型',
  `multimodal_model` bigint DEFAULT NULL COMMENT '多模态模型',
  `embedding_model` bigint DEFAULT NULL COMMENT '嵌入向量模型',
  `create_by` bigint NOT NULL,
  `create_time` timestamp NOT NULL,
  `update_by` bigint NOT NULL,
  `update_time` timestamp NOT NULL,
  UNIQUE KEY `iims_aigc_settings_user_id_uindex` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_ai_chat_settings`
--

LOCK TABLES `iims_ai_chat_settings` WRITE;
/*!40000 ALTER TABLE `iims_ai_chat_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_ai_chat_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_ai_chat_topic`
--

DROP TABLE IF EXISTS `iims_ai_chat_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_ai_chat_topic` (
  `id` bigint unsigned NOT NULL COMMENT '话题ID',
  `top` int DEFAULT NULL COMMENT '置顶索引',
  `title` varchar(90) DEFAULT NULL COMMENT '话题标题',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除：1 删除、0 保存',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='话题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_ai_chat_topic`
--

LOCK TABLES `iims_ai_chat_topic` WRITE;
/*!40000 ALTER TABLE `iims_ai_chat_topic` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_ai_chat_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_archive_mapper`
--

DROP TABLE IF EXISTS `iims_archive_mapper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_archive_mapper` (
  `organization_final_id` bigint NOT NULL COMMENT '组织架构最终的ID',
  `organization_ids` json DEFAULT NULL COMMENT '组织架构数组',
  `archive_type_ids` json DEFAULT NULL COMMENT '档案类型数组',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`organization_final_id`),
  UNIQUE KEY `iims_archive_mapper_pk_2` (`organization_final_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_archive_mapper`
--

LOCK TABLES `iims_archive_mapper` WRITE;
/*!40000 ALTER TABLE `iims_archive_mapper` DISABLE KEYS */;
INSERT INTO `iims_archive_mapper` VALUES (5,'[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(6,'[1, 6]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(7,'[1, 7]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(8,'[1, 8]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(9,'[1, 9]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(10,'[1, 2, 10]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(11,'[1, 2, 11]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(12,'[1, 2, 12]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(13,'[1, 2, 13]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(14,'[1, 3, 14]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(15,'[1, 3, 15]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(16,'[1, 3, 16]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(17,'[1, 3, 17]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(18,'[1, 4, 18]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(19,'[1, 4, 19]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(20,'[1, 4, 20]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10'),(21,'[1, 4, 21]','[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]',17,'2025-11-13 14:20:10',17,'2025-11-13 14:20:10');
/*!40000 ALTER TABLE `iims_archive_mapper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_archive_metadata`
--

DROP TABLE IF EXISTS `iims_archive_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_archive_metadata` (
  `id` bigint unsigned NOT NULL,
  `archival_code` varchar(90) DEFAULT NULL COMMENT '档号',
  `archival_title` varchar(90) DEFAULT NULL COMMENT '题名',
  `archival_num` varchar(120) DEFAULT NULL COMMENT '编号：室编案卷号/馆编案卷号/室编件号/馆编件号/文档序号',
  `archival_year` smallint DEFAULT NULL COMMENT '档案年度',
  `archival_date` timestamp NULL DEFAULT NULL COMMENT '档案日期',
  `archival_level` smallint DEFAULT NULL COMMENT '密级：绝密、机密、秘密',
  `archival_deadline` smallint DEFAULT NULL COMMENT '保管期限',
  `archival_abstract` varchar(255) DEFAULT NULL COMMENT '档案摘要',
  `archival_responsible` json DEFAULT NULL COMMENT '责任人',
  `archive_site` varchar(120) DEFAULT NULL COMMENT '存档地址',
  `archive_package` bigint DEFAULT NULL COMMENT '归档包：文件仓库里的文件ID',
  `archive_status` tinyint(1) DEFAULT '0' COMMENT '档案状态：退回 -1、待审核 0、审核中 1、审核通过 2',
  `metadata_property` json DEFAULT NULL COMMENT '档案其他属性：不公共数据',
  `metadata_ownership` int DEFAULT NULL COMMENT '档案归属：档案（全宗-门类-类别）菜单ID',
  `file_num` smallint DEFAULT '0' COMMENT '档案内部的文件数',
  `file_ids` json DEFAULT NULL COMMENT '档案内部存储文件：保存文件仓库里面文件IDS',
  `is_deleted` tinyint unsigned DEFAULT '0' COMMENT '是否删除：1 删除、2 彻底删除、0 保存',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='档案元数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_archive_metadata`
--

LOCK TABLES `iims_archive_metadata` WRITE;
/*!40000 ALTER TABLE `iims_archive_metadata` DISABLE KEYS */;
INSERT INTO `iims_archive_metadata` VALUES (1,'D001','Annual Report 2020','R2020-001',2021,'2019-12-31 16:00:00',1,999,'基础属性-测试参数','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2020/annual/',1001,1,'{\"carrier\": [\"5\", \"3\", \"1\"], \"pageNum\": 99, \"mainOrgan\": \"A\", \"sendOrgan\": \"B\", \"sendRange\": \"3\", \"urgentLevel\": \"4\", \"publishOrgin\": \"C\", \"enclosureNotation\": \"档案属性-测试参数\"}',151,5,'[1001, 1002, 1003, 1004, 1005]',0,10001,'2023-12-31 16:00:00',17,'2025-09-29 16:07:55'),(2,'D002','Project X Documentation','P2021-X-001',2021,'2021-03-14 16:00:00',2,10,'Documentation for Project X.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2021/project-x/',1002,1,'{}',152,3,'[1006, 1007, 1008]',0,10002,'2024-03-14 16:00:00',NULL,NULL),(3,'D003','Policy Update 2022','P2022-P-001',2022,'2022-05-31 16:00:00',1,30,'Updated company policies effective from 2022.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2022/policies/',1003,1,'{}',153,2,'[1009, 1010]',0,10003,'2024-05-31 16:00:00',NULL,NULL),(4,'D004','Sales Report Q1 2023','S2023-Q1-001',2023,'2023-03-31 16:00:00',2,10,'Sales report for the first quarter of 2023.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2023/sales/q1/',1004,1,'{\"maker\": \"AS\", \"checker\": \"DD\", \"bookkeeper\": \"FF\", \"receiptDate\": \"2024-09-16T16:00:00.000Z\", \"creditAmount\": 703.99, \"voucherNumber\": \"D096788\", \"accountingSubject\": \"5\", \"summaryExplanation\": \"测试数据\"}',154,4,'[1011, 1012, 1013, 1014]',0,10004,'2024-03-31 16:00:00',17,'2024-09-30 14:05:54'),(5,'D005','Training Manual 2024','T2024-M-001',2024,'2024-06-30 16:00:00',1,30,'Training manual for new employees in 2024.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2024/training/',1005,1,'{\"basis\": \"CS\", \"goals\": \"CS\", \"projectName\": \"TEST\", \"achievements\": \"CS\", \"fundingSource\": \"CS\", \"projectAmount\": 1, \"projectLeader\": \"UI\", \"projectNumber\": \"T677987\", \"startAndEndTime\": [\"2024-09-10T16:00:00.000Z\", \"2024-10-22T16:00:00.000Z\"]}',155,1,'[1015]',0,10005,'2024-06-30 16:00:00',17,'2024-09-30 14:54:56'),(6,'D006','Customer Feedback 2025','C2025-F-001',2025,'2025-09-30 16:00:00',2,10,'Customer feedback collected in 2025.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2025/customer-feedback/',1006,1,'{}',156,3,'[1016, 1017, 1018]',0,10006,'2025-09-30 16:00:00',NULL,NULL),(7,'D007','Annual Report 2026','R2026-001',2026,'2025-12-31 16:00:00',1,30,'Annual financial report for the year 2026.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2026/annual/',1007,1,'{}',157,5,'[1019, 1020, 1021, 1022, 1023]',0,10007,'2023-12-31 16:00:00',NULL,NULL),(8,'D008','Project Y Documentation','P2027-Y-001',2027,'2027-03-14 16:00:00',2,10,'Documentation for Project Y.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2027/project-y/',1008,1,'{}',158,3,'[1024, 1025, 1026]',0,10008,'2024-03-14 16:00:00',NULL,NULL),(9,'D009','Policy Update 2028','P2028-P-001',2028,'2028-05-31 16:00:00',1,30,'Updated company policies effective from 2028.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2028/policies/',1009,1,'{}',159,2,'[1027, 1028]',0,10009,'2024-05-31 16:00:00',NULL,NULL),(10,'D010','Sales Report Q1 2029','S2029-Q1-001',2029,'2029-03-31 16:00:00',2,10,'Sales report for the first quarter of 2029.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2029/sales/q1/',1010,1,'{}',1510,4,'[1029, 1030, 1031, 1032]',0,10010,'2024-03-31 16:00:00',NULL,NULL),(11,'D011','Training Manual 2030','T2030-M-001',2030,'2030-06-30 16:00:00',1,30,'Training manual for new employees in 2030.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2030/training/',1011,1,'{}',1511,1,'[1033]',0,10011,'2024-06-30 16:00:00',NULL,NULL),(12,'D012','Customer Feedback 2031','C2031-F-001',2031,'2031-09-30 16:00:00',2,10,'Customer feedback collected in 2031.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2031/customer-feedback/',1012,1,'{}',1512,3,'[1034, 1035, 1036]',0,10012,'2024-09-30 16:00:00',NULL,NULL),(13,'D013','Annual Report 2032','R2032-001',2032,'2031-12-31 16:00:00',1,30,'Annual financial report for the year 2032.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2032/annual/',1013,1,'{}',301,5,'[1037, 1038, 1039, 1040, 1041]',0,10013,'2023-12-31 16:00:00',NULL,NULL),(14,'D014','Project Z Documentation','P2033-Z-001',2033,'2033-03-14 16:00:00',2,10,'Documentation for Project Z.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2033/project-z/',1014,1,'{}',302,3,'[1042, 1043, 1044]',0,10014,'2023-03-14 16:00:00',NULL,NULL),(15,'D015','Policy Update 2034','P2034-P-001',2034,'2034-05-31 16:00:00',1,30,'Updated company policies effective from 2034.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2034/policies/',1015,1,'{}',303,2,'[1045, 1046]',0,10015,'2024-05-31 16:00:00',NULL,NULL),(16,'D016','Sales Report Q1 2035','S2035-Q1-001',2035,'2035-03-31 16:00:00',2,10,'Sales report for the first quarter of 2035.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2035/sales/q1/',1016,1,'{}',304,4,'[1047, 1048, 1049, 1050]',0,10016,'2024-03-31 16:00:00',NULL,NULL),(17,'D017','Training Manual 2036','T2036-M-001',2036,'2036-06-30 16:00:00',1,30,'Training manual for new employees in 2036.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2036/training/',1017,1,'{}',305,1,'[1051]',0,10017,'2024-06-30 16:00:00',NULL,NULL),(18,'D018','Customer Feedback 2037','C2037-F-001',2037,'2037-09-30 16:00:00',2,10,'Customer feedback collected in 2037.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2037/customer-feedback/',1018,1,'{}',306,3,'[1052, 1053, 1054]',0,10018,'2024-09-30 16:00:00',NULL,NULL),(19,'D019','Annual Report 2038','R2038-001',2038,'2037-12-31 16:00:00',1,30,'Annual financial report for the year 2038.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2038/annual/',1019,1,'{}',307,5,'[1055, 1056, 1057, 1058, 1059]',0,10019,'2023-12-31 16:00:00',NULL,NULL),(20,'D020','Project Alpha Report','R2021-020',2021,'2021-03-14 16:00:00',2,10,'Report on the progress of Project Alpha.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2021/project_alpha/',1001,1,'{\"pageNum\": 1}',151,3,'[1001, 1002, 1003]',0,1001,'2024-03-15 01:00:00',17,'2025-02-28 18:03:56'),(21,'D021','Meeting Minutes Q2','M2021-021',2021,'2021-04-09 16:00:00',1,10,'Minutes from the second quarter meetings.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2021/meetings/q2/',1002,1,'{}',152,4,'[1002, 1004, 1005, 1006]',0,1002,'2023-04-10 02:00:00',NULL,NULL),(22,'D022','Annual Budget Plan','B2021-022',2021,'2020-12-31 16:00:00',3,30,'Budget plan for the fiscal year 2021.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2021/budget/',1003,1,'{\"pageNum\": 1}',151,5,'[1003, 1007, 1008, 1009, 1010]',0,1003,'2023-01-01 04:00:00',17,'2025-03-01 04:24:19'),(23,'D023','Customer Feedback Survey','C2021-023',2021,'2021-06-14 16:00:00',1,10,'Survey results and analysis from customers.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2021/surveys/customer/',1004,1,'{}',152,2,'[1004, 1011]',0,1004,'2023-06-15 07:00:00',NULL,NULL),(24,'D024','Employee Training Manual','E2021-024',2021,'2021-01-31 16:00:00',2,10,'Training manual for new employees.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2021/training/manual/',1005,1,'{\"pageNum\": 1}',151,1,'[1005]',0,1005,'2023-02-01 02:00:00',17,'2025-03-01 04:24:40'),(25,'D025','Product Launch Strategy','P2021-025',2021,'2021-04-30 16:00:00',3,30,'Launch strategy for new product X.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2021/product_launches/',1006,1,'{}',152,3,'[1006, 1012, 1013]',0,1006,'2023-05-01 01:00:00',NULL,NULL),(26,'D026','Research Paper on AI','R2021-026',2021,'2021-07-31 16:00:00',2,10,'Research paper on recent advancements in AI.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2021/research/AI/',1007,1,'{\"pageNum\": 1}',151,2,'[1007, 1014]',0,1007,'2023-08-01 06:00:00',17,'2025-03-01 04:24:46'),(27,'D027','Legal Compliance Audit','L2021-027',2021,'2021-08-31 16:00:00',3,30,'Audit report on legal compliance issues.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2021/legal/audit/',1008,1,'{}',152,4,'[1008, 1015, 1016, 1017]',0,1008,'2023-09-01 03:00:00',NULL,NULL),(28,'D028','Internal Audit Report','I2021-028',2021,'2021-09-30 16:00:00',2,10,'Internal audit report on company operations.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2021/audit/internal/',1009,1,'{\"pageNum\": 1}',151,5,'[1009, 1018, 1019, 1020, 1021]',0,1009,'2023-10-01 01:00:00',17,'2025-03-01 04:24:55'),(29,'D029','Strategic Plan 2022','S2021-029',2021,'2021-10-31 16:00:00',3,30,'Strategic plan for the upcoming year 2022.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2021/strategic_plans/',1010,1,'{}',152,3,'[1010, 1022, 1023]',0,1010,'2023-11-01 02:00:00',NULL,NULL),(30,'D030','IT Infrastructure Review','I2021-030',2021,'2021-11-30 16:00:00',2,10,'Review of IT infrastructure and recommendations.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2021/IT/review/',1011,1,'{}',151,4,'[1011, 1024, 1025, 1026]',0,1011,'2023-12-01 07:00:00',NULL,NULL),(31,'D031','Sales Forecast 2022','F2021-031',2021,'2021-11-30 16:00:00',1,10,'Forecast for sales in 2022.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2021/sales/forecast/',1012,1,'{}',152,2,'[1012, 1027]',0,1012,'2023-12-01 03:00:00',NULL,NULL),(32,'D032','Health & Safety Guidelines','H2021-032',2021,'2021-06-30 16:00:00',2,10,'Guidelines for health and safety at workplace.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2021/health_safety/guidelines/',1013,1,'{}',151,3,'[1013, 1028, 1029]',0,1013,'2023-07-01 02:00:00',NULL,NULL),(33,'D033','Project Delta Summary','P2021-033',2021,'2021-02-28 16:00:00',3,30,'Summary of Project Delta.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2021/project_delta/',1014,1,'{}',152,2,'[1014, 1030]',0,1014,'2023-03-01 01:00:00',NULL,NULL),(34,'D034','Marketing Campaign Analysis','M2021-034',2021,'2021-03-31 16:00:00',2,10,'Analysis of marketing campaign effectiveness.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2021/marketing/campaign/',1015,1,'{}',151,4,'[1015, 1031, 1032, 1033]',0,1015,'2023-04-01 06:00:00',NULL,NULL),(35,'D035','Customer Service Policies','C2021-035',2021,'2021-04-30 16:00:00',1,10,'Policies for customer service.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2021/customer_service/policies/',1016,1,'{}',152,3,'[1016, 1034, 1035]',0,1016,'2023-05-01 02:00:00',NULL,NULL),(36,'D036','Human Resources Handbook','H2021-036',2021,'2021-05-31 16:00:00',2,10,'Handbook for human resources procedures.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2021/HR/handbook/',1017,1,'{}',151,4,'[1017, 1036, 1037, 1038]',0,1017,'2023-06-01 07:00:00',NULL,NULL),(37,'D037','Supply Chain Optimization','S2021-037',2021,'2021-06-30 16:00:00',3,30,'Optimization strategies for supply chain.','{\"id\": \"21\", \"username\": \"Q-001\"}','/archives/2021/supply_chain/optimization/',1018,1,'{}',152,2,'[1018, 1039]',0,1018,'2024-07-01 03:00:00',NULL,NULL),(38,'D038','Financial Statement 2021','F2021-038',2021,'2021-12-30 16:00:00',2,10,'Annual financial statement for 2021.','{\"id\": \"17\", \"username\": \"Aitenry\"}','/archives/2021/financial/statements/',1019,1,'{}',151,5,'[1019, 1040, 1041, 1042, 1043]',0,1019,'2023-12-31 01:00:00',NULL,NULL),(39,'D039','Annual Report 2022','A2022-039',2022,'2021-12-31 16:00:00',3,30,'Annual financial and operational report for the year 2022.','{\"id\": \"18\", \"username\": \"G-001\"}','/archives/2022/annual/',1020,1,'{}',152,5,'[1020, 1044, 1045, 1046, 1047]',0,1020,'2024-01-01 01:00:00',NULL,NULL),(42,'D6910','TEST',NULL,2024,'2024-08-27 16:00:00',1,10,'测试数据','{\"id\": \"21\", \"username\": \"Q-001\"}',NULL,NULL,1,'{\"carrier\": [\"3\", \"4\", \"2\", \"1\", \"5\"], \"pageNum\": 11, \"mainOrgan\": \"A\", \"sendOrgan\": \"B\", \"sendRange\": \"2\", \"urgentLevel\": \"2\", \"publishOrgin\": \"C\", \"enclosureNotation\": \"测试数据\"}',151,NULL,NULL,0,17,'2024-08-28 14:49:19',17,'2024-08-28 14:49:19'),(43,'D7631','To Be Once',NULL,2024,'2024-08-27 16:00:00',2,30,'测试数据','{\"id\": \"17\", \"username\": \"Aitenry\"}',NULL,NULL,1,'{\"carrier\": [\"2\"], \"pageNum\": 90, \"mainOrgan\": \"A\", \"sendOrgan\": \"B\", \"sendRange\": \"1\", \"urgentLevel\": \"1\", \"publishOrgin\": \"C\", \"enclosureNotation\": \"测试数据\"}',151,NULL,NULL,0,17,'2024-08-28 14:54:15',17,'2024-08-28 14:54:15'),(44,'D6910','TEST',NULL,2024,'2024-08-27 16:00:00',1,10,'','{\"id\": \"18\", \"username\": \"G-001\"}',NULL,NULL,0,'{\"carrier\": [\"3\", \"4\", \"2\", \"1\", \"5\"], \"pageNum\": 11, \"mainOrgan\": \"A\", \"sendOrgan\": \"B\", \"sendRange\": \"2\", \"urgentLevel\": \"2\", \"publishOrgin\": \"D\", \"enclosureNotation\": \"\"}',151,NULL,NULL,0,17,'2024-08-28 14:56:54',17,'2024-09-18 15:33:27'),(45,'D7631','To Be Once',NULL,2024,'2024-08-27 16:00:00',2,30,'测试数据','{\"id\": \"21\", \"username\": \"Q-001\"}',NULL,NULL,0,'{\"carrier\": [\"2\"], \"pageNum\": 94, \"mainOrgan\": \"A\", \"sendOrgan\": \"B\", \"sendRange\": \"1\", \"urgentLevel\": \"1\", \"publishOrgin\": \"C\", \"enclosureNotation\": \"测试数据\"}',151,NULL,NULL,0,17,'2024-08-28 14:57:18',17,'2024-09-18 15:33:27'),(46,'D7631','To Be Once',NULL,2024,'2024-08-27 16:00:00',2,30,'测试数据','{\"id\": \"17\", \"username\": \"Aitenry\"}',NULL,NULL,0,'{\"carrier\": [\"2\"], \"pageNum\": 90, \"mainOrgan\": \"A\", \"sendOrgan\": \"B\", \"sendRange\": \"1\", \"urgentLevel\": \"1\", \"publishOrgin\": \"C\", \"enclosureNotation\": \"测试数据\"}',151,NULL,NULL,0,17,'2024-08-28 14:58:40',17,'2024-09-18 15:33:33'),(47,'D1267','TR-Info',NULL,2024,'2024-08-19 16:00:00',2,30,'测试数据','{\"id\": \"18\", \"username\": \"G-001\"}',NULL,NULL,1,'{\"carrier\": [\"5\", \"3\", \"1\"], \"pageNum\": 10, \"mainOrgan\": \"E\", \"sendOrgan\": \"T\", \"sendRange\": \"2\", \"urgentLevel\": \"2\", \"publishOrgin\": \"B\", \"enclosureNotation\": \"测试数据\"}',151,NULL,NULL,0,17,'2024-08-28 15:02:03',17,'2024-08-29 16:41:21'),(205668159456550912,'T0910','TEST ID MORE',NULL,2024,'2024-09-23 16:00:00',2,999,'TEST','{\"id\": \"21\", \"username\": \"Q-001\"}',NULL,NULL,0,'{\"carrier\": [\"5\", \"4\", \"3\", \"2\", \"1\"], \"pageNum\": 1, \"mainOrgan\": \"A\", \"sendOrgan\": \"B\", \"sendRange\": \"2\", \"urgentLevel\": \"2\", \"publishOrgin\": \"C\", \"enclosureNotation\": \"TEST\"}',151,0,NULL,0,17,'2024-09-24 14:51:47',17,'2024-09-24 15:00:41');
/*!40000 ALTER TABLE `iims_archive_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_archive_type`
--

DROP TABLE IF EXISTS `iims_archive_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_archive_type` (
  `id` bigint unsigned NOT NULL,
  `name` varchar(45) DEFAULT NULL COMMENT '档案名称',
  `operate_component` varchar(90) NOT NULL COMMENT '新增档案页组件名',
  `detail_component` varchar(90) NOT NULL COMMENT '档案详情页组件名',
  `code` varchar(45) DEFAULT NULL COMMENT '全宗',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_archive_type`
--

LOCK TABLES `iims_archive_type` WRITE;
/*!40000 ALTER TABLE `iims_archive_type` DISABLE KEYS */;
INSERT INTO `iims_archive_type` VALUES (1,'文书档案','OperateDocArchive','DetailDocArchive','WSDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(2,'科技档案','OperateTechArchive','DetailTechArchive','KJDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(3,'人事档案','OperatePrsArchive','DetailPrsArchive','RSDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(4,'会计档案','OperateAcctArchive','DetailAcctArchive','KJDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(5,'项目档案','OperateProjectArchive','DetailProjectArchive','XMDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(6,'专业档案','OperateProfArchive','DetailProfArchive','ZYDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(7,'声像档案','OperateAudioVisArchive','DetailAudioVisArchive','SXDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(8,'合同档案','OperateContractArchive','DetailContractArchive','HTDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(9,'司法档案','OperateJudicialArchive','DetailJudicialArchive','SFDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(10,'建设档案','OperateBuildArchive','DetailBuildArchive','JSDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(11,'金融档案','OperateFinancialArchive','DetailFinancialArchive','JRDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39'),(12,'实物档案','OperatePhysicalArchive','DetailPhysicalArchive','SWDA',17,'2024-10-13 12:10:40',17,'2025-11-16 06:24:39');
/*!40000 ALTER TABLE `iims_archive_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_admin`
--

DROP TABLE IF EXISTS `iims_integral_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_admin` (
  `id` bigint unsigned NOT NULL COMMENT '管理员id',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '真实姓名',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `email` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户邮箱',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '性别，1男0女',
  `id_number` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '身份证号',
  `role` json NOT NULL COMMENT '角色',
  `organization` bigint DEFAULT NULL COMMENT '组织',
  `introduction` varchar(60) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '简介',
  `avatar` bigint unsigned DEFAULT NULL COMMENT '头像：文件ID',
  `root` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是超级管理员',
  `is_disable` tinyint(1) DEFAULT '0' COMMENT '是否禁用',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '最后修改人id',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_username` (`username`) USING BTREE,
  UNIQUE KEY `idx_phone` (`phone`) USING BTREE,
  UNIQUE KEY `idx_email` (`email`) USING BTREE,
  UNIQUE KEY `id_number` (`id_number`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_admin`
--

LOCK TABLES `iims_integral_admin` WRITE;
/*!40000 ALTER TABLE `iims_integral_admin` DISABLE KEYS */;
INSERT INTO `iims_integral_admin` VALUES (17,'YE CONG JING','Aitenry','aitenry@126.com','21232f297a57a5a743894a0e4a801fc3','13031239021','1','440823200003110361','[\"10\"]',22,'#For the future',243705076635164672,1,0,0,17,'2024-05-01 16:35:08',17,'2025-11-16 04:02:05'),(18,'G-DMS','G-001','dms@toryu.com','21232f297a57a5a743894a0e4a801fc3','15521633043','0','440823199703110361','[\"15\"]',27,'自律自强，奋斗不息，实现梦想指日可待。',243705124924186624,0,0,0,17,'2024-05-23 14:29:35',17,'2025-11-16 04:02:33'),(21,'Q-EAS','Q-001','eas@toryu.com','21232f297a57a5a743894a0e4a801fc3','19022666810','0','440823199010210312','[\"16\"]',28,'自律乃成功之基石，奋斗是进步之阶梯。',243705151411216384,0,0,0,17,'2024-06-21 04:26:26',17,'2025-11-16 04:13:25');
/*!40000 ALTER TABLE `iims_integral_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_article`
--

DROP TABLE IF EXISTS `iims_integral_article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_article` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '文章id',
  `title` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文章标题',
  `cover` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文章封面',
  `summary` varchar(960) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '文章摘要',
  `read_num` int unsigned NOT NULL DEFAULT '0' COMMENT '被阅读次数',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '文章类型 - 1：普通文章，2：收录于知识库',
  `weight` int unsigned NOT NULL DEFAULT '0' COMMENT '文章权重，用于是否置顶（0: 未置顶；>0: 参与置顶，权重值越高越靠前）',
  `dict_tag_ids` json DEFAULT NULL COMMENT '标签：字典值IDS',
  `dict_category_id` bigint DEFAULT NULL COMMENT '分类：字典值ID',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志位：0：未删除 1：已删除',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=287926063479263233 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='文章表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_article`
--

LOCK TABLES `iims_integral_article` WRITE;
/*!40000 ALTER TABLE `iims_integral_article` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_integral_article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_article_content`
--

DROP TABLE IF EXISTS `iims_integral_article_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_article_content` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '文章内容id',
  `article_id` bigint NOT NULL COMMENT '文章id',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '教程正文',
  `chunk_keys` json DEFAULT NULL COMMENT '块的MD5值',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_article_id` (`article_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=287926063676395521 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='文章内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_article_content`
--

LOCK TABLES `iims_integral_article_content` WRITE;
/*!40000 ALTER TABLE `iims_integral_article_content` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_integral_article_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_cipher`
--

DROP TABLE IF EXISTS `iims_integral_cipher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_cipher` (
  `id` bigint NOT NULL,
  `name` varchar(60) NOT NULL COMMENT '名称',
  `key` varchar(120) NOT NULL COMMENT '密钥',
  `salt` varchar(10) NOT NULL COMMENT '盐值',
  `deadline` timestamp NOT NULL COMMENT '过期时间',
  `interfaces` json DEFAULT NULL COMMENT '可访问的接口',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `iims_integral_cipher_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='开放接口密钥';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_cipher`
--

LOCK TABLES `iims_integral_cipher` WRITE;
/*!40000 ALTER TABLE `iims_integral_cipher` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_integral_cipher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_comment`
--

DROP TABLE IF EXISTS `iims_integral_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_comment` (
  `id` bigint unsigned NOT NULL COMMENT 'id',
  `content` varchar(120) NOT NULL DEFAULT '' COMMENT '评论内容',
  `router_url` varchar(60) NOT NULL DEFAULT '' COMMENT '评论所属的路由',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志位：0：未删除 1：已删除',
  `reply_comment_id` bigint unsigned DEFAULT NULL COMMENT '回复的评论 ID',
  `parent_comment_id` bigint unsigned DEFAULT NULL COMMENT '父评论 ID',
  `reason` varchar(300) DEFAULT '' COMMENT '原因描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1: 待审核；2：正常；3：审核未通过;',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_parent_comment_id` (`parent_comment_id`),
  KEY `idx_reply_comment_id` (`reply_comment_id`),
  KEY `idx_router_url` (`router_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_comment`
--

LOCK TABLES `iims_integral_comment` WRITE;
/*!40000 ALTER TABLE `iims_integral_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_integral_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_dict`
--

DROP TABLE IF EXISTS `iims_integral_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_dict` (
  `id` bigint unsigned NOT NULL,
  `name` varchar(30) DEFAULT NULL COMMENT '字典名称',
  `remark` varchar(120) DEFAULT NULL COMMENT '备注',
  `is_disable` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否禁用：0 正常、1 禁用',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
  `is_can_change` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否可以变更：0 是、1 否',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_dict`
--

LOCK TABLES `iims_integral_dict` WRITE;
/*!40000 ALTER TABLE `iims_integral_dict` DISABLE KEYS */;
INSERT INTO `iims_integral_dict` VALUES (0,'category','分类',0,0,0,17,'2024-11-01 14:18:28',17,'2025-09-29 14:57:39'),(1,'tag','标签',0,0,0,17,'2024-11-01 14:18:26',17,'2025-05-30 12:57:45'),(295915065788469248,'test1','测试1',1,1,1,17,'2025-05-31 15:40:46',17,'2025-06-01 15:20:34');
/*!40000 ALTER TABLE `iims_integral_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_dict_value`
--

DROP TABLE IF EXISTS `iims_integral_dict_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_dict_value` (
  `id` bigint NOT NULL COMMENT '键',
  `dict_id` bigint NOT NULL COMMENT '字典ID',
  `value` varchar(120) NOT NULL COMMENT '值',
  `remark` varchar(120) DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典值表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_dict_value`
--

LOCK TABLES `iims_integral_dict_value` WRITE;
/*!40000 ALTER TABLE `iims_integral_dict_value` DISABLE KEYS */;
INSERT INTO `iims_integral_dict_value` VALUES (1,1,'Java','',0,17,'2024-11-01 14:24:23',17,'2025-06-01 15:32:28'),(2,1,'Python','',0,17,'2024-11-01 14:24:25',17,'2025-06-01 15:32:32'),(3,1,'C++','',0,17,'2024-11-01 14:24:26',17,'2025-06-01 15:32:34'),(4,0,'技术分享','用于标识纯技术分享的内容',0,17,'2024-11-01 14:24:26',17,'2025-06-01 15:11:54'),(5,0,'开发项目','用于标识项目开发的内容',0,17,'2024-11-01 14:24:27',17,'2025-06-01 15:10:44'),(6,0,'市场发掘','用于标识市场里面发掘的内容',0,17,'2024-11-01 14:24:28',17,'2025-06-01 15:09:46'),(7,1,'自动化','',0,17,'2024-11-01 14:24:28',17,'2025-06-01 15:32:37'),(9,1,'AIGC','',0,17,'2024-11-01 14:24:28',17,'2025-06-01 15:32:40'),(296268767023992832,0,'设备维修','用于标识设备维修的内容',0,17,'2025-06-01 15:06:15',17,'2025-06-01 15:06:15'),(297344334309756928,0,'附加知识','给模型提供额外的知识，使其可以输出特定的文本信息',0,17,'2025-06-04 14:20:10',17,'2025-06-04 14:20:10'),(339753016666230784,0,'国家政策','用于标识国家出台的政策内容',0,17,'2025-09-29 14:57:08',17,'2025-09-29 14:57:08'),(339753584314945536,0,'法律法规','用于标识关于中国的法律法规',0,17,'2025-09-29 14:59:23',17,'2025-09-29 14:59:23');
/*!40000 ALTER TABLE `iims_integral_dict_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_logs`
--

DROP TABLE IF EXISTS `iims_integral_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_logs` (
  `id` bigint NOT NULL,
  `level` varchar(45) DEFAULT NULL,
  `thread_name` varchar(45) DEFAULT NULL,
  `message` text,
  `logger_name` varchar(120) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `iims_integral_logs_pk_2` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_logs`
--

LOCK TABLES `iims_integral_logs` WRITE;
/*!40000 ALTER TABLE `iims_integral_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_integral_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_menu`
--

DROP TABLE IF EXISTS `iims_integral_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_menu` (
  `id` bigint unsigned NOT NULL COMMENT '菜单id',
  `title` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `parent_id` bigint unsigned DEFAULT '0' COMMENT '父级菜单',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路径',
  `name` varchar(90) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
  `component` varchar(135) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件',
  `icon` varchar(105) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图标',
  `menu_type` char(1) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '' COMMENT '菜单类型（M目录 C视图 F按钮）',
  `perms` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '权限标识',
  `redirect` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '重定向',
  `create_by` bigint NOT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `status` tinyint(1) DEFAULT '1' COMMENT '菜单状态（1正常 0停用）',
  `visible` char(1) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `is_frame` int DEFAULT '1' COMMENT '是否开启新标签页（0是 1否）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_title` (`title`) USING BTREE COMMENT '标题唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='菜单集合表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_menu`
--

LOCK TABLES `iims_integral_menu` WRITE;
/*!40000 ALTER TABLE `iims_integral_menu` DISABLE KEYS */;
INSERT INTO `iims_integral_menu` VALUES (100,'信息门户',0,0,'/home','home','home','ri-numbers-fill','C','home:view','admin',10,'2021-03-30 19:12:12',17,'2024-12-27 17:37:48',1,'0',1),(101,'权限管理',0,4,'/permission','permission','','ri-server-fill','M','','admin',10,'2021-03-30 19:12:12',17,'2024-12-27 17:44:17',1,'0',1),(102,'用户管理',101,1,'admin','admin','permission/admin','ri-admin-fill','C','admin:list',NULL,10,'2021-03-30 19:12:12',10,'2025-01-01 14:43:39',1,'0',1),(104,'角色管理',101,3,'roles','role','permission/roles','ri-team-fill','C','role:list',NULL,10,'2021-03-30 19:12:12',17,'2025-01-01 14:43:54',1,'0',1),(105,'菜单管理',101,5,'menus','menu','permission/menus','ri-list-view','C','menu:list',NULL,10,'2024-12-25 15:30:54',17,'2025-01-01 14:47:03',1,'0',1),(201,'内置系统',0,2,'/system','system','','ri-dashboard-horizontal-fill','M','','system/archives/home',10,'2024-05-01 18:28:02',17,'2024-12-27 17:43:51',1,'0',1),(202,'档案管理系统',201,1,'archives','archives','','ri-archive-drawer-fill','M','archives:view','system/archives/home',10,'2024-05-01 18:48:36',10,'2024-05-01 18:48:36',1,'0',1),(203,'电子教务系统',201,2,'educational','educational','','ri-school-fill','M','educational:view',NULL,10,'2024-05-01 18:55:45',10,'2024-05-01 19:00:51',1,'0',1),(301,'知识管理',0,3,'/knowledge','knowledge','','ri-book-marked-fill','M','','article',10,'2024-05-09 13:09:14',17,'2024-12-27 17:44:22',1,'0',1),(302,'文档管理',301,1,'article','article','knowledge/article','ri-article-fill','C','articles:list',NULL,10,'2024-05-09 13:13:09',10,'2026-01-10 12:25:43',1,'0',1),(303,'知识库管理',301,2,'wiki','wiki','knowledge/wiki','ri-book-shelf-fill','C','wiki:list',NULL,10,'2024-05-09 13:22:02',10,'2024-05-09 13:22:02',1,'0',1),(601,'系统监控',0,6,'/monitor','monitor','','ri-bar-chart-box-ai-fill','M',NULL,NULL,17,'2024-09-04 16:33:54',17,'2024-09-04 16:35:08',1,'0',1),(701,'系统设置',0,7,'/settings','settings','','ri-equalizer-fill','M',NULL,'dict',17,'2024-09-04 16:32:43',17,'2024-09-17 08:49:19',1,'0',1),(901,'QJingTalk',0,9,'/ai/talk','ai','ai/talk','ri-chat-ai-fill','C','ai:talk:view',NULL,17,'2024-09-03 15:23:22',17,'2024-09-17 08:45:12',1,'0',0),(10201,'管理员查询',102,1,'','','','ri-search-line','F','permission:admin:query',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10202,'管理员新增',102,1,'','','','ri-radio-button-line','F','permission:admin:add',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10203,'管理员修改',102,1,'','','','ri-radio-button-line','F','permission:admin:update',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10204,'管理员删除',102,1,'','','','ri-radio-button-line','F','permission:admin:delete',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10205,'管理员导出',102,1,'','','','ri-radio-button-line','F','permission:admin:export',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10206,'管理员导入',102,1,'','','','ri-radio-button-line','F','permission:admin:import',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10301,'用户查询',103,1,'','','','ri-radio-button-line','F','permission:user:query',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10302,'用户新增',103,1,'','','','ri-radio-button-line','F','permission:user:add',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10303,'用户修改',103,1,'','','','ri-radio-button-line','F','permission:user:update',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10304,'用户删除',103,1,'','','','ri-radio-button-line','F','permission:user:delete',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10305,'用户导出',103,1,'','','','ri-radio-button-line','F','permission:user:export',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10306,'用户导入',103,1,'','','','ri-radio-button-line','F','permission:user:import',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10307,'用户下线',103,1,'','','','ri-radio-button-line','F','permission:user:offline',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10401,'角色查询',104,1,'','','','ri-radio-button-line','F','permission:role:query',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10402,'角色新增',104,1,'','','','ri-radio-button-line','F','permission:role:add',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10403,'角色修改',104,1,'','','','ri-radio-button-line','F','permission:role:update',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10404,'角色删除',104,1,'','','','ri-radio-button-line','F','permission:role:delete',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10405,'角色导出',104,1,'','','','ri-radio-button-line','F','permission:role:export',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10406,'角色导入',104,1,'','','','ri-radio-button-line','F','permission:role:import',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10501,'菜单查询',105,1,'','','','ri-radio-button-line','F','permission:menu:query',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10502,'菜单新增',105,1,'','','','ri-radio-button-line','F','permission:menu:add',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10503,'菜单修改',105,1,'','','','ri-radio-button-line','F','permission:menu:update',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10504,'菜单删除',105,1,'','','','ri-radio-button-line','F','permission:menu:delete',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10505,'菜单导出',105,1,'','','','ri-radio-button-line','F','permission:menu:export',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(10506,'菜单导入',105,1,'','','','ri-radio-button-line','F','permission:menu:import',NULL,10,'2021-03-30 19:12:12',10,'2021-03-30 19:12:12',1,'0',1),(20201,'档案门户',202,1,'home','Archive Portal','system/archives/home','ri-archive-fill','C','info:archives:home',NULL,10,'2024-05-01 18:48:36',10,'2024-07-06 17:12:56',1,'0',1),(20202,'档案桥接',202,2,'bridge','Archive bridging','system/archives/bridge','ri-links-fill','C','info:archives:bridge',NULL,10,'2024-07-06 16:57:47',NULL,NULL,1,'0',1),(20203,'档案采集',202,3,'collected','Archive Collecting',NULL,'ri-inbox-archive-fill','M',NULL,NULL,17,'2024-07-06 17:08:36',17,'2024-07-06 17:08:36',1,'0',1),(20301,'教务门户',203,1,'home','E-educational Administration Portal','system/educational/home','ri-community-fill','C','info:educational:home',NULL,10,'2024-05-01 18:48:36',10,'2024-06-14 14:52:32',1,'0',1),(20302,'成绩管理',240808635444367360,2,'transcript','Transcript Manage','system/educational/examination/transcript','ri-edit-circle-fill','C','info:educational:transcript',NULL,10,'2024-05-01 18:48:36',10,'2024-12-30 14:35:08',1,'0',1),(2020301,'收集库',20203,3,'collect','Collection Library','system/archives/collect','ri-archive-stack-fill','C','info:archives:collect',NULL,10,'2024-05-01 18:48:36',10,'2024-07-06 17:01:08',1,'0',1),(238999200422039552,'字典仓库',701,0,'dict',NULL,'settings/dict','ri-swap-2-fill','C','setting:dict:view',NULL,17,'2024-12-25 14:17:26',17,'2024-12-25 15:20:44',1,'0',1),(239011652547252224,'日志监控',601,1,'log',NULL,'monitor/log','ri-pages-fill','C','monitor:log:view',NULL,17,'2024-12-25 15:06:55',17,'2024-12-25 15:06:55',1,'0',1),(239013397163806720,'负载监控',601,0,'load',NULL,'monitor/load','ri-database-fill','C','monitor:load:view',NULL,17,'2024-12-25 15:13:51',17,'2024-12-25 15:13:51',1,'0',1),(239014028561747968,'定时任务',701,2,'task',NULL,'settings/task','ri-hourglass-fill','C','settings:task:view',NULL,17,'2024-12-25 15:16:22',17,'2024-12-26 15:38:39',1,'0',1),(239016569370447872,'密钥管理',101,4,'keys','keys','permission/keys','ri-shield-keyhole-fill','C','permission:keys:view',NULL,17,'2024-12-25 15:26:27',17,'2025-01-01 14:46:56',1,'0',1),(239380636689895424,'模型仓库',701,1,'model',NULL,'settings/model','ri-color-filter-fill','C','settings:model:view',NULL,17,'2024-12-26 15:33:08',17,'2024-12-26 15:38:48',1,'0',1),(239387126641332224,'智能云库',0,1,'/file/store',NULL,'file/store','ri-folders-fill','C','file:store:view',NULL,17,'2024-12-26 15:58:55',17,'2024-12-26 15:58:55',1,'0',0),(240806564712288256,'教学规划',203,2,'teach',NULL,'','ri-presentation-fill','M','',NULL,17,'2024-12-30 13:59:16',17,'2024-12-30 14:24:57',1,'0',1),(240808635444367360,'考试内容',203,3,'examination',NULL,NULL,'ri-draft-fill','M',NULL,NULL,17,'2024-12-30 14:07:29',17,'2024-12-30 14:12:43',1,'0',1),(240810542867025920,'课程管理',240806564712288256,1,'course',NULL,'system/educational/teach/course','ri-book-open-fill','C','educational:teach:course',NULL,17,'2024-12-30 14:15:04',17,'2024-12-30 14:17:44',1,'0',1),(240812860022853632,'班级管理',240806564712288256,2,'class',NULL,'system/educational/teach/class','ri-contacts-book-fill','C','educational:teach:class',NULL,17,'2024-12-30 14:24:16',17,'2024-12-30 14:24:16',1,'0',1),(240813597926756352,'考试安排',240808635444367360,0,'arrange',NULL,'system/educational/examination/arrange','ri-signpost-fill','C','educational:examination:arrange',NULL,17,'2024-12-30 14:27:12',17,'2024-12-30 14:28:37',1,'0',1),(240815448281714688,'成绩分析',240808635444367360,1,'analysis',NULL,'system/educational/examination/analysis','ri-body-scan-fill','C','educational:examination:analysis',NULL,17,'2024-12-30 14:34:34',17,'2024-12-30 14:35:35',1,'0',1),(240826981330063360,'信息管理',203,4,'service',NULL,NULL,'ri-contacts-fill','M',NULL,NULL,17,'2024-12-30 15:20:23',17,'2024-12-30 15:24:52',1,'0',1),(240827816525041664,'学生管理',240826981330063360,0,'student',NULL,'system/educational/service/student','ri-flower-fill','C',NULL,NULL,17,'2024-12-30 15:23:42',17,'2025-03-11 15:45:23',1,'0',1),(240828043986341888,'教师管理',240826981330063360,1,'teacher',NULL,'system/educational/service/teacher','ri-leaf-fill','C',NULL,NULL,17,'2024-12-30 15:24:37',17,'2025-03-11 15:45:46',1,'0',1),(241529010050437120,'教师排课',240806564712288256,3,'schedule',NULL,'system/educational/teach/schedule','ri-pencil-ruler-2-fill','C','educational:teach:schedule',NULL,17,'2025-01-01 13:50:00',17,'2025-01-01 13:50:46',1,'0',1),(241531418730172416,'财务管理',203,5,'finance',NULL,NULL,'ri-wallet-fill','M',NULL,NULL,17,'2025-01-01 13:59:34',17,'2025-01-01 13:59:42',1,'0',1),(241531780807659520,'财务收入',241531418730172416,1,'income',NULL,'system/educational/finance/income','ri-hand-coin-fill','C','finance:income:view',NULL,17,'2025-01-01 14:01:01',17,'2025-01-01 14:39:13',1,'0',1),(241533307563675648,'财务分析',241531418730172416,0,'analysis',NULL,'system/educational/finance/analysis','ri-funds-box-fill','C','finance:analysis:view',NULL,17,'2025-01-01 14:07:05',17,'2025-01-01 14:08:07',1,'0',1),(241535000191832064,'财务支出',241531418730172416,3,'expenses',NULL,'system/educational/finance/expenses','ri-token-swap-fill','C','finance:expenses:view',NULL,17,'2025-01-01 14:13:48',17,'2025-01-01 14:14:10',1,'0',1),(241538580479086592,'统计报表',203,6,'report',NULL,'system/educational/report','ri-table-view','C','educational:report:view',NULL,17,'2025-01-01 14:28:02',17,'2025-01-01 14:28:40',1,'0',1),(241542415356399616,'组织管理',101,6,'organization',NULL,'permission/organization','ri-organization-chart','C','permission:organization:view',NULL,17,'2025-01-01 14:43:16',17,'2025-01-01 14:46:47',1,'0',1);
/*!40000 ALTER TABLE `iims_integral_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_organization`
--

DROP TABLE IF EXISTS `iims_integral_organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_organization` (
  `id` bigint NOT NULL,
  `parent_id` bigint NOT NULL,
  `name` varchar(60) DEFAULT NULL COMMENT '名称',
  `type` tinyint NOT NULL COMMENT '类型：0 公司、1 部门、2 职位',
  `code` varchar(45) DEFAULT NULL COMMENT '全宗',
  `description` varchar(120) DEFAULT NULL COMMENT '描述',
  `is_deleted` tinyint DEFAULT NULL COMMENT '是否删除：0 存在、1 删除',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `iims_integral_organization_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_organization`
--

LOCK TABLES `iims_integral_organization` WRITE;
/*!40000 ALTER TABLE `iims_integral_organization` DISABLE KEYS */;
INSERT INTO `iims_integral_organization` VALUES (1,0,'启境科技有限责任公司',0,'QJ','集团公司',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(2,1,'宇辰星途有限责任公司',0,'YCXT','启境科技子公司',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(3,1,'生命纪元有限责任公司',0,'SMJY','启境科技子公司',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(4,1,'凡心禹辰有限责任公司',0,'FXYC','启境科技子公司',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(5,1,'总裁办',1,'ZCB','集团公司总裁办公室',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(6,1,'集团财务部',1,'CWB','集团公司财务管理部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(7,1,'战略发展部',1,'FZB','集团公司战略规划与发展部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(8,1,'集团人事部',1,'RSB','集团公司人力资源中心',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(9,1,'集团行政部',1,'XZB','集团公司行政服务中心',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(10,2,'研发部',1,'YFB','宇辰星途 - 产品研发部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(11,2,'销售部',1,'XSB','宇辰星途 - 产品销售部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(12,2,'人事部',1,'RSB','宇辰星途 - 人力资源部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(13,2,'财务部',1,'CWB','宇辰星途 - 财务管理部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(14,3,'研发部',1,'YFB','生命纪元 - 产品研发部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(15,3,'销售部',1,'XSB','生命纪元 - 产品销售部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(16,3,'人事部',1,'RSB','生命纪元 - 人力资源部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(17,3,'财务部',1,'CWB','生命纪元 - 财务管理部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(18,4,'研发部',1,'YFB','凡心禹辰 - 产品研发部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(19,4,'销售部',1,'XSB','凡心禹辰 - 产品销售部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(20,4,'人事部',1,'RSB','凡心禹辰 - 人力资源部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(21,4,'财务部',1,'CWB','凡心禹辰 - 财务管理部门',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(22,5,'集团总裁',2,NULL,'集团总裁职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(23,5,'总裁助理',2,NULL,'集团总裁助理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(24,6,'集团财务总监',2,NULL,'集团公司财务总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(25,6,'集团财务经理',2,NULL,'集团公司财务经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(26,6,'集团财务专员',2,NULL,'集团公司财务专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(27,7,'战略发展总监',2,NULL,'集团战略发展总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(28,7,'战略分析师',2,NULL,'集团战略分析师职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(29,8,'集团人事总监',2,NULL,'集团公司人事总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(30,8,'集团人事经理',2,NULL,'集团公司人事经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(31,8,'集团人事专员',2,NULL,'集团公司人事专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(32,9,'集团行政总监',2,NULL,'集团公司行政总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(33,9,'集团行政经理',2,NULL,'集团公司行政经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(34,9,'集团行政专员',2,NULL,'集团公司行政专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(35,10,'研发总监',2,NULL,'宇辰星途 - 研发总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(36,10,'研发经理',2,NULL,'宇辰星途 - 研发经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(37,10,'研发工程师',2,NULL,'宇辰星途 - 研发工程师职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(38,11,'销售总监',2,NULL,'宇辰星途 - 销售总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(39,11,'销售经理',2,NULL,'宇辰星途 - 销售经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(40,11,'销售专员',2,NULL,'宇辰星途 - 销售专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(41,12,'人事经理',2,NULL,'宇辰星途 - 人事经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(42,12,'人事专员',2,NULL,'宇辰星途 - 人事专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(43,13,'财务经理',2,NULL,'宇辰星途 - 财务经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(44,13,'财务专员',2,NULL,'宇辰星途 - 财务专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(45,14,'研发总监',2,NULL,'生命纪元 - 研发总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(46,14,'研发经理',2,NULL,'生命纪元 - 研发经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(47,14,'研发工程师',2,NULL,'生命纪元 - 研发工程师职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(48,15,'销售总监',2,NULL,'生命纪元 - 销售总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(49,15,'销售经理',2,NULL,'生命纪元 - 销售经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(50,15,'销售专员',2,NULL,'生命纪元 - 销售专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(51,16,'人事经理',2,NULL,'生命纪元 - 人事经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(52,16,'人事专员',2,NULL,'生命纪元 - 人事专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(53,17,'财务经理',2,NULL,'生命纪元 - 财务经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(54,17,'财务专员',2,NULL,'生命纪元 - 财务专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(55,18,'研发总监',2,NULL,'凡心禹辰 - 研发总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(56,18,'研发经理',2,NULL,'凡心禹辰 - 研发经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(57,18,'研发工程师',2,NULL,'凡心禹辰 - 研发工程师职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(58,19,'销售总监',2,NULL,'凡心禹辰 - 销售总监职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(59,19,'销售经理',2,NULL,'凡心禹辰 - 销售经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(60,19,'销售专员',2,NULL,'凡心禹辰 - 销售专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(61,20,'人事经理',2,NULL,'凡心禹辰 - 人事经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(62,20,'人事专员',2,NULL,'凡心禹辰 - 人事专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(63,21,'财务经理',2,NULL,'凡心禹辰 - 财务经理职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46'),(64,21,'财务专员',2,NULL,'凡心禹辰 - 财务专员职位',0,17,'2025-11-12 16:09:46',17,'2025-11-12 16:09:46');
/*!40000 ALTER TABLE `iims_integral_organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_permission`
--

DROP TABLE IF EXISTS `iims_integral_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_permission` (
  `id` bigint unsigned NOT NULL,
  `bind_id` bigint NOT NULL COMMENT '绑定ID',
  `bind_type` tinyint(1) NOT NULL COMMENT '绑定对象类型：0 文件、1 档案',
  `bind_wu_ids` json DEFAULT NULL COMMENT '当前绑定的对象有那些用户拥有写权限',
  `bind_ru_ids` json DEFAULT NULL COMMENT '当前绑定的对象有那些用户拥有读权限',
  `bind_xu_ids` json DEFAULT NULL COMMENT '当前绑定的对象有那些用户拥有执行权限',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dms_archive_permission_archive_id_uindex` (`bind_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_permission`
--

LOCK TABLES `iims_integral_permission` WRITE;
/*!40000 ALTER TABLE `iims_integral_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_integral_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_role`
--

DROP TABLE IF EXISTS `iims_integral_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_role` (
  `id` bigint unsigned NOT NULL COMMENT '主键',
  `role_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `role_en` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限字符',
  `menus` json NOT NULL COMMENT '菜单权限集合',
  `info` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
  `systemic` tinyint DEFAULT '0' COMMENT '是否为系统内置角色',
  `create_by` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '最后修改人id',
  `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unq_role_name` (`role_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='管理员角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_role`
--

LOCK TABLES `iims_integral_role` WRITE;
/*!40000 ALTER TABLE `iims_integral_role` DISABLE KEYS */;
INSERT INTO `iims_integral_role` VALUES (10,'超级管理员','Admin [IIMS]','[\"100\", \"101\", \"241542415356399616\", \"239016569370447872\", \"102\", \"10201\", \"10202\", \"10203\", \"10204\", \"10205\", \"10206\", \"103\", \"10301\", \"10302\", \"10303\", \"10304\", \"10305\", \"10306\", \"10307\", \"104\", \"10401\", \"10402\", \"10403\", \"10404\", \"10405\", \"10406\", \"105\", \"10501\", \"10502\", \"10503\", \"10504\", \"10505\", \"10506\", \"201\", \"202\", \"20201\", \"20202\", \"20203\", \"2020301\", \"203\", \"240826981330063360\", \"240828043986341888\", \"240827816525041664\", \"20301\", \"241531418730172416\", \"241531780807659520\", \"241535000191832064\", \"241533307563675648\", \"241538580479086592\", \"240806564712288256\", \"241529010050437120\", \"240812860022853632\", \"240810542867025920\", \"240808635444367360\", \"240815448281714688\", \"20302\", \"240813597926756352\", \"301\", \"302\", \"303\", \"601\", \"239013397163806720\", \"239011652547252224\", \"701\", \"238999200422039552\", \"239380636689895424\", \"239014028561747968\", \"901\", \"239387126641332224\"]','系统内置管理员，不可删除',1,10,'2024-03-12 17:22:53',17,'2025-01-01 22:46:01'),(15,'档案管理员','Admin [DMS]','[\"100\", \"201\", \"202\", \"20201\", \"20202\", \"20203\", \"2020301\", \"301\", \"302\", \"303\", \"901\"]','拥有档案管理系统最高权限',0,10,'2024-05-09 22:24:54',17,'2025-01-04 21:09:09'),(16,'教务管理员','Admin [EAS]','[\"100\", \"201\", \"203\", \"240826981330063360\", \"240828043986341888\", \"240827816525041664\", \"20301\", \"241531418730172416\", \"241531780807659520\", \"241535000191832064\", \"241533307563675648\", \"241538580479086592\", \"240806564712288256\", \"241529010050437120\", \"240812860022853632\", \"240810542867025920\", \"240808635444367360\", \"240815448281714688\", \"20302\", \"240813597926756352\", \"301\", \"302\", \"303\", \"901\"]','拥有电子教务系统最高权限',0,17,'2024-06-21 12:15:02',17,'2025-01-04 21:10:25');
/*!40000 ALTER TABLE `iims_integral_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_statistics`
--

DROP TABLE IF EXISTS `iims_integral_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_statistics` (
  `statistical_time` timestamp NOT NULL,
  `statistical_data` json DEFAULT NULL,
  `statistical_feedback` json DEFAULT NULL,
  PRIMARY KEY (`statistical_time`),
  UNIQUE KEY `iims_integral_statistics_pk_2` (`statistical_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_statistics`
--

LOCK TABLES `iims_integral_statistics` WRITE;
/*!40000 ALTER TABLE `iims_integral_statistics` DISABLE KEYS */;
INSERT INTO `iims_integral_statistics` VALUES ('2023-12-31 16:00:00','{\"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-03-14 16:00:00','{\"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-03-31 16:00:00','{\"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-05-01 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 1, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-05-22 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 1, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-05-31 16:00:00','{\"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-06-20 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 1, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-06-30 16:00:00','{\"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-08-27 16:00:00','{\"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-09-23 16:00:00','{\"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-09-30 16:00:00','{\"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-10-31 16:00:00','{\"logCount\": 0, \"dictCount\": 2, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2024-11-01 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 3}',NULL),('2024-11-02 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 1}',NULL),('2025-01-06 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 9, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-01-17 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 26, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-01-18 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 9, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-01-23 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 7, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-01-25 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 7, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-01-26 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 2, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-01-27 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 3, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-01-28 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 1, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-01-30 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 3, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-01-31 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 27, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-02-21 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 1, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-02-22 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 2, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-03-11 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 5, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-03-12 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 5, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-03-24 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 4, \"userCount\": 0, \"wikiCount\": 1, \"articleCount\": 0}',NULL),('2025-04-12 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 1, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 1}',NULL),('2025-04-18 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 4, \"userCount\": 0, \"wikiCount\": 2, \"articleCount\": 0}',NULL),('2025-05-08 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 2, \"userCount\": 0, \"wikiCount\": 1, \"articleCount\": 1}',NULL),('2025-05-23 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 9, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-06-06 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 1, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-09-18 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 6, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-09-19 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 2, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-09-23 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 2, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-09-25 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 1, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-09-28 16:00:00','{\"logCount\": 0, \"dictCount\": 0, \"fileCount\": 3, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-09-30 16:00:00','{\"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-10-03 16:00:00','{\"logCount\": 14, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-10-04 16:00:00','{\"logCount\": 254, \"dictCount\": 0, \"fileCount\": 1, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-10-14 16:00:00','{\"logCount\": 73, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-10-16 16:00:00','{\"logCount\": 75, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-10-17 16:00:00','{\"logCount\": 5, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-10-18 16:00:00','{\"logCount\": 297, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL),('2025-10-20 16:00:00','{\"logCount\": 14, \"dictCount\": 0, \"fileCount\": 0, \"userCount\": 0, \"wikiCount\": 0, \"articleCount\": 0}',NULL);
/*!40000 ALTER TABLE `iims_integral_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_warehouse`
--

DROP TABLE IF EXISTS `iims_integral_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_warehouse` (
  `id` bigint NOT NULL,
  `item_type` smallint DEFAULT NULL COMMENT '系统类型：ES：0、IIMS：1、DMS：2、EAS：3',
  `file_name` varchar(120) DEFAULT NULL COMMENT '文件初始名称',
  `file_rename` varchar(120) DEFAULT NULL COMMENT '文件重命名',
  `file_bucket` varchar(90) DEFAULT NULL COMMENT '文件存储桶',
  `file_key` varchar(255) DEFAULT NULL COMMENT '存放文件MD5值',
  `file_path` varchar(255) DEFAULT NULL COMMENT '文件存储路径',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小',
  `file_type` varchar(60) DEFAULT NULL COMMENT '文件类型',
  `file_date` timestamp NULL DEFAULT NULL COMMENT '文件日期',
  `file_abstract` varchar(255) DEFAULT NULL COMMENT '文件摘要',
  `file_status` tinyint(1) DEFAULT '0' COMMENT '删除：-1、未使用：0、已使用：1',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `iims_integral_warehouse_pk` (`file_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存放文件服务信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_warehouse`
--

LOCK TABLES `iims_integral_warehouse` WRITE;
/*!40000 ALTER TABLE `iims_integral_warehouse` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_integral_warehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_wiki`
--

DROP TABLE IF EXISTS `iims_integral_wiki`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_wiki` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(120) NOT NULL DEFAULT '' COMMENT '标题',
  `cover` bigint NOT NULL COMMENT '封面：文件ID',
  `summary` varchar(300) DEFAULT '' COMMENT '摘要',
  `weight` int unsigned NOT NULL DEFAULT '0' COMMENT '权重，用于是否置顶（0: 未置顶；>0: 参与置顶，权重值越高越靠前）',
  `type` tinyint DEFAULT '0' COMMENT '知识库类型：0 企业，1 个人',
  `is_publish` tinyint NOT NULL DEFAULT '1' COMMENT '是否发布：0：未发布 1：已发布',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志位：0：未删除 1：已删除',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `update_time` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=298270653696577537 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='知识库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_wiki`
--

LOCK TABLES `iims_integral_wiki` WRITE;
/*!40000 ALTER TABLE `iims_integral_wiki` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_integral_wiki` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iims_integral_wiki_catalog`
--

DROP TABLE IF EXISTS `iims_integral_wiki_catalog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iims_integral_wiki_catalog` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `wiki_id` bigint unsigned NOT NULL COMMENT '知识库id',
  `doc_id` bigint unsigned DEFAULT NULL COMMENT '文章id',
  `type` tinyint DEFAULT NULL COMMENT '文件类型：0 文章、1 文档',
  `title` text NOT NULL COMMENT '标题',
  `level` tinyint NOT NULL DEFAULT '1' COMMENT '目录层级',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '父目录id',
  `sort` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '排序',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志位：0：未删除 1：已删除',
  `is_embedding` tinyint DEFAULT '0' COMMENT '是否向量化：0 未向量化、1 向量化',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp NOT NULL COMMENT '创建时间',
  `update_by` bigint NOT NULL COMMENT '更新者',
  `update_time` timestamp NOT NULL COMMENT '最后一次更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sort` (`sort`) USING BTREE,
  KEY `idx_wiki_id` (`wiki_id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=338665673452556289 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='知识库目录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iims_integral_wiki_catalog`
--

LOCK TABLES `iims_integral_wiki_catalog` WRITE;
/*!40000 ALTER TABLE `iims_integral_wiki_catalog` DISABLE KEYS */;
/*!40000 ALTER TABLE `iims_integral_wiki_catalog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'iims'
--

--
-- Dumping routines for database 'iims'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-31 22:21:19
