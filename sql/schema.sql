-- ============================================
-- Ethereal 社交应用 - 数据库DDL建表脚本
-- ============================================

-- 请先手动创建数据库：CREATE DATABASE IF NOT EXISTS frients DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 然后选择该数据库（USE frients;）执行以下脚本

-- 1. 用户表 t_user
CREATE TABLE `t_user` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `phone`           VARCHAR(20)     NOT NULL COMMENT '手机号',
    `nickname`        VARCHAR(50)     DEFAULT NULL COMMENT '昵称',
    `avatar`          VARCHAR(500)    DEFAULT NULL COMMENT '头像URL',
    `gender`          TINYINT         DEFAULT NULL COMMENT '性别 1男 2女',
    `birthday`        DATE            DEFAULT NULL COMMENT '出生日期',
    `bio`             VARCHAR(500)    DEFAULT NULL COMMENT '个人简介',
    `profession`      VARCHAR(100)    DEFAULT NULL COMMENT '职业',
    `city`            VARCHAR(50)     DEFAULT NULL COMMENT '城市',
    `longitude`       DECIMAL(10,7)   DEFAULT NULL COMMENT '经度',
    `latitude`        DECIMAL(10,7)   DEFAULT NULL COMMENT '纬度',
    `verified`        TINYINT         DEFAULT 0 COMMENT '认证状态 0未认证 1已认证',
    `vibe_score`      INT             DEFAULT 0 COMMENT '魅力值',
    `vip_level`       TINYINT         DEFAULT 0 COMMENT 'VIP等级 0普通',
    `status`          TINYINT         DEFAULT 1 COMMENT '状态 1正常 2封禁',
    `wx_openid`       VARCHAR(100)    DEFAULT NULL COMMENT '微信openid',
    `qq_openid`       VARCHAR(100)    DEFAULT NULL COMMENT 'QQ openid',
    `last_login_time` DATETIME        DEFAULT NULL,
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_city` (`city`),
    KEY `idx_gender` (`gender`),
    KEY `idx_location` (`latitude`, `longitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 用户兴趣标签表 t_user_tag
CREATE TABLE `t_user_tag` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `user_id`         BIGINT          NOT NULL,
    `tag_name`        VARCHAR(50)     NOT NULL COMMENT '标签名称',
    `tag_category`    VARCHAR(30)     DEFAULT NULL COMMENT '标签分类',
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣标签';

-- 3. 用户照片表 t_user_photo
CREATE TABLE `t_user_photo` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `user_id`         BIGINT          NOT NULL,
    `photo_url`       VARCHAR(500)    NOT NULL,
    `sort_order`      INT             DEFAULT 0 COMMENT '排序(0为主图)',
    `audit_status`    TINYINT         DEFAULT 0 COMMENT '审核 0待审 1通过 2拒绝',
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户照片';

-- 4. 喜欢记录表 t_user_like
CREATE TABLE `t_user_like` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `user_id`         BIGINT          NOT NULL COMMENT '发起者',
    `target_user_id`  BIGINT          NOT NULL COMMENT '被喜欢者',
    `action_type`     TINYINT         NOT NULL COMMENT '1喜欢 2超级喜欢 3跳过',
    `is_mutual`       TINYINT         DEFAULT 0 COMMENT '是否互相喜欢',
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_target` (`user_id`, `target_user_id`),
    KEY `idx_target` (`target_user_id`),
    KEY `idx_mutual` (`is_mutual`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='喜欢记录';

-- 5. 会话表 t_conversation
CREATE TABLE `t_conversation` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `user_id_a`       BIGINT          NOT NULL COMMENT '用户A(id较小)',
    `user_id_b`       BIGINT          NOT NULL COMMENT '用户B(id较大)',
    `last_msg_content` VARCHAR(500)   DEFAULT NULL,
    `last_msg_time`   DATETIME        DEFAULT NULL,
    `unread_count_a`  INT             DEFAULT 0,
    `unread_count_b`  INT             DEFAULT 0,
    `status`          TINYINT         DEFAULT 1 COMMENT '1正常 2已关闭',
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_users` (`user_id_a`, `user_id_b`),
    KEY `idx_user_a` (`user_id_a`),
    KEY `idx_user_b` (`user_id_b`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';

-- 6. 聊天消息表 t_chat_message
CREATE TABLE `t_chat_message` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `conversation_id` BIGINT          NOT NULL,
    `sender_id`       BIGINT          NOT NULL,
    `msg_type`        TINYINT         DEFAULT 1 COMMENT '1文本 2图片 3语音 4系统',
    `content`         TEXT            NOT NULL,
    `is_read`         TINYINT         DEFAULT 0,
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_conv_id` (`conversation_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息';

-- 7. 动态表 t_moment
CREATE TABLE `t_moment` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `user_id`         BIGINT          NOT NULL,
    `content`         TEXT            DEFAULT NULL,
    `images`          JSON            DEFAULT NULL COMMENT '图片URL数组',
    `longitude`       DECIMAL(10,7)   DEFAULT NULL,
    `latitude`        DECIMAL(10,7)   DEFAULT NULL,
    `location_name`   VARCHAR(100)    DEFAULT NULL,
    `like_count`      INT             DEFAULT 0,
    `comment_count`   INT             DEFAULT 0,
    `audit_status`    TINYINT         DEFAULT 0 COMMENT '审核 0待审 1通过 2拒绝',
    `status`          TINYINT         DEFAULT 1 COMMENT '1正常 2删除',
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_user_id` (`user_id`),
    KEY `idx_location` (`latitude`, `longitude`),
    KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态表';

-- 8. 动态评论表 t_moment_comment
CREATE TABLE `t_moment_comment` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `moment_id`       BIGINT          NOT NULL,
    `user_id`         BIGINT          NOT NULL,
    `content`         VARCHAR(500)    NOT NULL,
    `parent_id`       BIGINT          DEFAULT 0 COMMENT '父评论id',
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_moment_id` (`moment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态评论';

-- 9. 动态点赞表 t_moment_like
CREATE TABLE `t_moment_like` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `moment_id`       BIGINT          NOT NULL,
    `user_id`         BIGINT          NOT NULL,
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_moment_user` (`moment_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞';

-- 10. 管理员表 t_admin
CREATE TABLE `t_admin` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `username`        VARCHAR(50)     NOT NULL,
    `password`        VARCHAR(200)    NOT NULL COMMENT 'BCrypt加密',
    `role`            VARCHAR(20)     DEFAULT 'operator',
    `status`          TINYINT         DEFAULT 1,
    `created_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 11. 系统配置表 t_system_config
CREATE TABLE `t_system_config` (
    `id`              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    `config_key`      VARCHAR(100)    NOT NULL,
    `config_value`    TEXT            NOT NULL,
    `description`     VARCHAR(200)    DEFAULT NULL,
    `updated_at`      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';
