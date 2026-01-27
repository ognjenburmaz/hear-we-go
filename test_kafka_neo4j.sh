#!/bin/bash

# Test script to populate Neo4j via Kafka events
# Aleksa version – safe ordering

# 0️⃣ Variables
KAFKA_CONTAINER="streaming-kafka"
CONTENT_TOPIC="content-created"
USER_ACTIVITY_TOPIC="user-activity"

echo "⏳ Cleaning Neo4j database..."
docker exec -i streaming-neo4j cypher-shell "MATCH (n) DETACH DELETE n;"
echo "✅ Neo4j cleaned."

sleep 2

# 1️⃣ Produce content-created event
echo "⏳ Producing content-created event..."
docker exec -i $KAFKA_CONTAINER rpk topic produce $CONTENT_TOPIC <<'EOF'
{
  "id": "song1",
  "title": "Test Song",
  "type": "SONG",
  "artistId": "artist1",
  "artistName": "Test Artist",
  "genre": "Rock"
}
EOF
echo "✅ content-created event sent."

sleep 2

# 2️⃣ Produce user subscribes to genre
echo "⏳ Producing GENRE_SUBSCRIBED event..."
docker exec -i $KAFKA_CONTAINER rpk topic produce $USER_ACTIVITY_TOPIC <<'EOF'
{
  "userId": "user1",
  "eventType": "GENRE_SUBSCRIBED",
  "payload": {
    "genre": "Rock"
  }
}
EOF
echo "✅ GENRE_SUBSCRIBED event sent."

sleep 2

# 3️⃣ Produce RATED event
echo "⏳ Producing RATED event..."
docker exec -i $KAFKA_CONTAINER rpk topic produce $USER_ACTIVITY_TOPIC <<'EOF'
{
  "userId": "user1",
  "eventType": "RATED",
  "payload": {
    "songId": "song1",
    "value": 5
  }
}
EOF
echo "✅ RATED event sent."

sleep 2

# 4️⃣ Instructions for Neo4j verification
echo ""
echo "🎉 Done producing events."
echo "Check Neo4j to see nodes and relationships:"
echo "MATCH (u:User)-[r:RATED]->(s:Song)<-[:CREATED]-(a:Artist),"
echo "      (s)-[:BELONGS_TO]->(g:Genre)"
echo "RETURN u, r, s, a, g;"
