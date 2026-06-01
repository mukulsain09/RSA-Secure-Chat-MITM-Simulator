# 🛡️ RSA Secure Chat & MITM Simulator

An advanced educational laboratory demonstrating the mechanics of RSA encryption, multi-threaded networking, and cryptographic vulnerabilities.

---

## 📖 Project Overview
This project simulates a real-world communication scenario where security and adversarial interception coexist. It consists of a **Secure Chat System** (Defenders) and a **Man-in-the-Middle Hacker Server** (Attacker). 

Unlike typical "hello world" encryption scripts, this project handles the entire lifecycle: **Key Generation -> Public Key Handshake -> Encrypted Transmission -> Packet Sniffing -> Mathematical Cryptanalysis.**

---

## 🎯 Core Objectives
- **Secure Communication**: Implement two-way encrypted chat using modular RSA logic.
- **Visual Cryptography**: Use a "Security Dashboard" to pull back the curtain on what happens to data during encryption.
- **Mathematical Exploitation**: Demonstrate that encryption is only as strong as its parameters by factoring the modulus in real-time.

---

## 🏗️ System Architecture
The project is built using a **Proxy Architecture**:
1. **The Clients** (`ChatClient3/4`): Connect to the Server. They generate their own RSA key pairs using `BigInteger` math.
2. **The Handshake**: Clients send their Public Keys $(e, n)$ to the server. The server relays them but stores a copy for analysis.
3. **The Secure Tunnel**: Once keys are swapped, clients encrypt messages using the peer's public key.
4. **The Interception**: The `Server` acts as a transparent relay (Sniffer), capturing every cipher-text packet.

---

## 🔬 The Attack: Fermat's Factorization Method
The highlight of the "Attacker" module is the implementation of Fermat's Factorization algorithm.

### The Mathematical Flaw:
RSA security depends on the assumption that factoring $n$ is hard. However, if the primes $p$ and $q$ are chosen too close to each other, $n$ can be factored easily using Fermat's observation:
$$n = (\frac{p+q}{2})^2 - (\frac{q-p}{2})^2$$

### How the Server Exploits This:
As soon as the Server intercepts a public key, it triggers a background thread that:
1. Performs square-root searches to find the "middle" of the primes.
2. Recovers $p$ and $q$ within milliseconds.
3. Computes the Totient $\phi(n) = (p-1)(q-1)$.
4. Derives the Private Key $d = e^{-1} \pmod{\phi(n)}$.
5. **Outcome**: The server can now decrypt every message the victims send, effectively "breaking" the encryption without the victims knowing.

---

## ⚖️ Simulation vs. Real-World MITM
| Feature | This Project (Educational) | Industry Standard (Real World) |
| :--- | :--- | :--- |
| **Prime Gap** | Small (Primes are close for demo) | Large & Random (Primes are far apart) |
| **Key Size** | ~12-16 bits | 2048 to 4096 bits |
| **MITM Method** | Direct Socket Interception | ARP Spoofing / DNS Hijacking |
| **Defense** | Manual Key Exchange | SSL/TLS Certificates (PKI) |

---

## 🧠 Key Learnings & Insights
1. **Handshake Vulnerability**: Learned that the most dangerous part of any protocol is the initial key swap. Without a "Trusted Third Party" (like a Certificate Authority), you can never be sure who you are talking to.
2. **The "BigInteger" Necessity**: Standard `int` and `long` types are useless for RSA. Understanding `modPow` and `modInverse` was critical for the math to work.
3. **Concurrency**: Implementing a "Sniffer" required multi-threading to ensure the attack logic didn't lag or crash the actual chat communication.
4. **Proxy Logic**: Built a transparent relay that manipulates data stream while maintaining a stable connection for the victims.

---

## 🚀 How to Run
1. **Compile**: `javac *.java`
2. **Host**: Run `java Server` (The Hacker Console)
3. **Connect**: Run `java ChatClient3` and `java ChatClient4`
4. **Analyze**: Watch the "Security Dashboard" in the clients and the "Cracked Key" output in the Server console.

---

## 🛠️ Project Structure
- **`Server.java`**: The MITM Server with cracking logic.
- **`ChatClient3/4.java`**: GUI clients with modern "Matrix" security logs.
- **`RSAUtil.java`**: Modular encryption/decryption engine.
- **`RSA.java`**: Manual RSA testing utility.
- **`UserA/B.java`**: Reference implementations for standard Java Crypto libraries.
