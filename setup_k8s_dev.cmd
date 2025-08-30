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
    --set global.postgresql.auth.database=%DB_NAME% ^
    --set redis.auth.password=%REDIS_PASSWORD% ^
    --set minio.auth.rootUser=%MINIO_USERNAME% ^
    --set minio.auth.rootPassword=%MINIO_PASSWORD%

helm upgrade --install communication helm/Communication ^
    --namespace communication ^
    --create-namespace ^
    --set rabbitmq.auth.username=%RABBITMQ_USER% ^
    --set rabbitmq.auth.password=%RABBITMQ_PASSWORD% ^
    --set rabbitmq.auth.su_username=%RABBITMQ_SU_USER% ^
    --set rabbitmq.auth.su_password=%RABBITMQ_SU_PASSWORD%

echo To make cluster services work with local environment use 'telepresence connect --context=minikube' (if using minikube) command