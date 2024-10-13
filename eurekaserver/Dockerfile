FROM maven:3.8.3-openjdk-17 as build
WORKDIR ./src
COPY . .
RUN mvn install -DskipTests=true

FROM eclipse-temurin:17.0.8.1_1-jre-ubi9-minimal

RUN unlink /etc/localtime;ln -s  /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime
COPY --from=build src/target/eurekaserver*.jar /run/app.jar

EXPOSE 8888

ENV JAVA_OPTIONS="-Xmx2048m -Xms256m"
ENTRYPOINT java -jar $JAVA_OPTIONS /run/app.jar

#FROM eclipse-temurin:21-jre-alpine
#COPY target/eurekaserver*.jar app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]