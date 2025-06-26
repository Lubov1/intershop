# keycloak-setup.sh
#!/bin/bash
set -e

until /opt/keycloak/bin/kcadm.sh config credentials \
        --server http://localhost:8080 \
        --realm master \
        --user  "$KEYCLOAK_ADMIN" \
        --password "$KEYCLOAK_ADMIN_PASSWORD" > /dev/null 2>&1
do
  echo "⏳ Waiting for Keycloak..."
  sleep 5
done
echo "✅ Keycloak is up"

EXISTS=$(/opt/keycloak/bin/kcadm.sh get clients -r master --fields clientId \
          | grep -o '"intershop"' || true)

if [ -z "$EXISTS" ]; then
  echo "➕ Creating client 'intershop'"
  /opt/keycloak/bin/kcadm.sh create clients -r master \
      -s clientId=intershop \
      -s enabled=true \
      -s protocol=openid-connect \
      -s publicClient=false \
      -s secret=intershop-secret \
      -s serviceAccountsEnabled=true \
      -s redirectUris='["http://localhost:8180/*"]'
else
  echo "ℹ️  Client 'intershop' already exists"
fi
