# 🐾 Pəncə (Paw) - Smart Lost & Found Pet Network

## 📌 Overview
**Pəncə** (meaning "Paw") is a community-driven backend service designed to reunite lost pets with their owners. It moves beyond traditional manual searching by utilizing a **passive participant network** and **Perceptual Hashing (pHash)** for intelligent image matching. 

Instead of relying solely on users actively searching a database, Pəncə is designed around a network effect: when a pet is reported lost, the system asynchronously notifies active users in that geographic area, turning the community into a real-time search network.

## 🎯 The Problem & The Solution
**The Problem:** Traditional methods of finding lost pets (posters, WhatsApp groups) are slow, localized, and rely heavily on manual effort. Standard image matching (like MD5) fails when a pet is photographed from different angles, lighting, or if their fur has grown.
**The Solution:** 1. **pHash Integration:** Uses Discrete Cosine Transform (DCT) to evaluate the structural shape of the pet rather than pixel-perfect details, allowing for highly accurate matching despite different lighting or camera angles.
2. **Instant Network Alerts:** Simulates a system where users in the vicinity are instantly notified via background threads, increasing the chances of a quick rescue.

## 🚀 Key Features
- **Smart Image Matching:** Compares uploaded images against the database using custom pHash logic and Hamming distance calculation.
- **Asynchronous Notifications:** Uses multithreading to dispatch alerts to nearby users without blocking the main application thread.
- **Clean Architecture:** Built strictly with Java and Spring Boot, implementing a structured Controller-Service-Repository pattern.
- **Pure Java Implementation:** Developed **without Lombok** to demonstrate a deep understanding of core Object-Oriented Programming (Encapsulation, Manual Builder Patterns, Constructors).

## 🛠️ Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 3 (REST API)
- **Database:** SQLite (Lightweight, zero-configuration setup)
- **Image Processing:** Custom Perceptual Hashing (pHash) Algorithm

## 🧠 Core Computer Science Concepts Applied
This project was built to apply and demonstrate advanced Java concepts:
- **Multithreading & Concurrency:** Utilized `ExecutorService`, `Callable`, `Future`, and `Consumer` in the `NotificationService` to handle asynchronous community alerts.
- **Object-Oriented Programming (OOP):** Strict use of Encapsulation, Polymorphism, and manual implementation of the **Builder Design Pattern** (e.g., in `Pet.java`).
- **Data Structures & Algorithms:** Applied 2D Arrays and complex loops for image processing and DCT conversion.
- **Generics & Collections:** Leveraged for type-safe DTOs (`MatchResultDTO<T>`) and efficient data manipulation via Java Streams and Lambdas.
- **Exception Handling:** Implemented global exception handling (`@ControllerAdvice`) and custom exceptions (`PetNotFoundException`) for robust API responses.
- **File I/O:** Managed multipart file uploads, processing, and local storage efficiently.

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/pets/lost` | Report a lost pet (Upload image & details) |
| `POST` | `/api/pets/found` | Report a found pet (Upload image & details) |
| `POST` | `/api/pets/match` | Upload an image to find a match via pHash |
| `GET`  | `/api/pets/lost` | Retrieve all lost pets |
| `GET`  | `/api/pets/found`| Retrieve all found pets |

## 🏃‍♂️ How to Run Locally

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/yourusername/pence-backend.git](https://github.com/yourusername/pence-backend.git)
   cd pence-backend
