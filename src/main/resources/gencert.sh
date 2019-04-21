#!/usr/bin/env bash
# >>>>>>>>>>>>>>>>>> Root Certificate <<<<<<<<<<<<<<<<<<<<<<<<
# Generate root certificate private key: ca.key
openssl genrsa -out ca.key 2048

# Generate a self-signed root certificate: ca.crt
openssl req -new -key ca.key -x509 -days 720 -out ca.crt -subj /C=US/ST=CA/L="Orange Cove"/O="Amdexa"/CN="Amdexa Root CA"

# >>>>>>>>>>>>>>>>>> Server Certificate <<<<<<<<<<<<<<<<<<<<<<<<
# Generate server certificate private key: ca.key
openssl genrsa -out server.key 2048

# Generate server certificate request: server.csr
openssl req -new -nodes  -days 720  -key server.key -out server.csr -subj /C=US/ST=CA/L="Orange Cove"/O="Amdexa"/CN=twister.sequoia.amdexa.com

#signed server certificate: server.crt
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt

# >>>>>>>>>>>>>>>>>> Client Certificate <<<<<<<<<<<<<<<<<<<<<<<<
# Generate client certificate private key: ca.key
openssl genrsa -out client.key 2048

# Generate a client certificate request: client.csr
openssl req -new -nodes  -days 720  -key client.key -out client.csr -subj /C=US/ST=CA/L="Orange Cove"/O="Amdexa"/CN=jail-mgmt.sequoia.amdexa.com

#signed client certificate: client.crt
openssl x509 -req -in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out client.crt

#save as .p12
openssl pkcs12 -export -in server.crt -inkey server.key -name auth -out server.p12


#https://blog.codecentric.de/en/2018/08/x-509-client-certificates-with-spring-security/