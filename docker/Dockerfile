FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /cryptic-backend

EXPOSE 80
EXPOSE 8080

COPY /docker/docker-entrypoint.sh ./docker-entrypoint.sh
COPY /server/build/install/server ./server/
COPY /server/www ./www/
COPY /java-daemon/build/install/java-daemon ./java-daemon/
#COPY Python files

ENTRYPOINT ["./docker-entrypoint.sh"]
