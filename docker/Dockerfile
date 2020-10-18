FROM adoptopenjdk:11-jdk-hotspot AS builder

ARG GRADLE_USER=gradle
ARG GRADLE_GROUP=gradle
ARG SERVICE_NAME=server

RUN set -o errexit -o nounset \
    && groupadd --system --gid 1000 ${GRADLE_GROUP} \
    && useradd --system --gid ${GRADLE_GROUP} --uid 1000 --shell /bin/bash --create-home ${GRADLE_USER} \
    && chown --recursive ${GRADLE_USER}:${GRADLE_GROUP} /home/${GRADLE_USER} \
    && apt-get update \
    && apt-get install git -y

COPY --chown=gradle:gradle . /home/${GRADLE_USER}/src
WORKDIR /home/${GRADLE_USER}/src

USER ${GRADLE_USER}
RUN chmod +x ./gradlew && ./gradlew :${SERVICE_NAME}:installDist --stacktrace --no-daemon

FROM adoptopenjdk:11-jre-hotspot

ARG GRADLE_USER=gradle
ARG CRYPTIC_USER=cryptic
ARG CRYPTIC_GROUP=cryptic
ARG SERVICE_NAME=server

ENV CRYPTIC_HOME /opt/cryptic-backend
ENV DATA_DIR /data

RUN set -o errexit -o nounset \
    && groupadd --system --gid 1000 ${CRYPTIC_GROUP} \
    && useradd --system --gid ${CRYPTIC_GROUP} --uid 1000 --shell /bin/bash --create-home ${CRYPTIC_USER} \
    && mkdir -p ${DATA_DIR} \
    && chown --recursive ${CRYPTIC_USER}:${CRYPTIC_GROUP} ${DATA_DIR} \
    && chown --recursive ${CRYPTIC_USER}:${CRYPTIC_GROUP} /home/${CRYPTIC_USER}

WORKDIR ${DATA_DIR}
COPY --from=builder --chown=${CRYPTIC_USER}:${CRYPTIC_GROUP} /home/${GRADLE_USER}/src/${SERVICE_NAME}/build/install/${SERVICE_NAME}/ ${CRYPTIC_HOME}/

RUN ln --symbolic ${CRYPTIC_HOME}/bin/${SERVICE_NAME} /usr/bin/cryptic \
  && chmod +x /usr/bin/cryptic
USER ${CRYPTIC_USER}
VOLUME ${DATA_DIR}

ENTRYPOINT ["cryptic"]
