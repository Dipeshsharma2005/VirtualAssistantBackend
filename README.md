# 🖥 Virtual Assistant Backend

Spring Boot backend for the **Virtual Assistant App**.  
Handles authentication, JWT, assistant customization, and AI chat response management.

---

## 🌍 Live Backend URL
**Deployed on Render:**  
➡️ https://virtualassistantbackend-oefv.onrender.com  

⚠️ **Note:** The backend service is currently **temporarily unavailable**. It will be restored shortly.  

**Frontend Repo:**  
🔗 https://github.com/Dipeshsharma2005/VirtualAssistantFrontend

---

## 🚀 Features

- ⚙️ Built with **Spring Boot 3**
- 🔐 Secure login using **JWT Authentication**
- 🗄 **PostgreSQL** database integration
- 👤 **User registration & login**
- 🎨 **Assistant customization** (name + avatar)
- 🤖 **AI chat endpoints** powered by Gemini API

---

## 🧰 Tech Stack

| Layer | Technology |
|-------|-------------|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA |
| Build Tool | Maven |
| Utility | Lombok |

---

## ⚡ Setup Instructions

### 1️⃣ Clone Repository
```bash
git clone https://github.com/Dipeshsharma2005/VirtualAssistantBackend.git
cd virtual-assistant
```

---

### 2️⃣ Configure Environment Variables

Update the existing file:

```
src/main/resources/application.properties
```

Replace values with your own credentials:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}

JWT_SECRET_KEY=${JWT_KEY}

cloudinary.cloud-name=${CLOUD_NAME}
cloudinary.api-key=${CLOUD_API_KEY}
cloudinary.api-secret=${CLOUD_API_SECRET}

gemini.api.key=${GEMINI_KEY}
```

💡 You can set these values using environment variables or directly inside the file for local testing.

---

### 3️⃣ Run Backend
```bash
./mvnw spring-boot:run
```

**Server runs at:**  
➡️ http://localhost:8080
---

## 🔑 API Endpoints

### 🧾 Auth
| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/signin` | Login & receive JWT |

---

### 👤 User
| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/api/users/current` | Get logged-in user |
| PUT | `/api/users/update/{id}` | Update assistant name/image |

---

### 🤖 Assistant
| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/api/users/ask?userId={id}` | Ask Gemini AI |

---

## ☁️ Deployment (Render)

- Deploy using **Render Web Service**
- Add required **environment variables**
- Ensure correct **PORT configuration**

---

## 🧪 API Testing in Postman

### ✳️ Signup Request
<img src="https://github.com/user-attachments/assets/32c316bd-1a56-4627-bfa7-30ab8f6df074" alt="Signup in Postman" width="800"/>

### 💬 Asking Gemini AI
<img src="https://github.com/user-attachments/assets/e40ee6dd-2721-4061-8694-f5223bef32fb" alt="Asking in Postman" width="800"/>

---

## 🧠 Architecture Overview
```
Frontend (React)
        │
        │  JWT Auth + REST APIs
        ▼
Spring Boot Backend
        │
 ┌───────────────┬───────────────┐
 ▼               ▼               ▼
PostgreSQL   Cloudinary     Gemini API
(Database)   (Images)       (AI Chat)
```

---

## 🧑‍💻 Author

**Dipesh Sharma**

---

⭐ **If you like this project, give it a star!**
