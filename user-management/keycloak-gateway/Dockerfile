FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY target/classes/opex-master-realm.json opex-master-realm.json
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]