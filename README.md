# Encryptify

**Encryptify** is a privacy-focused, **zero-knowledge**, cloud drive web application, implemented as microservices and deployed on Kubernetes using Helm. It leverages strong cryptographic practices and modern stack components to ensure data confidentiality and resilience, even against future threats.

---

##  Architecture & Tech Stack

- **Backend:** Java + Spring Boot (microservice architecture)
- **Frontend:** Next.js (in development)
- **Storage:**
  - PostgreSQL – main database
  - Redis – caching layer
  - MinIO – object storage (files)
- **Messaging & Integration:** RabbitMQ for asynchronous communication between services
- **Infrastructure:** Kubernetes (Helm charts) with GitOps pipeline (ArgoCD)

---

##  Core Features

- **Secure Authentication:** JWT issued via JWKS with key rotation (Ed25519)
- **Folder & File CRUD:** Create/read/delete operations for directories and files (update is partly done, but still in progress)
- **Encrypted Routing:** Email data is hashed; future enhancements include metadata stripping and filename encryption
- **Server-Side Security:**
  - AES-256 encryption for data—has strong quantum resistance due to doubling brute-force difficulty via Grover’s algorithm :contentReference[oaicite:2]{index=2}
  - Infrastructure designed for post-quantum readiness
- **Future Improvements:**
  - API for E2EE via external encryption service
  - Stripping metadata from client-side file uploads
  - Post-quantum key exchange / encryption when standards mature

---

##  Deployment & CI Strategy

- **Microservice-based repo layout:**
  - `core/`
  - `auth/`
  - `auth-lib/`
  - `email/`
  - `encryption/` (working on it!)
- **CI/CD:**
  - Build & test with GitHub Actions
  - Deploy with ArgoCD (Helm charts live in repo dedicated to manifests)
- **Secrets Management:** External Secrets Operator integrated with cloud (OCI) to securely inject secrets

---

##  Security Considerations

- **Zero-Knowledge Principles:**
  - Backend does not hold unencrypted payloads or user keys
  - JWKS allows stateless token validation with rotating keys (Ed25519)
- **Quantum-Resistance Approach:**
  - Use AES-256 for symmetric data
  - Track emerging post-quantum crypto standards (e.g., Kyber, Dilithium)
  - Future-proof architecture aligned with evolving NIST recommendations

---

##  License

Licensed under the **Apache License 2.0**. See `LICENSE.txt` for details.

---

##  For Developers & Recruiters

Encryptify demonstrates:
- Real-world microservices with secure JWT/JWKS setup
- End-to-end data flow using Postgres, Redis, MinIO, and messaging via RabbitMQ
- Kubernetes provisioning with Helm + GitOps (ArgoCD)
- Strategic awareness of cryptographic future challenges, including post-quantum readiness

This project is ideal for developers seeking production-ready systems with a strong focus on security and modern infrastructure paradigms.
