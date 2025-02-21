#OpenJDK 17 기반 이미지 사용
FROM openjdk:17-jdk-slim

#작업 디렉토리 설정
WORKDIR /app

#JAR 파일 및 설정 파일을 컨테이너로 복사
COPY golmokstar-0.0.1-SNAPSHOT.jar /app/app.jar
COPY application.yml /app/config/application.yml
COPY .env /app/

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "-Dspring.config.location=/app/config/application.yml", "/app/app.jar", "--debug"]