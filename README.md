# RSA Secure Chat & MITM Simulator

## Project Overview
This project is an educational cybersecurity laboratory designed to demonstrate the end-to-end lifecycle of the **RSA Cryptosystem**—from secure key exchange and transmission to mathematical vulnerability analysis and interception.

The system simulates two users communicating via a secure chat interface while a "Hacker Server" (Man-in-the-Middle) attempts to compromise their privacy in real-time.

---

## 🎯 Project Objectives
1. **Implementation**: Build a functional, multi-threaded secure chat system using RSA.
2. **Visual Education**: Provide a "Security Console" that reveals the underlying mathematical operations (keys, ciphertexts).
3. **Vulnerability Demonstration**: Show how mathematical shortcuts can bypass encryption when security parameters (prime numbers) are poorly chosen.

---

## 🔬 The Factorization Attack (Fermat's Method)
The core of the "Hacker Server" is the implementation of **Fermat's Factorization Method**. 

### The Math:
RSA security relies on the difficulty of factoring a large number $n$ into its two prime factors $p$ and $q$. Fermat's method is highly efficient when $p$ and $q$ are close to each other. It works on the principle that any odd integer can be expressed as the difference of two squares:
$$n = a^2 - b^2 = (a-b)(a+b)$$

1. The server starts with $a = \lceil \sqrt{n} \rceil$.
2. It checks if $b^2 = a^2 - n$ is a perfect square.
3. If not, it increments $a$ and tries again.
4. Once a square is found, the primes are recovered as $p = a-b$ and $q = a+b$.

### Usage in this Project:
In `Server.java`, the MITM agent intercepts the public keys. Because the primes used in the chat demo are small for illustrative purposes, the server factors $n$ in **under 1ms**, calculates the private key $d$, and begins decrypting private messages immediately.

---

## ⚖️ Simulation vs. Real World
| Feature | Educational Simulation (This Project) | Real-World Cryptography (HTTPS/SSH) |
| :--- | :--- | :--- |
| **Key Size** | Small (Demonstration primes) | Large (RSA-2048 or RSA-4096) |
| **Attack Type** | Mathematical Factorization | Key Replacement / MITM via ARP Spoofing |
| **Identity** | None (Vulnerable to MITM) | Digital Certificates & Trust Authorities (CA) |
| **Complexity** | Fermat's Method | General Number Field Sieve (GNFS) |

---

## 🧠 Key Learnings
1. **The Handshake Vulnerability**: Encryption is only secure if the identity of the person sending the key is verified. Without certificates, a MITM attack is trivial.
2. **Prime Selection Matters**: In RSA, choosing primes that are too close together makes the modulus $n$ vulnerable to Fermat's factorization, regardless of how large the numbers are.
3. **BigInteger Math**: Learned to handle large-scale modular exponentiation and modular inverse operations essential for cryptographic safety.
4. **Network Proxying**: Understanding how a server can act as a transparent proxy to sniff and manipulate traffic.

---

## 🚀 Components
- **Server.java**: The MITM / Cracker Server.
- **ChatClient3.java & ChatClient4.java**: Secure GUI clients with real-time security dashboards.
- **RSAUtil.java**: The encryption engine handling BigInteger logic.
- **UserA.java & UserB.java**: References for JCA (Java Cryptography Architecture) standards.
