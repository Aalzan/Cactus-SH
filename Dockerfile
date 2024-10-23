# Этап сборки
FROM amazoncorretto:17 AS build

# Устанавливаем рабочий каталог для сборки
WORKDIR /app


# Копируем файлы проекта
COPY . /app
RUN ls /app/src/main/resources/images/

# Устанавливаем dos2unix
RUN yum install -y dos2unix

# Преобразуем файл gradlew в Unix-формат
RUN dos2unix gradlew

# Даем разрешение на выполнение скрипта gradlew
RUN chmod +x gradlew

# Сборка проекта
RUN ./gradlew build

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
