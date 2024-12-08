FROM gradle:jdk21-alpine AS build

WORKDIR /app

COPY ./gradlew /app/gradlew
COPY ./gradle /app/gradle
COPY ./settings.gradle.kts /app/settings.gradle.kts
COPY ./build.gradle.kts /app/build.gradle.kts
COPY ./src /app/src

RUN ./gradlew build
RUN ./gradlew javadoc

FROM eclipse-temurin:21-jre-alpine AS run

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/my-app.jar
COPY --from=build /app/build/jacoco /app/jacoco
COPY --from=build /app/build/reports/tests/test /app/test
COPY --from=build /app/build/docs/javadoc /app/javadoc

ENTRYPOINT ["java", "-jar", "/app/my-app.jar"]