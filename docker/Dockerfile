ARG JVM_VERSION=17
ARG SERVICE_NAME=server

FROM eclipse-temurin:${JVM_VERSION}-jdk-focal AS builder
ARG SERVICE_NAME

RUN set -o errexit -o nounset \
 && apt-get update \
 && apt-get install git -y

COPY . /src
WORKDIR /src

RUN set -o errexit -o nounset \
 && chmod +x ./gradlew \
 && ./gradlew clean :${SERVICE_NAME}:shadowJar :${SERVICE_NAME}:assemble --stacktrace --no-daemon

FROM eclipse-temurin:${JVM_VERSION}-jdk-focal AS jre
ARG JVM_VERSION
ARG SERVICE_NAME

WORKDIR /src

COPY --from=builder /src/${SERVICE_NAME}/build/libs/${SERVICE_NAME}-*-all.jar ${SERVICE_NAME}.jar

RUN set -o errexit -o nounset \
 && jdeps \
     --ignore-missing-deps \
     -q \
     --multi-release ${JVM_VERSION} \
     --print-module-deps \
     ./${SERVICE_NAME}.jar > jre-deps.info \
 && jlink \
     --add-modules $(cat jre-deps.info) \
     --strip-debug \
     --no-man-pages \
     --no-header-files \
     --compress 2 \
     --output ./jre

FROM eclipse-temurin:${JVM_VERSION}-jdk-focal AS extractor
ARG SERVICE_NAME

WORKDIR /src

COPY --from=builder /src/${SERVICE_NAME}/build/libs/${SERVICE_NAME}-*.jar ${SERVICE_NAME}.jar
RUN java -Djarmode=layertools -jar ${SERVICE_NAME}.jar extract

FROM ubuntu:focal
ARG SERVICE_NAME

ENV JAVA_HOME=/opt/jre
ENV PATH="/opt/jre/bin:${PATH}"

ENV HTTP_PORT=8080
ENV MANAGEMENT_PORT=8081

ARG CRYPTIC_USER=cryptic-${SERVICE_NAME}
ARG CRYPTIC_GROUP=cryptic-${SERVICE_NAME}
ARG DATA_DIR=/data

RUN set -o errexit -o nounset \
 && groupadd --system --gid 1000 ${CRYPTIC_GROUP} \
 && useradd --system --gid ${CRYPTIC_GROUP} --uid 1000 --shell /bin/bash --create-home ${CRYPTIC_USER} \
 && mkdir -p ${DATA_DIR} \
 && chown --recursive ${CRYPTIC_USER}:${CRYPTIC_GROUP} ${DATA_DIR} \
 && chown --recursive ${CRYPTIC_USER}:${CRYPTIC_GROUP} /home/${CRYPTIC_USER} \
 \
 && apt-get update \
 && apt-get install -y --no-install-recommends curl jq ca-certificates \
 && rm -rf /var/lib/apt/lists/* \
 \
 && curl -fsSL https://raw.githubusercontent.com/fabric8io-images/run-java-sh/master/fish-pepper/run-java-sh/fp-files/run-java.sh -so /opt/run-java.sh \
 && chmod +x /opt/run-java.sh

COPY --from=jre /src/jre ${JAVA_HOME}

WORKDIR ${DATA_DIR}

ENV JAVA_APP_DIR=${DATA_DIR}
ENV JAVA_MAIN_CLASS=org.springframework.boot.loader.JarLauncher
ENV JAVA_APP_NAME=cryptic-${SERVICE_NAME}
ENV JAVA_CLASSPATH=/opt/cryptic-${SERVICE_NAME}

USER ${CRYPTIC_USER}:${CRYPTIC_GROUP}
VOLUME ${DATA_DIR}

EXPOSE ${HTTP_PORT}
EXPOSE ${MANAGEMENT_PORT}

COPY --from=extractor /src/dependencies/ /opt/cryptic-${SERVICE_NAME}
COPY --from=extractor /src/spring-boot-loader/ /opt/cryptic-${SERVICE_NAME}
COPY --from=extractor /src/snapshot-dependencies/ /opt/cryptic-${SERVICE_NAME}
COPY --from=extractor /src/application/ /opt/cryptic-${SERVICE_NAME}

ENTRYPOINT ["/opt/run-java.sh"]

#HEALTHCHECK --start-period=10s --interval=30s --timeout=3s --retries=3 \
#            CMD curl --silent --fail --request GET "http://localhost:${MANAGEMENT_PORT}/actuator/health" \
#                | jq --exit-status '.status == "UP"' || exit 1
