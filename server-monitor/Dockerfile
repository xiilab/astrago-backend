FROM amazoncorretto:17

ENV LC_ALL=C.UTF-8

# OpenSSL 설치
RUN yum update -y && yum install -y openssl curl

# GitHub 인증서 다운로드 및 cacerts에 추가
RUN echo | openssl s_client -connect github.com:443 -servername github.com 2>/dev/null | \
    openssl x509 > /tmp/github.crt && \
    keytool -importcert -alias github -file /tmp/github.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt && \
    rm /tmp/github.crt

COPY build/libs/*.jar /java/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/java/app.jar"]