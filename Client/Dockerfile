FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /workspace/app

# Копировать только файл pom.xml и кэшировать зависимости
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw
COPY pom.xml .
COPY src src

# Сборка приложения с пропуском тестов
RUN ./mvnw install -DskipTests -X


# Используйте официальный образ Java для запуска jar файла
FROM openjdk:21-slim
WORKDIR /app

# Копирование собранного jar файла из предыдущего шага
COPY --from=build /workspace/app/target/*.jar app.jar

# Запуск приложения
ENTRYPOINT ["java","-jar","app.jar"]
