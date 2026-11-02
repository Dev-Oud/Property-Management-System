#!/bin/sh
set -eu

: "${PGHOST:?Set PGHOST}"
: "${PGDATABASE:?Set PGDATABASE}"
: "${PGUSER:?Set PGUSER}"
: "${PGPASSWORD:1010}"

backup_dir="${BACKUP_DIR:-./backups}"
retention_days="${BACKUP_RETENTION_DAYS:-14}"
mkdir -p "$backup_dir"
timestamp=$(date -u +%Y%m%dT%H%M%SZ)
output="$backup_dir/${PGDATABASE}-${timestamp}.dump"

pg_dump --format=custom --no-owner --no-acl --file="$output"
find "$backup_dir" -type f -name "${PGDATABASE}-*.dump" -mtime "+$retention_days" -delete
printf 'Database backup written: %s\n' "$output"
