FROM openjdk:17-ea-17-slim

COPY --from=builder /app/build/libs/*.jar  /app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]