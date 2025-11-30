
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



