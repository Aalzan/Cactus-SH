# Этап сборки
FROM amazoncorretto:17 AS build
FROM ghcr.io/railwayapp/nixpacks:ubuntu-1727136237

# Устанавливаем рабочий каталог для сборки
WORKDIR /demo1

# Копируем файлы проекта
COPY . /demo1
# Устанавливаем dos2unix
RUN yum install -y dos2unix

# Преобразуем файл gradlew в Unix-формат
RUN dos2unix gradlew
RUN ls -la /app/src/main/resources/images

# Даем разрешение на выполнение скрипта gradlew
RUN chmod +x gradlew
RUN chmod -R 755 /app/src/main/resources/images

RUN ls -la src/main/resources/images

# Сборка проекта
RUN ./gradlew clean build -x check -x test --stacktrace
# Финальный этап
FROM amazoncorretto:17

# Устанавливаем рабочий каталог для приложения
WORKDIR /app

# Копируем скомпилированный jar файл из предыдущего этапа
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Сообщаем Docker о том, что контейнер слушает на порту 8080
EXPOSE 8080

# Определяем команду для запуска приложения
CMD ["java", "-jar", "/app/app.jar"]
