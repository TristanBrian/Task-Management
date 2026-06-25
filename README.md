# 📋 Task Management System

A full‑stack task management application built with **Spring Boot** (backend) and **Angular** (frontend), featuring JWT authentication, CRUD operations, and a clean Material Design UI.

---

## 🚀 Features

- **User Authentication** – Register and login with JWT tokens
- **Task CRUD** – Create, read, update, and delete tasks
- **Status Management** – Mark tasks as Pending, In Progress, or Completed
- **Filtering** – Filter tasks by status
- **Responsive UI** – Modern interface built with Angular Material
- **RESTful API** – Well‑documented endpoints with Swagger
- **Docker Support** – Run everything with a single command

---

## 🧰 Tech Stack

### Backend
- Java 11+
- Spring Boot 2.7.5
- Spring Security & JWT
- Spring Data JPA
- MariaDB / MySQL
- Maven
- Swagger / OpenAPI

### Frontend
- Angular 14+
- Angular Material
- RxJS
- TypeScript
- SCSS

---

## 📦 Prerequisites

- **Java 11+** – [Download](https://adoptium.net/)
- **Node.js 14+** – [Download](https://nodejs.org/)
- **MariaDB / MySQL** – [Installation guide](https://mariadb.org/download/)
- **Maven** – [Install](https://maven.apache.org/install.html) (or use the included Maven wrapper)
- **Angular CLI** (optional) – `npm install -g @angular/cli`

---

## ⚙️ Setup

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/task-management.git
cd task-management
```

### 2. Backend Setup

#### a) Configure the database
Create a database (e.g., `taskdb`) and a user with appropriate privileges.

```sql
CREATE DATABASE taskdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'taskuser'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON taskdb.* TO 'taskuser'@'localhost';
FLUSH PRIVILEGES;
```

#### b) Configure `application.properties`
Edit `backend/src/main/resources/application.properties` – use environment variables or placeholders.

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/taskdb?useSSL=false&serverTimezone=UTC}
spring.datasource.username=${DB_USERNAME:taskuser}
spring.datasource.password=${DB_PASSWORD:your_secure_password}
jwt.secret=${JWT_SECRET:mySuperSecretKey123!}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

#### c) Run the backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will start at `http://localhost:8080`.  
Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

---

### 3. Frontend Setup

#### a) Install dependencies
```bash
cd frontend
npm install
```

#### b) Configure API endpoint
Environment files are in `frontend/src/environments/`.  
For development, `environment.ts` already points to `http://localhost:8080/api`.

#### c) Run the frontend
```bash
npm start    # or ng serve
```

The frontend will be served at `http://localhost:4200`.

---

## 🐳 Docker (Alternative)

Run the entire stack with Docker Compose:

```bash
docker-compose up -d --build
```

- Frontend: `http://localhost`
- Backend API: `http://localhost:8080`
- Database: MySQL on port `3306`

---

## 🌐 Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/taskdb?...` | JDBC database URL |
| `DB_USERNAME` | `taskuser` | Database username |
| `DB_PASSWORD` | `your_secure_password` | Database password |
| `JWT_SECRET` | `mySuperSecretKey123!` | Secret key for signing JWTs |
| `JWT_EXPIRATION` | `86400000` | Token validity (ms) |

---

## 🧪 Testing

### Backend
```bash
cd backend
mvn test
```

### Frontend
```bash
cd frontend
ng test    # or npm test
```

---

## 📁 Project Structure

```
task-management/
├── backend/                     # Spring Boot
│   ├── src/main/java/...        # Java source
│   ├── src/main/resources/      # Properties, static
│   └── pom.xml
├── frontend/                    # Angular
│   ├── src/app/                 # Components, services
│   ├── src/environments/        # Environment config
│   ├── angular.json
│   └── package.json
├── docker-compose.yml
└── README.md
```

---

## 📬 API Endpoints

All endpoints are prefixed with `/api`.

| Method | Endpoint              | Description                |
|--------|-----------------------|----------------------------|
| POST   | `/auth/login`         | Login – returns JWT        |
| POST   | `/auth/register`      | Register new user          |
| GET    | `/tasks`              | Get all tasks for user     |
| GET    | `/tasks/status/{status}` | Filter tasks by status   |
| POST   | `/tasks`              | Create a new task          |
| PUT    | `/tasks/{id}`         | Update an existing task    |
| DELETE | `/tasks/{id}`         | Delete a task              |

> Full API documentation is available via Swagger at `/swagger-ui.html`.

---

## 🤝 Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`.
3. Commit your changes: `git commit -am 'Add some feature'`.
4. Push to the branch: `git push origin feature/your-feature`.
5. Open a Pull Request.

---

## 📄 License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.

---

## ✨ Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Angular](https://angular.io/)
- [Angular Material](https://material.angular.io/)
- [MariaDB](https://mariadb.org/)

---

**Happy coding!** 🚀