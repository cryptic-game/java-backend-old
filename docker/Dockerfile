FROM adoptopenjdk/openjdk11:alpine-jre

USER cryptic:cryptic

WORKDIR /cryptic-backend

COPY --chown=cryptic:cryptic /docker/docker-entrypoint.sh ./docker-entrypoint.sh
COPY --chown=cryptic:cryptic /server/build/install/server ./server/
COPY --chown=cryptic:cryptic /java-daemon/build/install/java-daemon ./java-daemon/
#COPY Python files

ENTRYPOINT ["./docker-entrypoint.sh"]
