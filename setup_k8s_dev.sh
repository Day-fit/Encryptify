[ -f .env ] || exit 1
set -a
. .env
set +a

helm template data helm/Data \
  --set global.postgresql.auth.username="$DB_USERNAME" \
  --set global.postgresql.auth.password="$DB_PASSWORD" \
  --set global.postgresql.auth.postgresPassword="$DB_SU_PASSWORD" \
  --set global.postgresql.auth.database="$DB_NAME" > rendered/Data.yaml
kubectl apply -f rendered/Data.yaml
kubectl port-forward svc/data-postgresql 5432:5432