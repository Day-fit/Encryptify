@echo off
IF NOT EXIST .env exit /b 1

FOR /F "usebackq tokens=* delims=" %%A IN (.env) DO (
    FOR /F "tokens=1,2 delims==" %%B IN ("%%A") DO (
        SET "%%B=%%C"
    )
)

helm upgrade --install data helm/Data ^
    --namespace data ^
    --create-namespace ^
    --set global.postgresql.auth.username=%DB_USERNAME% ^
    --set global.postgresql.auth.password=%DB_PASSWORD% ^
    --set global.postgresql.auth.postgresPassword=%DB_SU_PASSWORD% ^
    --set global.postgresql.auth.database=%DB_NAME%

helm upgrade --install rabbitmq helm/Communication ^
    --namespace communication ^
    --create-namespace ^
    --set rabbitmq.auth.username=%RABBITMQ_USER% ^
    --set rabbitmq.auth.password=%RABBITMQ_PASSWORD% ^
    --set rabbitmq.auth.su_username=%RABBITMQ_SU_USER% ^
    --set rabbitmq.auth.su_password=%RABBIT_SU_PASSWORD%

cmd /c netstat -ano | findstr /R ":5432[^0-9]" >nul && echo Port 5432 (PostgreSQL is probably running, skipping) || start "Port Forward" cmd /k kubectl port-forward svc/data-postgresql -n data 5432:5432
cmd /c netstat -ano | findstr /R ":5672[^0-9]" >nul && echo Port 5672 (RabbitMQ is probably running, skipping) || start "Port Forward" cmd /k kubectl port-forward svc/rabbitmq -n communication 5672:5672
cmd /c netstat -ano | findstr /R ":15672[^0-9]" >nul && echo Port 15672 (RabbitMQ UI is probably running, skipping) || start "Port Forward" cmd /k kubectl port-forward svc/rabbitmq -n communication 15672:15672