FROM ubuntu:22.04

RUN apt-get update && apt-get install -y libopenblas-dev openjdk-8-jdk-headless clang
RUN apt-get update && apt-get install -y curl && curl https://raw.githubusercontent.com/sbt/sbt/4e7fefa70cda0cac2683991c33abbc9da9c62bf9/sbt > /sbt && chmod u+x /sbt