# Указываем базовый образ с JDK
FROM eclipse-temurin:17-jdk-alpine AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы проекта
COPY . .

# Устанавливаем права на выполнение gradlew
RUN chmod +x gradlew

# Собираем приложение
RUN ./gradlew clean build -x test

# Создаем финальный образ
FROM eclipse-temurin:17-jre-alpine

# Указываем рабочую директорию
WORKDIR /app

# Копируем скомпилированный JAR файл из предыдущего образа
COPY --from=build /app/build/libs/*.jar app.jar

# Указываем, что приложение будет слушать на порту 8080
EXPOSE 8080

# Устанавливаем переменные окружения
ENV JAVA_OPTS=""

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "-Dserver.port=8080", "app.jar"]
