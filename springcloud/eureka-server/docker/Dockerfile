FROM openjdk:8
ENV workdir="/root/tyler/eureka-server"
COPY . ${workdir}
WORKDIR ${workdir}
EXPOSE 8761
CMD ["java","-jar","demo-0.0.1-SNAPSHOT.jar"]
