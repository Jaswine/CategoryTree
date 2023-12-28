FROM maven:3.8.2-openjdk-17

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean install

EXPOSE 5050

CMD ["mvn", "spring-boot:run"]