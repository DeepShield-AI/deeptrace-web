-- 用户Token表，支持多种类型的Token
CREATE TABLE `user_token` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                              `user_id` BIGINT NOT NULL COMMENT '用户ID',
                              `token_type` VARCHAR(20) NOT NULL COMMENT 'Token类型：API_KEY-API密钥, JWT_REFRESH-JWT刷新令牌, ACCESS_TOKEN-访问令牌',
                              `token_value` VARCHAR(255) NOT NULL COMMENT 'Token值',
                              `token_name` VARCHAR(100) DEFAULT NULL COMMENT 'Token名称，用于标识用途',
                              `read_permission` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '读权限：0-无读权限, 1-有读权限',
                              `write_permission` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '写权限：0-无写权限, 1-有写权限',
                              `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已删除, 1-正常',
                              `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间，NULL表示永久有效',
                              `last_used_time` TIMESTAMP  DEFAULT NULL COMMENT '最后使用时间',
                              `create_time` TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `deleted_at` TIMESTAMP  DEFAULT NULL COMMENT '删除时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_token_value` (`token_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户Token表';