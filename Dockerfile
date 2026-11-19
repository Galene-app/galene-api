FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src/main src/main
RUN mvn -B -q package -Dmaven.test.skip=true


FROM eclipse-temurin:25-jre
WORKDIR /app

ENV TZ=America/Sao_Paulo \
    SPRING_PROFILES_ACTIVE=prod \
    HAS_MFA=true \
    MAILEROO_FROM_NAME=Galene \
    JAVA_OPTS="-XX:MaxRAMPercentage=75"

RUN groupadd --system galene && useradd --system --gid galene --no-create-home galene
COPY --from=build /app/target/*.jar /app/app.jar
USER galene

EXPOSE 7057
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
