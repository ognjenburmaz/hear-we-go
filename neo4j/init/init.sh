#!/bin/bash
set -e

echo "▶ Starting Neo4j…"
/startup/docker-entrypoint.sh neo4j &

echo "⏳ Waiting for Neo4j to accept connections…"

# Wait for Neo4j Bolt to be ready
until cypher-shell "RETURN 1" >/dev/null 2>&1; do
  sleep 2
done

echo "✅ Neo4j is ready. Applying schema…"
cypher-shell < /var/lib/neo4j/import/schema.cypher

echo "🎉 Schema applied successfully"

wait
