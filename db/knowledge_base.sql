/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.0.161
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : 192.168.0.161:3306
 Source Schema         : dmp

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 15/09/2020 14:12:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for knowledge_base
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_base`;
CREATE TABLE `knowledge_base`  (
  `id` bigint(11) NOT NULL COMMENT '主键',
  `ask_value` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '提问',
  `ans_value` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '回复',
  `hit` int(11) NOT NULL COMMENT '命中次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
