# 添加 Java 8 镜像来源
FROM openjdk:8-jdk-alpine
#维护者信息
MAINTAINER jkguo
# 添加 Spring Boot 包
ADD target/*.jar app.jar
# 镜像启动成为容器后，对外暴露的端口
EXPOSE 8080
# Image创建容器时的初始化内存，最大内存，及启动时使用的profile. -c为清除以前启动的数据
ENTRYPOINT ["java","-Xms1024m","-Xmx1024m","-jar","/app.jar","-c"]
#设置时区
RUN echo 'Asia/Shanghai' >/etc/timezone \
