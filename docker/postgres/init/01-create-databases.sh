#!/bin/sh
set -eu

create_database() {
  database_name="$1"

  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres <<-EOSQL
    SELECT 'CREATE DATABASE "${database_name}"'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${database_name}')\gexec
EOSQL
}

create_database "$KEYCLOAK_DB_NAME"
create_database "$USER_DB_NAME"
create_database "$INCIDENT_DB_NAME"
create_database "$COMMENT_DB_NAME"
create_database "$NOTIFICATION_DB_NAME"
create_database "$CHAT_DB_NAME"
