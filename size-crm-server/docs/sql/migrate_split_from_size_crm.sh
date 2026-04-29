#!/usr/bin/env bash
# 从单库 size_crm 拆出系统表 / 流程引擎表到 size_crm_system、size_crm_workflow，
# 并从 size_crm 中删除已迁移表（仅保留 crm_*）。
# 用法：在仓库根或本目录执行 ./migrate_split_from_size_crm.sh
# 可通过环境变量覆盖：MYSQL_HOST MYSQL_USER MYSQL_PASSWORD MYSQL_PORT

set -euo pipefail

# 非交互 shell 常无 mysql 的 alias/PATH，可用 MYSQL_BIN 显式指定
MYSQL_BIN="${MYSQL_BIN:-/usr/local/mysql/bin/mysql}"
if ! command -v "$MYSQL_BIN" >/dev/null 2>&1; then
  MYSQL_BIN="$(command -v mysql || true)"
fi
if [[ -z "$MYSQL_BIN" || ! -x "$MYSQL_BIN" ]]; then
  echo "未找到 mysql 客户端，请设置环境变量 MYSQL_BIN=/path/to/mysql" >&2
  exit 127
fi

HOST="${MYSQL_HOST:-127.0.0.1}"
PORT="${MYSQL_PORT:-3306}"
USER="${MYSQL_USER:-root}"
PASSWORD="${MYSQL_PASSWORD:-linsz99@}"
SRC_DB="size_crm"
SYS_DB="size_crm_system"
WF_DB="size_crm_workflow"

mysql_exec() {
  "$MYSQL_BIN" -h"$HOST" -P"$PORT" -u"$USER" -p"$PASSWORD" "$@"
}

echo ">>> 1. 创建目标库（若不存在）"
mysql_exec -e "
CREATE DATABASE IF NOT EXISTS \`${SYS_DB}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS \`${WF_DB}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
"

echo ">>> 2. 迁移 sys_* -> ${SYS_DB}"
while IFS= read -r t; do
  [[ -z "$t" ]] && continue
  echo "    表: $t"
  mysql_exec -e "
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS \`${SYS_DB}\`.\`${t}\`;
CREATE TABLE \`${SYS_DB}\`.\`${t}\` LIKE \`${SRC_DB}\`.\`${t}\`;
INSERT INTO \`${SYS_DB}\`.\`${t}\` SELECT * FROM \`${SRC_DB}\`.\`${t}\`;
SET FOREIGN_KEY_CHECKS=1;
"
done < <(mysql_exec -N -e "SELECT table_name FROM information_schema.tables WHERE table_schema='${SRC_DB}' AND table_name LIKE 'sys_%' ORDER BY table_name")

echo ">>> 3. 迁移 ACT_* / FLW_* -> ${WF_DB}"
while IFS= read -r t; do
  [[ -z "$t" ]] && continue
  echo "    表: $t"
  mysql_exec -e "
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS \`${WF_DB}\`.\`${t}\`;
CREATE TABLE \`${WF_DB}\`.\`${t}\` LIKE \`${SRC_DB}\`.\`${t}\`;
INSERT INTO \`${WF_DB}\`.\`${t}\` SELECT * FROM \`${SRC_DB}\`.\`${t}\`;
SET FOREIGN_KEY_CHECKS=1;
"
done < <(mysql_exec -N -e "SELECT table_name FROM information_schema.tables WHERE table_schema='${SRC_DB}' AND (table_name LIKE 'ACT\_%' OR table_name LIKE 'FLW\_%') ORDER BY table_name")

echo ">>> 4. 校验行数（源库 vs 目标库）"
mysql_exec -e "
SELECT 'sys_rowcheck' AS k,
  (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${SRC_DB}' AND table_name LIKE 'sys_%') AS src_tables,
  (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${SYS_DB}' AND table_name LIKE 'sys_%') AS dst_tables;
"
mysql_exec -e "
SELECT 'engine_rowcheck' AS k,
  (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${SRC_DB}' AND (table_name LIKE 'ACT\\_%' OR table_name LIKE 'FLW\\_%')) AS src_tables,
  (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${WF_DB}' AND (table_name LIKE 'ACT\\_%' OR table_name LIKE 'FLW\\_%')) AS dst_tables;
"

echo ">>> 5. 从 ${SRC_DB} 删除已迁移表（保留 crm_*）"
# 必须在同一 mysql 会话内 SET FOREIGN_KEY_CHECKS 与 DROP，否则外键表会报错。
{
  echo "SET FOREIGN_KEY_CHECKS=0;"
  mysql_exec -N -e "SELECT CONCAT('DROP TABLE IF EXISTS \`${SRC_DB}\`.\`', table_name, '\`;') FROM information_schema.tables WHERE table_schema='${SRC_DB}' AND table_name NOT LIKE 'crm\\_%' ORDER BY table_name DESC;"
  echo "SET FOREIGN_KEY_CHECKS=1;"
} | "$MYSQL_BIN" -h"$HOST" -P"$PORT" -u"$USER" -p"$PASSWORD"

echo ">>> 6. 最终 ${SRC_DB} 剩余表"
mysql_exec -e "SHOW TABLES FROM \`${SRC_DB}\`;"

echo ">>> 完成。请重启各微服务并确认连接串指向 ${SYS_DB} / ${WF_DB} / ${SRC_DB}。"
