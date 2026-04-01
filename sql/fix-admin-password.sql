-- 若库里已是旧占位哈希导致无法登录，在 frients 库执行本脚本（密码：admin123）
USE frients;
UPDATE `t_admin`
SET `password` = '$2b$10$n1KYIq8TMu1oEnDwRz2B1.d3vY.5kjYozcg2O.8j3cogWTHw0ooD.'
WHERE `username` = 'admin';
