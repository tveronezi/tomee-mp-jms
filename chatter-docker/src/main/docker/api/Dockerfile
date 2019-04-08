ARG VERSION
ARG STABLE_VERSION_TOMEE

FROM veronezi/sample-tomee:${STABLE_VERSION_TOMEE}

COPY --chown=tomee:tomee ROOT.war /opt/tomee/webapps/
COPY --chown=tomee:tomee logging.properties /opt/tomee/conf/

WORKDIR /opt/tomee/

ENV CATALINA_OPTS -Xms128m -Xmx512m
ENV SPIDER_AMQ_PATH tcp://amq:61616

COPY --chown=tomee:tomee start.sh .
RUN chmod u+x start.sh