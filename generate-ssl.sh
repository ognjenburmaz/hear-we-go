#!/bin/bash

# 1. Install/Update local CA
mkcert -install

# 2. Generate the Leaf Certificate (For the services)
echo "Generating Certificate..."
mkcert -key-file certs/key.pem -cert-file certs/cert.pem \
  localhost 127.0.0.1 ::1 \
  api-gateway \
  discovery-service \
  users-service \
  content-service \
  ratings-service \
  subscriptions-service \
  notification-service \
  recommendation-service \
  analytics-service

# 3. Create the Keystore (Identity)
# This creates the 'PrivateKeyEntry' (Who I am)
echo "Packaging Identity..."
openssl pkcs12 -export \
  -in certs/cert.pem \
  -inkey certs/key.pem \
  -out certs/keystore.p12 \
  -name springboot \
  -passout pass:password123

# 4. Import the mkcert Root CA into the file as a 'TrustedCertEntry'
# This tells Java: "Trust anyone signed by mkcert"
echo "Importing Root CA..."
CAROOT=$(mkcert -CAROOT)
keytool -import -trustcacerts -noprompt \
  -alias mkcert-root \
  -file "$CAROOT/rootCA.pem" \
  -keystore certs/keystore.p12 \
  -storepass password123

echo "Done! Keystore generated with Chain and Root CA."