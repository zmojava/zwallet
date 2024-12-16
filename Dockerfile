# Используем официальный образ OpenJDK для выполнения приложения
FROM openjdk:17-jdk-alpine

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем файл с артефактом в контейнер
COPY target/ZWallet-0.0.1-SNAPSHOT.jar /app/app.jar

# Открываем порт 8080 для приложения
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
