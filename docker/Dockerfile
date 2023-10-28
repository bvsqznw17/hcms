FROM openjdk:8u232-jre

ADD hcms.jar app.jar
EXPOSE 8080
# FROM tomcat:latest

# COPY webapps/chjd.war /usr/local/tomcat/webapps

VOLUME ["/home/hcms/tmpFile","/home/hcms/appStoreFile","/home/hcms/uploadPath","/logs","/usr/local/api/logs","/usr/local/api/conf","/usr/local/api/cert"]
# #设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "/app.jar"]
