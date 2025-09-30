FROM amazoncorretto:22-alpine

RUN mkdir -p /opt/mock-oidc/
WORKDIR /opt/mock-oidc/

COPY build/libs/mock-oidc-1.0.0.jar app.jar

RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001

EXPOSE 8081

CMD java -jar app.jar
