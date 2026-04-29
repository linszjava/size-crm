-- =============================================================================
-- 开发/联调：Camunda 表与当前引擎版本不一致（如 ENGINE-03017、缺列）时，清空引擎表后重启 size-crm-workflow，
-- 由 camunda.bpm.database.schema-update=true 按 7.x 全量重建。
-- 使用前确认库内无待保留的流程数据；生产环境须走评审后的备份与迁移方案。
-- =============================================================================
USE `size_crm_workflow`;

SET SESSION foreign_key_checks = 0;

DROP PROCEDURE IF EXISTS `tmp_drop_camunda_tables`;

DELIMITER //
CREATE PROCEDURE `tmp_drop_camunda_tables`()
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE t VARCHAR(128);
  DECLARE cur CURSOR FOR
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND (table_name REGEXP '^(ACT_|FLW_)');
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO t;
    IF done THEN
      LEAVE read_loop;
    END IF;
    SET @stmt = CONCAT('DROP TABLE IF EXISTS `', t, '`');
    PREPARE s FROM @stmt;
    EXECUTE s;
    DEALLOCATE PREPARE s;
  END LOOP;
  CLOSE cur;
END //
DELIMITER ;

CALL `tmp_drop_camunda_tables`();
DROP PROCEDURE IF EXISTS `tmp_drop_camunda_tables`;

SET SESSION foreign_key_checks = 1;
