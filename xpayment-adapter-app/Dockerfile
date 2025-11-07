FROM eclipse-temurin:23-jre-alpine

ARG JAR_FILE=xpayment-adapter-app-3.5.5.jar

WORKDIR /app

COPY target/${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]