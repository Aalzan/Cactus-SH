# Используем Gradle как базовый образ для сборки
FROM gradle:7.4.0-jdk17 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы проекта в контейнер
COPY . .

# Выполняем сборку приложения
RUN ./gradlew clean build -x test --no-daemon

# Используем минимальный JDK образ для финального контейнера
FROM openjdk:17-jdk-slim

# Копируем скомпилированный JAR файл из предыдущего этапа
COPY --from=build /app/build/libs/*.jar app.jar

# Определяем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "/app.jar"]
