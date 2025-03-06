# Suhi Shop Web Page
Web demonstrates my skill with the [technologies I use](#technologies-used).On this webpage, on the frontend side, you can browse items by categories and edit particular items. On the backend side, you can edit and post items, categories, and subcategories.

## Table of contents
* [Technologies Used](#technologies-used)
* [Features](#features)
* [Screenshots](#screenshots)
* [Project Status](#project-status)
* [Setup](#setup)
* [Running](#running)
* [Licene](#licence)

## Technologies Used
- Java
- Spring Boot
- Hibernate
- PostgreSQL
- Angular
- Docker

## Features
- Get items by category
- Get item by subcategory
- Editing a specified item

## Screenshots
**Database diagram:**
>![SushiShop_database](images/Database_diagram.png)

## Project Status
The project is in progress.

## Setup
- You have to install [docker](https://docs.docker.com/engine/install/) and/or [git](https://git-scm.com/downloads).
- Download the project, for example, using the git command:
```git
git clone https://github.com/lukaskierzek/online-sushi-shop.git
```
- Open the command line in the directory where the **docker-compose.yml** file is located and run the command:
```docker
docker compose up -build
```

## Running
- Link to the backend page:
>http://localhost:8080/swagger-ui/index.html

- Link to the website page:
>http://localhost:4200/sushishop/item?category=NEW-ITEM

- Link to the PgAdmin:
>http://localhost:5050/


## Licence
> [Full content of the licence](LICENSE).

MIT
