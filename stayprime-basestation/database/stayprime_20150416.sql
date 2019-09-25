CREATE DATABASE  IF NOT EXISTS `stayprime` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `stayprime`;
-- MySQL dump 10.13  Distrib 5.5.38, for debian-linux-gnu (i686)
--
-- Host: 127.0.0.1    Database: stayprime
-- ------------------------------------------------------
-- Server version	5.5.38-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ads`
--

DROP TABLE IF EXISTS `ads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ads` (
  `ad_id` int(11) NOT NULL AUTO_INCREMENT,
  `client_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(400) DEFAULT NULL,
  `category` int(11) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `source` varchar(1024) DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ad_id`),
  KEY `client_constraint` (`client_id`),
  KEY `category_constraint` (`category`),
  CONSTRAINT `client_constraint` FOREIGN KEY (`client_id`) REFERENCES `clients` (`client_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `adzones`
--

DROP TABLE IF EXISTS `adzones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adzones` (
  `adzone_id` int(11) NOT NULL AUTO_INCREMENT,
  `contract_id` int(11) NOT NULL,
  `course` int(11) DEFAULT NULL,
  `hole` int(11) DEFAULT NULL,
  `name` varchar(64) NOT NULL,
  `shape` longtext,
  `shape_alpha` double DEFAULT NULL,
  `color` int(11) DEFAULT NULL,
  `extra_info` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`adzone_id`),
  KEY `contract_fk_constraint` (`contract_id`),
  CONSTRAINT `contract_fk_constraint` FOREIGN KEY (`contract_id`) REFERENCES `contracts` (`contract_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cart_info`
--

DROP TABLE IF EXISTS `cart_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cart_info` (
  `cart_number` int(11) NOT NULL,
  `mac_address` varchar(20) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `heading` float DEFAULT '0',
  `course_last_updated` timestamp NULL DEFAULT NULL,
  `location_last_updated` timestamp NULL DEFAULT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `pace_of_play` int(11) DEFAULT NULL,
  `playing_course` int(11) DEFAULT NULL,
  `playing_hole` int(11) DEFAULT NULL,
  `cart_mode` int(11) DEFAULT NULL,
  PRIMARY KEY (`cart_number`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cart_tracking`
--

DROP TABLE IF EXISTS `cart_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cart_tracking` (
  `cart_number` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `heading` float DEFAULT NULL,
  `speed` float DEFAULT NULL,
  PRIMARY KEY (`cart_number`,`timestamp`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cart_unit`
--

DROP TABLE IF EXISTS `cart_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cart_unit` (
  `mac_address` varchar(20) NOT NULL,
  `ip_address` varchar(20) DEFAULT NULL,
  `sim_ip` varchar(45) DEFAULT NULL,
  `sim_iccid` varchar(45) DEFAULT NULL,
  `firmware_version` varchar(32) DEFAULT NULL,
  `software_version` varchar(32) DEFAULT NULL,
  `ip_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `course_updated` timestamp NULL DEFAULT NULL,
  `pinlocation_updated` timestamp NULL DEFAULT NULL,
  `ads_updated` timestamp NULL DEFAULT NULL,
  `settings_updated` timestamp NULL DEFAULT NULL,
  `menu_updated` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`mac_address`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clients` (
  `client_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `details` varchar(400) DEFAULT NULL,
  `contact_info` varchar(400) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `report_prefs` int(11) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contracts`
--

DROP TABLE IF EXISTS `contracts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contracts` (
  `contract_id` int(11) NOT NULL AUTO_INCREMENT,
  `ad_id` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `plan` tinyint(4) NOT NULL,
  `hole_ad` varchar(128) CHARACTER SET latin1 DEFAULT NULL,
  `ad_zone` varchar(128) CHARACTER SET latin1 DEFAULT NULL,
  PRIMARY KEY (`contract_id`),
  KEY `ad_id_constraint` (`ad_id`),
  CONSTRAINT `ad_id_constraint` FOREIGN KEY (`ad_id`) REFERENCES `ads` (`ad_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_info`
--

DROP TABLE IF EXISTS `course_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_info` (
  `course_id` int(10) unsigned NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(255) NOT NULL,
  `contact_info` varchar(255) NOT NULL,
  `units` int(11) NOT NULL DEFAULT '0',
  `logo_image` varchar(255) DEFAULT NULL,
  `map_image` varchar(255) DEFAULT NULL,
  `corner_top_left` varchar(64) DEFAULT NULL,
  `corner_top_right` varchar(64) DEFAULT NULL,
  `corner_bottom_left` varchar(64) DEFAULT NULL,
  `corner_bottom_right` varchar(64) DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`course_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_objects`
--

DROP TABLE IF EXISTS `course_objects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_objects` (
  `id` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `location` varchar(64) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `image_center` varchar(32) DEFAULT NULL,
  `image_meter_width` double DEFAULT NULL,
  `image_min_pixel_width` int(11) DEFAULT NULL,
  `shape` longtext,
  `shape_alpha` double DEFAULT NULL,
  `show_distance` tinyint(1) DEFAULT NULL,
  `show_name` tinyint(1) DEFAULT NULL,
  `show_shape` tinyint(1) DEFAULT NULL,
  `show_coordinate` tinyint(1) DEFAULT NULL,
  `closed_shape` tinyint(1) DEFAULT NULL,
  `color` int(11) DEFAULT NULL,
  `hit_message` varchar(255) DEFAULT NULL,
  `static_object` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course_settings`
--

DROP TABLE IF EXISTS `course_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course_settings` (
  `name` varchar(32) NOT NULL,
  `value` varchar(255) NOT NULL,
  `last_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `courses` (
  `number` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(255) NOT NULL,
  `holes` int(11) NOT NULL,
  PRIMARY KEY (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `default_tees`
--

DROP TABLE IF EXISTS `default_tees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `default_tees` (
  `course` int(11) NOT NULL,
  `number` int(11) NOT NULL,
  `color` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  KEY `default_tees_fk` (`course`),
  CONSTRAINT `default_tees_fk` FOREIGN KEY (`course`) REFERENCES `courses` (`number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fnb_order`
--

DROP TABLE IF EXISTS `fnb_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fnb_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `current_hole` int(11) NOT NULL,
  `cart_number` int(11) NOT NULL,
  `item` varchar(45) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `order_created` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hole_objects`
--

DROP TABLE IF EXISTS `hole_objects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hole_objects` (
  `course` int(11) NOT NULL,
  `hole` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `location` varchar(64) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `image_center` varchar(32) DEFAULT NULL,
  `image_meter_width` double DEFAULT NULL,
  `image_min_pixel_width` int(11) DEFAULT NULL,
  `shape` longtext,
  `shape_alpha` double DEFAULT NULL,
  `show_distance` tinyint(1) DEFAULT NULL,
  `show_name` tinyint(1) DEFAULT NULL,
  `show_shape` tinyint(1) DEFAULT NULL,
  `show_coordinate` tinyint(1) DEFAULT NULL,
  `closed_shape` tinyint(1) DEFAULT NULL,
  `color` int(11) DEFAULT NULL,
  `hit_message` varchar(255) DEFAULT NULL,
  `static_object` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`course`,`hole`,`id`) USING BTREE,
  CONSTRAINT `hole_objects_fk` FOREIGN KEY (`course`, `hole`) REFERENCES `holes` (`course`, `hole`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `holes`
--

DROP TABLE IF EXISTS `holes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `holes` (
  `course` int(11) NOT NULL,
  `hole` int(11) NOT NULL,
  `par` int(11) NOT NULL,
  `pace_of_play` int(11) NOT NULL,
  `cart_path_only` tinyint(1) NOT NULL,
  `description` varchar(255) NOT NULL,
  `pro_tips` varchar(1000) NOT NULL,
  `map_image` varchar(255) DEFAULT NULL,
  `flyover_image` varchar(255) DEFAULT NULL,
  `corner_top_left` varchar(64) DEFAULT NULL,
  `corner_top_right` varchar(64) DEFAULT NULL,
  `corner_bottom_left` varchar(64) DEFAULT NULL,
  `corner_bottom_right` varchar(64) DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `stroke_index` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`course`,`hole`),
  CONSTRAINT `holes_ibfk_1` FOREIGN KEY (`course`) REFERENCES `courses` (`number`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `huts_info`
--

DROP TABLE IF EXISTS `huts_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `huts_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `hut_number` int(10) NOT NULL,
  `phone_number` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `from_number` varchar(45) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `holes` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hut_number_UNIQUE` (`hut_number`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='	';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu_items`
--

DROP TABLE IF EXISTS `menu_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu_items` (
  `item_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(127) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `price` float NOT NULL,
  `picture` varchar(127) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `active` tinyint(1) NOT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`)
) ENGINE=MyISAM AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pin_location`
--

DROP TABLE IF EXISTS `pin_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pin_location` (
  `course` int(11) NOT NULL,
  `hole` int(11) NOT NULL,
  `location` varchar(64) DEFAULT NULL,
  `last_updated` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`course`,`hole`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `round`
--

DROP TABLE IF EXISTS `round`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `round` (
  `round_id` int(11) NOT NULL AUTO_INCREMENT,
  `cart_number` int(11) DEFAULT NULL,
  `roundNum` int(11) DEFAULT NULL,
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`round_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `score_card`
--

DROP TABLE IF EXISTS `score_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `score_card` (
  `scorecard_id` int(11) NOT NULL AUTO_INCREMENT,
  `player_names` varchar(45) NOT NULL,
  `scores` varchar(145) NOT NULL DEFAULT '0',
  `cart_number` int(11) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`scorecard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_request`
--

DROP TABLE IF EXISTS `service_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_request` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cart_number` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `type` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `reply_time` timestamp NULL DEFAULT NULL,
  `replied_by` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=157 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_settings`
--

DROP TABLE IF EXISTS `system_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_settings` (
  `name` varchar(32) NOT NULL,
  `value` varchar(255) NOT NULL,
  `last_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tee_boxes`
--

DROP TABLE IF EXISTS `tee_boxes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tee_boxes` (
  `course` int(11) NOT NULL DEFAULT '0',
  `hole` int(11) NOT NULL DEFAULT '0',
  `number` int(11) NOT NULL,
  `color` int(11) NOT NULL,
  `distance` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`course`,`hole`,`number`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_login`
--

DROP TABLE IF EXISTS `user_login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_login` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_UNIQUE` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-16 12:22:26
