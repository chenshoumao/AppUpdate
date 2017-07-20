/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50087
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50087
File Encoding         : 65001

Date: 2017-07-20 18:43:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for land_version
-- ----------------------------
DROP TABLE IF EXISTS `land_version`;
CREATE TABLE `land_version` (
  `key` varchar(255) default NULL,
  `original_version` varchar(255) default NULL,
  `new_version` varchar(255) default NULL,
  `update_time` datetime default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of land_version
-- ----------------------------

-- ----------------------------
-- Table structure for ship
-- ----------------------------
DROP TABLE IF EXISTS `ship`;
CREATE TABLE `ship` (
  `name` varchar(255) default NULL,
  `size` varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of ship
-- ----------------------------
INSERT INTO `ship` VALUES ('big', '1000');
INSERT INTO `ship` VALUES ('fff', '100');

-- ----------------------------
-- Table structure for ship_update_logs
-- ----------------------------
DROP TABLE IF EXISTS `ship_update_logs`;
CREATE TABLE `ship_update_logs` (
  `update_type` varchar(255) default NULL,
  `original_version` varchar(255) default NULL,
  `new_version` varchar(255) default NULL,
  `create_time` datetime default NULL,
  `update_time` datetime default NULL,
  `update_state` varchar(255) default NULL,
  `description` varchar(255) default NULL,
  `is_over` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of ship_update_logs
-- ----------------------------
INSERT INTO `ship_update_logs` VALUES ('ditu', '1.0.0.0_ditu_release_20170713', '1.0.0.1_ditu_release_20170719', '2017-07-20 18:06:17', '2017-07-20 18:16:32', '等待岸端数据反馈...', null, '1');
INSERT INTO `ship_update_logs` VALUES ('ditu', '1.0.0.1_ditu_release_20170719', null, '2017-07-20 18:16:51', null, '等待岸端数据反馈...', null, '0');
INSERT INTO `ship_update_logs` VALUES ('haitu', '1.0.0.0_haitu_release_20170713', null, '2017-07-20 18:17:42', null, '等待岸端数据反馈...', null, '0');

-- ----------------------------
-- Table structure for ship_version
-- ----------------------------
DROP TABLE IF EXISTS `ship_version`;
CREATE TABLE `ship_version` (
  `key` varchar(255) default NULL,
  `original_version` varchar(255) default NULL,
  `new_version` varchar(255) default NULL,
  `update_time` datetime default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ship_version
-- ----------------------------

-- ----------------------------
-- Table structure for update_logs
-- ----------------------------
DROP TABLE IF EXISTS `update_logs`;
CREATE TABLE `update_logs` (
  `ip` varchar(255) default NULL,
  `update_type` varchar(255) default NULL,
  `original_version` varchar(255) default NULL,
  `new_version` varchar(255) default NULL,
  `update_time` datetime default NULL,
  `update_state` int(1) default NULL,
  `description` varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of update_logs
-- ----------------------------
INSERT INTO `update_logs` VALUES ('localhost', 'ditu', '1.0.0.0_ditu_release_20170713', '1.0.0.1_ditu_release_20170719', '2017-07-20 18:04:43', '1', '打包成功');
INSERT INTO `update_logs` VALUES ('localhost', 'ditu', '1.0.0.0_ditu_release_20170713', '1.0.0.1_ditu_release_20170719', '2017-07-20 18:06:17', '1', '打包成功');
INSERT INTO `update_logs` VALUES ('localhost', 'ditu', '1.0.0.1_ditu_release_20170719', '1.0.0.1_ditu_release_20170719', '2017-07-20 18:16:51', '0', '版本一致，无需更新');
INSERT INTO `update_logs` VALUES ('localhost', 'haitu', '1.0.0.0_haitu_release_20170713', '1.0.0.1_haitu_release_20170717', '2017-07-20 18:17:42', '0', '版本存在依赖，需要把 底图版本 更新');
SET FOREIGN_KEY_CHECKS=1;
