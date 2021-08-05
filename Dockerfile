FROM openjdk:14
EXPOSE 50051

ARG JAR_FILE=build/libs/*-all.jar
ADD ${JAR_FILE} app.jar

CMD java -jar /app.jar
