#!/bin/bash

URL="https://localhost:8080/api/content/songs"
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImNyZWF0ZWQiOjE3Njg0MjA3MzYyMjMsImV4cCI6MTc2ODQyNDMzNn0.DjcgGJuA5JWPviHhw_Cq4NIvWDAoeQtbhtHID6AUaMM"

echo "Launching 10 parallel requests..."

for i in {1..10}
do
   # The '&' at the end makes it run in background (Parallel)
   curl -k -s -o /dev/null -w "Request $i: %{http_code}\n" \
     -H "Authorization: Bearer $TOKEN" \
     $URL &
done

# Wait for all background requests to finish
wait
echo "Attack finished."