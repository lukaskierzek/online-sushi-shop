FROM maven:3.9.9-eclipse-temurin-23 AS build

WORKDIR /online_sushi_shop_app

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:23-jdk

WORKDIR /online_sushi_shop_app

COPY --from=build /online_sushi_shop_app/target/onlineSushiShop-0.0.1-SNAPSHOT.jar online-sushi-shop-app.jar

CMD ["java", "-jar", "online-sushi-shop-app.jar"]
