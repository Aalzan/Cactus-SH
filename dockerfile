# Используем образ Gradle с OpenJDK
FROM gradle:7.4.0-jdk17 AS build

# Установим рабочую директорию
WORKDIR /app

# Копируем файлы Gradle
COPY build.gradle settings.gradle ./
# Копируем все остальные файлы
COPY src ./src

# Запускаем сборку
RUN ./gradlew build -x test --no-daemon

# Используем минимальный образ OpenJDK для выполнения
FROM openjdk:17-jdk-slim

# Указываем том для временных файлов
VOLUME /tmp

# Копируем собранный .jar файл из предыдущего этапа
COPY --from=build /app/build/libs/*.jar app.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "/app.jar"]
