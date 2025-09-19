# -------- Build Stage --------
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven files first (better caching)
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build app (skip tests)
RUN ./mvnw clean package -DskipTests


# -------- Run Stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render assigns dynamic PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT}"]
