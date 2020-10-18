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

COPY --chown=${CRYPTIC_USER}:${CRYPTIC_GROUP} ${SERVICE_NAME}/build/install/${SERVICE_NAME} ${CRYPTIC_HOME}
WORKDIR ${DATA_DIR}

RUN ln --symbolic ${CRYPTIC_HOME}/bin/${SERVICE_NAME} /usr/bin/cryptic \
  && chmod +x /usr/bin/cryptic
USER ${CRYPTIC_USER}
VOLUME ${DATA_DIR}

ENTRYPOINT ["cryptic"]
