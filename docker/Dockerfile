FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /cryptic-backend

EXPOSE 80
EXPOSE 8080

COPY /docker/docker-entrypoint.sh ./docker-entrypoint.sh
COPY /server/build/install/server ./server/
COPY /java-daemon/build/install/java-daemon ./java-daemon/
#COPY Python files

RUN apk --update --no-cache add tzdata \
  && cp /usr/share/zoneinfo/UTC /etc/localtime \
  && echo "UTC" >/etc/timezone \
  && apk --no-cache del tzdata

ENTRYPOINT ["./docker-entrypoint.sh"]
