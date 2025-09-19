# 🖥 Virtual Assistant Backend


Spring Boot backend for the **Virtual Assistant App**.  
Handles authentication, JWT, assistant customization, and AI chat response management.  

## 🌍 Live Backend URL
Deployed on Render:  
➡️ https://virtualassistantbackend-oefv.onrender.com

🔗 Frontend Repo: [virtual-assistant-frontend](https://github.com/Dipeshsharma2005/VirtualAssistantFrontend)  

---

## 🚀 Features
- Spring Boot 3
- Spring Security with JWT Authentication
- PostgreSQL database
- User registration & login
- Assistant customization (name + avatar)
- AI assistant chat endpoints
- Dockerfile for deployment (Render/Heroku/others)

---

## ⚙️ Tech Stack
- Java 17
- Spring Boot 3
- Spring Security + JWT
- JPA + PostgreSQL
- Maven
- Lombok

---

## ⚡ Setup Instructions

### 1️⃣ Clone & Navigate
```bash
git clone https://github.com/yourusername/virtual-assistant.git
cd backend


2️⃣ Configure Database & Keys

Create src/main/resources/application.properties:

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


3️⃣ Run Backend
./mvnw spring-boot:run

Server runs at:
➡️ http://localhost:8080

🔑 API Endpoints
Auth

POST /api/auth/signup → Register new user

POST /api/auth/signin → Login & receive JWT

User

GET /api/users/current → Get logged-in user

PUT /api/users/update/{id} → Update assistant name/image

Assistant

POST /api/users/ask?userId={id} → Ask Gemini AI


☁️ Deployment
Using Docker
docker build -t virtual-assistant-backend .
docker run -p 8080:8080 virtual-assistant-backend

On Render

Select Docker deploy option

Add environment variables

Expose $PORT

<img width="1920" height="1080" alt="Asking in postman" src="https://github.com/user-attachments/assets/e40ee6dd-2721-4061-8694-f5223bef32fb" />
<img width="1920" height="1080" alt="Signup in postman" src="https://github.com/user-attachments/assets/32c316bd-1a56-4627-bfa7-30ab8f6df074" />


