# 📝 Blog Backend API

Бэкенд-приложение для блога, переписанное с использованием **Spring Boot 3**. Приложение упаковано в Executable JAR, запускается во встроенном контейнере Tomcat, использует H2 базу данных и предоставляет REST API.

## 🛠 Стек технологий

*   **Язык:** Java 21
*   **Фреймворк:** Spring Boot 3.2+ (Web, JDBC, Test)
*   **Сборка:** Gradle
*   **База данных:** H2 (Embedded, in-memory)
*   **Сервер приложений:** Embedded Tomcat (встроен в Spring Boot)
*   **Тестирование:** JUnit 5, Mockito, Spring Boot Test

---

## 🚀 Как запустить проект

### Вариант 1: Локальный запуск (через Gradle)

1.  Убедитесь, что установлены **Java 21** и **Gradle**.
2.  Выполните команду в корне проекта:
    ```bash
    ./gradlew bootRun
    ```
3.  Бэкенд будет доступен по адресу: `http://localhost:8080`

### Вариант 2: Запуск Executable JAR
1. Сборка готового к проду jar файла со встроенным сервером.
2. Сборка приложения:
    ```bash
    ./gradlew bootJar
    ```
    Файл появится в директории build/libs/.
3. Запуск:
    ```bash
    java -jar build/libs/blog-backend-0.0.1-SNAPSHOT.jar
    ```

## 🧪 Тестирование

Проект покрыт юнит-тестами (Service layer) и интеграционными тестами (Controller + DAO + DB).

Запуск всех тестов:
```bash
./gradlew test
```

Покрытие кода (Code Coverage)
В проекте настроен плагин JaCoCo. Чтобы сгенерировать HTML-отчет о покрытии:

```bash
./gradlew test jacocoTestReport
```
Отчет будет доступен по пути: build/reports/jacoco/test/html/index.html.


## 🔌 API Endpoints

Все запросы возвращают данные в формате JSON.

### 📄 Посты (`/api/posts`)

| Метод | URL | Описание |
| :--- | :--- | :--- |
| `GET` | `/api/posts?pageNumber=1&pageSize=5&search=...` | Получение списка постов с пагинацией и поиском. |
| `POST` | `/api/posts` | Создание нового поста. |
| `POST` | `/api/posts/{id}` | Получение одного поста по ID. |
| `PUT` | `/api/posts/{id}` | Обновление поста. |
| `DELETE` | `/api/posts/{id}` | Удаление поста. |
| `POST` | `/api/posts/{id}/likes` | Лайкнуть пост (инкремент лайков). |
| `PUT` | `/api/posts/{id}/image` | Загрузить картинку (Multipart File). |
| `GET` | `/api/posts/{id}/image` | Получить картинку (возвращает байты image/jpeg). |

### 💬 Комментарии (`/api/posts/{postId}/comments`)

| Метод | URL | Описание |
| :--- | :--- | :--- |
| `GET` | `/api/posts/{id}/comments` | Получить все комментарии поста. |
| `POST` | `/api/posts/{id}/comments` | Добавить комментарий. |
| `PUT` | `.../comments/{commentId}` | Редактировать комментарий. |
| `DELETE` | `.../comments/{commentId}` | Удалить комментарий. |
