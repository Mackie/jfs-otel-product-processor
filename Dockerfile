FROM amazoncorretto:17-alpine-jdk

RUN apk add --no-cache libstdc++

ENV LANG C.UTF-8

ENV JAVA_HOME=/usr/lib/jvm/default-jvm
ENV PATH=$PATH:/usr/lib/jvm/default-jvm/bin