# FROM amazoncorretto:17
FROM harbor.xiilab.com:32443/library/amazoncorretto:17

ENV LC_ALL=C.UTF-8

# OpenSSL 설치
RUN yum update -y && yum install -y openssl

# GitHub 인증서 체인 다운로드 및 분리
RUN echo | openssl s_client -connect github.com:443 -servername github.com -showcerts 2>/dev/null | \
    awk '/-----BEGIN CERTIFICATE-----/,/-----END CERTIFICATE-----/' | \
    awk 'split_after==1{n++;split_after=0} /-----END CERTIFICATE-----/{split_after=1}{print > "/tmp/cert" n ".pem"}'

# 각 인증서를 Java cacerts에 추가
RUN for cert in /tmp/cert*.pem; do \
        alias=$(basename $cert .pem); \
        keytool -importcert -alias "github_${alias}" -file $cert -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt; \
    done

# 임시 파일 삭제
RUN rm /tmp/cert*.pem

# 애플리케이션 JAR 파일 복사
COPY build/libs/*.jar /java/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/java/app.jar"]