FROM amazoncorretto:17

ENV LC_ALL=C.UTF-8

# OpenSSL 설치
RUN yum update -y && yum install -y openssl

# GitHub 루트 인증서 다운로드 및 cacerts에 추가
RUN echo | openssl s_client -connect github.com:443 -servername github.com -showcerts 2>/dev/null | \
    sed -n '/-----BEGIN CERTIFICATE-----/,/-----END CERTIFICATE-----/p' | \
    sed '0,/-----END CERTIFICATE-----/d' | \
    tac | \
    sed '0,/-----BEGIN CERTIFICATE-----/d' | \
    tac > /tmp/github_root_ca.pem && \
    keytool -importcert -alias github_root_ca -file /tmp/github_root_ca.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt && \
    rm /tmp/github_root_ca.pem

# 애플리케이션 JAR 파일 복사
COPY build/libs/*.jar /java/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/java/app.jar"]