# Установите образ Gradle с OpenJDK
FROM gradle:jdk17 AS build

# Установите рабочую директорию
WORKDIR /app

# Копируем файл build.gradle и загружаем зависимости
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build --no-daemon -x test

# Используем минимальный образ OpenJDK для запуска
FROM openjdk:17-jdk-slim

# Указываем том для временных файлов
VOLUME /tmp

# Копируем собранный .jar файл из предыдущего этапа
COPY --from=build /app/build/libs/*.jar app.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "/app.jar"]
