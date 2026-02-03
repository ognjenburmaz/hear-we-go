#!/bin/bash

# 0️⃣ Configuration
KAFKA_CONTAINER="streaming-kafka"
NEO4J_CONTAINER="streaming-neo4j"
CONTENT_TOPIC="content-created"
USER_ACTIVITY_TOPIC="user-activity"

echo "--------------------------------------------------"
echo "🧹 1. Cleaning Neo4j Database..."
docker exec -i $NEO4J_CONTAINER cypher-shell "MATCH (n) DETACH DELETE n;"
echo "✅ Neo4j cleaned."

# 1️⃣ Produce content-created event (One-liner format)
echo "--------------------------------------------------"
echo "⏳ Producing content-created event..."
echo '{"id":"song1","title":"Test Song","type":"SONG","artistId":"artist1","artistName":"Test Artist","genre":"Rock"}' | \
docker exec -i $KAFKA_CONTAINER rpk topic produce $CONTENT_TOPIC
echo "✅ content-created event sent."

sleep 2

# 2️⃣ Produce user activity (One-liner format)
echo "--------------------------------------------------"
echo "⏳ Producing RATED event..."
echo '{"userId":"user1","eventType":"RATED","payload":{"songId":"song1","value":5}}' | \
docker exec -i $KAFKA_CONTAINER rpk topic produce $USER_ACTIVITY_TOPIC
echo "✅ RATED event sent."

echo "--------------------------------------------------"
echo "⏳ Waiting for Recommendation Service to process (5s)..."
sleep 5

# 3️⃣ Verify in Neo4j
echo "--------------------------------------------------"
echo "🔍 VERIFYING NEO4J DATA..."
echo "--------------------------------------------------"

# This query joins User -> Song -> Artist to prove the logic worked
docker exec -i $NEO4J_CONTAINER cypher-shell --format plain "
MATCH (u:User {id: 'user1'})-[r:RATED]->(s:Song {id: 'song1'})<-[:CREATED]-(a:Artist)
RETURN 'SUCCESS: User ' + u.id + ' rated \"' + s.title + '\" (' + r.value + ' stars) by Artist: ' + a.name AS result;
"

echo "--------------------------------------------------"
echo "🎉 If you see 'SUCCESS', the JSON truncation is fixed!"