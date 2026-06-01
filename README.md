# RSA Secure Chat & MITM Simulator

**Description:** 
An end-to-end Java simulation of RSA cryptography. It features a secure GUI-based chat system with real-time key exchange, alongside an adversarial Man-in-the-Middle server that intercepts traffic and actively cracks private keys using Fermat's Factorization to demonstrate mathematical vulnerabilities.

## Components
- **Server.java**: The MITM / Cracker Server.
- **ChatClient3.java & ChatClient4.java**: Secure GUI-based chat clients with security dashboards.
- **RSAUtil.java**: Core encryption/decryption logic using BigInteger.
- **UserA.java & UserB.java**: Industrial-standard RSA examples using JCA.
- **RSA.java**: Standalone utility for manual RSA calculation and testing.

## How to Run
1. Compile all files: `javac *.java`
2. Start the server: `java Server`
3. Start the clients: `java ChatClient3` and `java ChatClient4`
