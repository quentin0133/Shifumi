FROM rsunix/yourkit-openjdk17

ADD target/ShiFumeUnMi.jar ShiFumeUnMi.jar
ENTRYPOINT ["java", "-jar", "ShiFumeUnMi.jar"]
EXPOSE 8080