FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY eclipse-java-formatter.xml .

# mise en cache des dépendances
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp dependency:go-offline

# sources & build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp package -DskipTests 

# image finale
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
