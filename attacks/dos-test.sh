#!/bin/bash

URL="https://localhost:8080/api/content/songs"
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImNyZWF0ZWQiOjE3NzAwODQwODE4MzcsImV4cCI6MTc3MDA4NzY4MX0.0n5TG3WunAZQl3L8D7n7M2mgc3CIXXKpLOeIOQYx8Wk"

echo "Launching 1000 parallel requests..."

for i in {1..2000}
do
   # The '&' at the end makes it run in background (Parallel)
   curl -k -s -o /dev/null -w "Request $i: %{http_code}\n" \
     -H "Authorization: Bearer $TOKEN" \
     $URL &
done

# Wait for all background requests to finish
wait
echo "Attack finished."