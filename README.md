# 🖥 Virtual Assistant Backend

Spring Boot backend for the **Virtual Assistant App**.  
Handles authentication, JWT, assistant customization, and AI chat response management.

---

## 🌍 Live Backend URL
**Deployed on Render:**  
➡️ [https://virtualassistantbackend-oefv.onrender.com](https://virtualassistantbackend-oefv.onrender.com)

**Frontend Repo:**  
🔗 [virtual-assistant-frontend](https://github.com/yourusername/virtual-assistant-frontend)

---

## 🚀 Features

- ⚙️ Built with **Spring Boot 3**
- 🔐 Secure login using **JWT Authentication**
- 🗄 **PostgreSQL** database integration
- 👤 **User registration & login**
- 🎨 **Assistant customization** (name + avatar)
- 🤖 **AI chat endpoints** powered by Gemini API
- 🐳 **Dockerfile** for easy deployment (Render, Heroku, etc.)

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

### 1️⃣ Clone & Navigate
```bash
git clone https://github.com/yourusername/virtual-assistant.git
cd backend
```

### 2️⃣ Configure Database & Keys

Create a file at:
```
src/main/resources/application.properties
```

Add the following:
```properties
spring.application.name=VirtualAssistant

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

JWT_SECRET_KEY=${JWT_KEY}

cloudinary.cloud-name=${CLOUD_NAME}
cloudinary.api-key=${CLOUD_API_KEY}
cloudinary.api-secret=${CLOUD_API_SECRET}

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

gemini.api.key=${GEMINI_KEY}
```

### 3️⃣ Run Backend
```bash
./mvnw spring-boot:run
```

**Server runs at:**  
➡️ [http://localhost:8080](http://localhost:8080)

---

## 🔑 API Endpoints

### 🧾 Auth
| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/signin` | Login & receive JWT |

### 👤 User
| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/api/users/current` | Get logged-in user |
| PUT | `/api/users/update/{id}` | Update assistant name/image |

### 🤖 Assistant
| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/api/users/ask?userId={id}` | Ask Gemini AI |

---

## ☁️ Deployment

### 🐳 Using Docker
```bash
docker build -t virtual-assistant-backend .
docker run -p 8080:8080 virtual-assistant-backend
```

### 🚀 On Render
- Select **Docker deploy option**
- Add **environment variables**
- Expose `$PORT`

---

## 🧪 API Testing in Postman

### ✳️ Signup Request
<img src="https://github.com/user-attachments/assets/32c316bd-1a56-4627-bfa7-30ab8f6df074" alt="Signup in Postman" width="800"/>

### 💬 Asking Gemini AI
<img src="https://github.com/user-attachments/assets/e40ee6dd-2721-4061-8694-f5223bef32fb" alt="Asking in Postman" width="800"/>

---

## 🧠 Architecture Overview
```
┌────────────┐      JWT       ┌──────────────┐
│  Frontend  │  <──────────>  │  Spring Boot │
│ (React)    │                 │  Backend     │
└────────────┘                 │  (REST API)  │
        │                      └──────┬──────┘
        │  PostgreSQL + Cloudinary    │
        ▼                             ▼
 ┌──────────────┐           ┌────────────────┐
 │  Database    │           │  Gemini AI API │
 └──────────────┘           └────────────────┘
```

---

## 🧑‍💻 Author

**Dipesh Sharma**  


---

⭐ **If you like this project, give it a star!**
