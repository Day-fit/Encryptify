@echo off
IF NOT EXIST .env exit /b 1
IF NOT EXIST "./rendered" mkdir rendered

FOR /F "usebackq tokens=* delims=" %%A IN (.env) DO (
    FOR /F "tokens=1,2 delims==" %%B IN ("%%A") DO (
        SET "%%B=%%C"
    )
)

helm template data helm/Data ^
  --set global.postgresql.auth.username=%DB_USERNAME% ^
  --set global.postgresql.auth.password=%DB_PASSWORD% ^
  --set global.postgresql.auth.postgresPassword=%DB_SU_PASSWORD% ^
  --set global.postgresql.auth.database=%DB_NAME% > rendered\Data.yaml

kubectl apply -f rendered\Data.yaml
cmd /c netstat -ano | findstr /R ":5432[^0-9]" >nul && echo Port 5432 (PostgreSQL is probably running, skipping) || start "Port Forward" cmd /k kubectl port-forward svc/data-postgresql 5432:5432