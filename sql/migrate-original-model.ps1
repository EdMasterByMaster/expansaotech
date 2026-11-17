$ErrorActionPreference = "Stop"

docker compose exec -T db /bin/bash -c '/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -C -b -i /sql/migration-original-model-to-unicode.sql'
if ($LASTEXITCODE -ne 0) {
    throw "A migração do schema original falhou."
}
