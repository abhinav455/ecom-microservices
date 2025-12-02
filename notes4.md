
deploy


----

docker 3 ways
-dockerfile
-buildpacks spring //creates images based on pom.xml
                   //layers and image caching 
-jib
 // a maven/gradle plugin that builds docker images without docker installed,
 // directly from build tool

-docker system prune -a --volumes --force
-docker network prune --force
-docker rm -f $(docker ps -q)

1.dockerfile
get jar
>./mvnw clean package -DskipTests
>create docker file with
FROM eclipse-temurin:23-jdk-ubi9-minimal
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

>docker build -t config-server ./configserver
>docker run --env-file configserver/.env -d --name config-server -p 8889:8888 config-server

after each change, rebuild jar and each step

>chmod +x ./mvnw


-----


Config management in docker

services inside docker communicate using service-names,
just like eureka

IMP-
but we mapped port inside container to ext port, 
   then Others can communicate to it using ports of localhost etc.
localhost will refer to container itself, not others
   thus it ITSELF cant communicate to others using localhost name

thus use service names only, did the same in observability
AS SHARE SAME NETWORK IN DOCKER

earlier all services and eureka we were getting localhost ips 
now need to change to service-names

means, earlier in eureka,  service-name: localhost:port
                     now,  service-name: docker service-name
docker/kubernetes acts as eureka server itself



    cloud:
        config:
            server:
                native:
                    search-locations: classpath:/config
                    #file:///Users/abhinav.bhattacharje/springboot_proj/ecom_microservices/configserver/src/main/resources/config
                    #docker cant use above, move above to volume


      resourceserver:
        jwt:
          issuer-uri: http://keycloak:8080/realms/ecom-app
            #http://localhost:8443/realms/ecom-app #used by spring to autofetch keycloak endpoints like login url, token url
            #in keycloak container, running at 8080 port
            #cant use localhost inside docker, use service-name itslef


in config server, add files as gateway-service-docker.yml
and in the gateway-service application.yml, use profile
    
    spring:
        profiles:   
            active: docker

but dont explicitely state that, pass it from docker-compose
    environment:
      - SPRING_PROFILES_ACTIVE=docker



env vars in docker using environment: tag

1)create docker specific config for each service for docker container using service-name
2)pass env vars from docker-compose file
3)add env to docker-compose using main .env file


-docker exec config-server ls /config


    healthcheck:
      test: ["CMD", "wget", "--spider", "http://localhost:8761/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5

   as without it, docker only checks whether container running or not, not whether app inside container is running or not



postgres-
    volumes:
      - postgres:/data/postgres
      - ./init-multi-db.sql:docker-entrypoint-initdb.d/init.sql

postgres on creation loads this special file and runs it which creation of db service

>docker compose down -v

-chmod +x build-projects.sh 
-./build-projects.sh 



---------------------------

buildpacks

>./mvnw spring-boot:build-image -DskipTests  /
         -Dspring-boot.build-image.imageName=<image-name>
> 
{start cmd and metadata, .jar, app dependencies, java runtime}

can run on any cloud

				<configuration>
					<image>
						<name>ecom/config-server</name>
					</image>
				</configuration>


<name>	The image name and optionally tag (e.g., my-app:1.0).
<builder>	The builder image used to build the app (default is Paketo's base).
<runImage>	Optional. Base image used during runtime.
<env>	Set environment variables used during build (e.g., JVM version, memory limits).
<publish>	If true, pushes the built image to a remote Docker registry.
<docker>	Credentials used if you're publishing the image to DockerHub or another registry.


#image built with docker-compose
config-server:
    build: ../../configserver       
    #image: ecom/config-server #image built with builtpacks
    container_name: config-server


-----

gateway security error  - iss claim is not valid in token 
as iss = http://localhost:8443/realms/ecom-app
but it is inside docker, thus to verify the token, we need to update code 
inside gateway

within gateway, we have configured
resourceserver:
    jwt:
        issuer-uri: http://keycloak:8080/realms/ecom-app


keycloak issue - 
1)Update Keycloak realm settings → Realm Settings → Endpoints → “Frontend URL”
http://keycloak:8080
//2)or do same for all realms via docker-compose env-
//KC_HTTP_ENABLED: "true"
//# INTERNAL Hostname (used by other containers)
//KC_HOSTNAME: "localhost"
//KC_HOSTNAME_URL: "http://localhost:8443"
//# ALLOW mismatch for internal docker URL "keycloak"
//KC_HOSTNAME_STRICT: "false"
//KC_HOSTNAME_STRICT_HTTPS: "false"



------------------------------------------------------


Observability with docker containers

backend network for backend related tasks
loki network for loki/minio(storage)/ related tasks

And then I went into the container and used kcadm.sh like this:
# cd /opt/keycloak/bin
# ./kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin
# ./kcadm.sh update realms/master -s sslRequired=NONE
#./kcadm.sh update realms/ecom-app -s sslRequired=NONE

ps aux | grep otel

zipkin:
image: openzipkin/zipkin
container_name: zipkin
ports:
- 9411:9411
networks:
- loki
- backend


add zipkin to both networks

------------------------------------------------------

jib, w/o docker, on ci/cd servers can use to build image

add <plugin> to pom.xm with dockerhub/gcr url,credentials, name etc.

>./mvnw compile jib:build //push to hub
>./mvnw clean compile jib:build 
            //removes /target/*.jar, and compiles java code, then builds image
>./mvnw compile jib:dockerBuild //use cred in pom file, or just do docker login in cli, it takes stored cred
>./mvnw jib:buildTar //et a .tar file offline use and share, use with >docker load

google built it




-----------------------------------------------------------

Restricting Direct Access to service containers without Gateway-

containers now need vpc, inbound firewall rules to only grant access 
to ip of gateway
look at sso.png

in docker, easy,
    -just dont expose docker port outside
    remove    ports:
                - 8082:8082


IMP-
thus now, from external only accessible by gateway-service, and gateway-service
talk to other services inside docker environment different containers
docker is like a cloud env only


-----------------------------------------------------------


Microservice Packaging


now we have microservices in 
1)different server(localhost server:port in app.yml)
2)docker container -docker compose  services: 
3)have profiles different config with env. vars and gateway-service-prod/docker.yml

but how to move them b/w dev,staging,prod in another good way?

without packaging springboot application executes as follows-
-code compilation(.class file bytecode)
-run main class
-without jar, we need to set classpath correctly to use dependencies
(in jar, everything is packaged)
-embedded server
-source code changes(just run again, dont need to rebuild jar)
-can use development mode without jar, rapid integration and modification

packaging - 
compile into bytecode with dependent libraries in a single executable artifact-
easily run(jar-java archive)
jre executes jar

.war , ear, docker image are different packaging options

pom - project object model


--
download maven and get it installed from apache maven website
Or use maven wrapper ./mvnw , that we automatically get with springboot apps
    .mnv/wrapper/maven-wrapper.properties file

>mvn clean //remove all prev built files
           //.class and .jar files
>mvn package  //compiles and packages
> mvn install //install dependencies based on pom

>jar -tf target/configserver-0.0.1-SNAPSHOT.jar 
     //show all .jar and .class and other file inside it
>java -jar  target/configserver-0.0.1-SNAPSHOT.jar 


![job.png](job.png)


now we have single postgres instance, varius db inside it
but can have many service: posgres-product and postgres-order in docker compose
 config.yml -  url: jdbc:postgresql://postgres-product:5432/productdb


docker is like a cloud env only
./mvnw spring-boot:build-image "-Dspring-boot.build-image.imageName=<your-dockerhub-username>/<your-image-name>"
 
<build>
<plugins>
spring-boot-maven-plugin

this plugin helps us to use - spring-boot:build-image
--





