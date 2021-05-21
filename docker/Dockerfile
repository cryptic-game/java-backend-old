ARG JAVA_VERSION=16
ARG JVM_IMPL=hotspot
ARG SERVICE_NAME=server

FROM adoptopenjdk:${JAVA_VERSION}-jdk-${JVM_IMPL} AS builder

RUN set -o errexit -o nounset \
    && apt-get update \
    && apt-get install git -y

COPY . /src
WORKDIR /src

RUN chmod +x ./gradlew \
    && ./gradlew :admin-panel:bootJar --stacktrace --no-daemon

FROM adoptopenjdk:${JAVA_VERSION}-jre-${JVM_IMPL} AS extractor

WORKDIR /src

COPY --from=builder /src/${SERVICE_NAME}/build/libs/${SERVICE_NAME}*.jar cryptic.jar
RUN java -Djarmode=layertools -jar cryptic.jar extract

FROM adoptopenjdk:${JAVA_VERSION}-jre-${JVM_IMPL}

ARG CRYPTIC_USER=cryptic
ARG CRYPTIC_GROUP=cryptic
ARG DATA_DIR=/data

RUN set -o errexit -o nounset \
    && groupadd --system --gid 1000 ${CRYPTIC_GROUP} \
    && useradd --system --gid ${CRYPTIC_GROUP} --uid 1000 --shell /bin/bash --create-home ${CRYPTIC_USER} \
    && mkdir -p ${DATA_DIR} \
    && chown --recursive ${CRYPTIC_USER}:${CRYPTIC_GROUP} ${DATA_DIR} \
    && chown --recursive ${CRYPTIC_USER}:${CRYPTIC_GROUP} /home/${CRYPTIC_USER}

WORKDIR ${DATA_DIR}

USER ${CRYPTIC_USER}:${CRYPTIC_GROUP}
EXPOSE 8080
VOLUME ${DATA_DIR}

COPY --from=extractor /src/dependencies/ /opt/cryptic
COPY --from=extractor /src/spring-boot-loader/ /opt/cryptic
COPY --from=extractor /src/snapshot-dependencies/ /opt/cryptic
COPY --from=extractor /src/application/ /opt/cryptic

ENTRYPOINT ["java", "-cp", "/opt/cryptic", "org.springframework.boot.loader.JarLauncher"]
