# 📝 Blog Backend API

Бэкенд-приложение для блога, разработанное на **Java 21** с использованием **Spring Framework 6**.
Приложение предоставляет REST API для взаимодействия с фронтендом (React), хранит данные в БД **H2**, поддерживает загрузку изображений.

## 🛠 Стек технологий

*   **Язык:** Java 21
*   **Фреймворк:** Spring Framework 6.1.4 (Context, MVC, JDBC)
*   **Сборка:** Maven
*   **База данных:** H2 (Embedded, in-memory)
*   **Сервер приложений:** Apache Tomcat 10 (Docker) / Jetty 11 (Maven Plugin)
*   **Тестирование:** JUnit 5, Mockito, Spring Test, AssertJ
*   **Контейнеризация:** Docker, Docker Compose

---

## 🚀 Как запустить проект

### Вариант 1: Локальный запуск (через Maven)
Самый быстрый способ для разработки. Используется плагин `jetty-maven-plugin`.

1.  Убедитесь, что установлены **Java 21** и **Maven**.
2.  Выполните команду в корне проекта:
    ```bash
    mvn jetty:run
    ```
3.  Бэкенд будет доступен по адресу: `http://localhost:8080`

### Вариант 2: Запуск в Docker (Бэкенд)
**Предварительные требования:**
*   Установлен Docker и Docker Compose.

**Команды:**

1.  Сборка контейнера:
    ```bash
    docker build -t my-blog-backend .
    ```
    
2.   Запуск контейнера:
    ```bash
    docker run -p 8080:8080 my-blog-backend
    ```

4.  **Доступ:**
    *   ⚙️ **Бэкенд (API):** [http://localhost:8080](http://localhost:8080)


## 🧪 Тестирование

Проект покрыт юнит-тестами (Service layer) и интеграционными тестами (Controller + DAO + DB).

Запуск всех тестов:
```bash
mvn clean test
```

Покрытие кода (Code Coverage)
В проекте настроен плагин JaCoCo. Чтобы сгенерировать HTML-отчет о покрытии:

```bash
mvn clean test jacoco:report
```
Отчет будет доступен по пути: target/site/jacoco/index.html.


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
