# 基于哪个镜像
FROM java:8
# 将本地文件夹挂载到当前容器
VOLUME /tmp
ADD sc-release-final-result-service-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT  ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]