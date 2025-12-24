# --- Этап 1: Сборка (Build) ---
# Используем образ с Maven и Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Создаем рабочую папку внутри контейнера
WORKDIR /app

# Копируем файл конфигурации сборки и исходный код
COPY pom.xml .
COPY src ./src

# Запускаем сборку WAR файла, пропуская тесты (чтобы ускорить процесс)
RUN mvn clean package -DskipTests

# --- Этап 2: Запуск (Run) ---
# Используем Tomcat 10 (обязательно 10+ для Spring 6/Jakarta EE)
FROM tomcat:10.1-jdk21

# Удаляем стандартные приложения Tomcat (Manager, Docs, Examples), чтобы было чисто
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем собранный WAR файл из этапа сборки в папку веб-приложений Tomcat.
# Важно: называем его ROOT.war, чтобы приложение открывалось по адресу localhost:8080/
# (без названия проекта в пути)
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Открываем порт 8080
EXPOSE 8080

# Запускаем Tomcat
CMD ["catalina.sh", "run"]