# Sushi Shop Web Page
This project showcases my expertise in building web applications using **Java, Spring Boot, PostgreSQL and React**. The frontend allows users to browse and edit sushi items, while the backend manages item, category, and subcategory data.

## Table of Contents
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Screenshots](#screenshots)
- [Project Status](#project-status)
- [Setup](#setup)
- [Running the Project](#running-the-project)
- [License](#license)

---

## Technologies Used
This project utilizes the following technologies:
- **[Java](https://www.java.com/)** – Backend logic
- **[Spring Boot](https://spring.io/projects/spring-boot)** – REST API and backend services
- **[Hibernate](https://hibernate.org/)** – ORM for database management
- **[PostgreSQL](https://www.postgresql.org/)** – Database
- **[React](https://react.dev/)** – Frontend framework
- **[Material UI](https://mui.com/material-ui/)** – React component library
- **[Docker](https://www.docker.com/)** – Containerization
- **[JUnit](https://junit.org/)** – Unit testing framework
- **[Mockito](https://site.mockito.org/)** – Mocking framework for testing dependencies


---

## Features
- Retrieve items by category  
- Retrieve items by subcategory
- Sign in and sign out
- Edit specific items (admin feature)
- List of all items (admin feature)

---

## Screenshots
### **Database Diagram:**
![SushiShop_database](images/Database_diagram.png)

---

## Project Status
**In Progress** – More features will be added soon.

---

## Setup
To set up the project, follow these steps:

1. Install the necessary dependencies:
    - [Docker](https://docs.docker.com/engine/install/)
    - [Git](https://git-scm.com/downloads)

2. Clone the repository:
   ```bash
   git clone https://github.com/lukaskierzek/online-sushi-shop.git
   ```
   
3. Change directory to online-sushi-shop:
   ```bash
   cd online-sushi-shop
   ```

4. Build and run the application using Docker:
   ```bash
   docker compose up --build
   ```

---

## Running the Project
- **Backend API (Swagger UI):**  
  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)<br/>
  **Username**: admin <br/>
  **Password**: admin123


- **Frontend Webpage:**  
  [http://localhost:5137/sushishop/item?category=NEW-ITEM](http://localhost:4200/sushishop/item?category=NEW-ITEM)<br/>
  **Username**: admin <br/>
  **Password**: admin123


- **PgAdmin (Database UI):**  
  [http://localhost:5050/](http://localhost:5050/)

---

## License
This project is licensed under the **MIT License**.  
For full details, see [LICENSE](LICENSE).

