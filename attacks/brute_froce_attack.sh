#!/bin/bash

URL="https://localhost:8080/api/users/login/psw"

USERNAME="admin"

PASSWORDS=(
  "admin"
  "password"
  "123456"
  "admin123"
  "Admin123"
  "qwerty"
  "letmein"
  "root"
  "test123"
  "Admin123!"
)

echo "Starting brute-force attempt simulation..."
echo

i=1
for PASS in "${PASSWORDS[@]}"
do
  echo "[$i] Trying password: $PASS"

  RESPONSE=$(curl -k -s -w "\nHTTP_STATUS:%{http_code}" \
    -X POST "$URL" \
    -H "Content-Type: application/json" \
    -d "{
      \"username\": \"$USERNAME\",
      \"password\": \"$PASS\"
    }")

  BODY=$(echo "$RESPONSE" | sed '$d')
  STATUS=$(echo "$RESPONSE" | tail -n1 | cut -d':' -f2)

  echo "    Status: $STATUS"
  echo "    Response: $BODY"
  echo

  ((i++))
  sleep 0.5
done

echo "Brute-force simulation finished."
