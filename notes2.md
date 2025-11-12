
config management-

1)parsing and environment
2)relaxed binding (from env create java objects to use)

dev-qa-prod settings change
like different db servers etc.

1)spring boot profile
2)env vars/cli args/ext config files/jvm system vars
3)centralized spring cloud config server in microservices
(configured to pull configs from db/git/files)

4)security, like oauth client secret ids and keys
 like password in config files, need to encrypt decryot with aes etc

use hashicorp vault, aws secrets manager, kubernetes secrets

5)consistency and centralization - 
10 microservice, will have 10 yaml, no consistency
5.1)solution is spring cloud config etc.(centralized config server).
5.2)can also use git

6)dynamic updates and high availability without restarting app
-use spring cloud config server with refresh abilities

7)monitor and version the configs, rollback to prev version
-use git versioning with spring cloud config
-or kubernetes config maps



--------------

spring profiles for different env, 
db conn, log levels, app specific props
separate config for each env and dont need to update when switching
feature flags

basically manually parse the yaml using fasterxml if we want to load custom config


------------

You have this in your main application.yml:

spring:
application:
name: configdemo
profiles:
active: dev

This means:
➡️ At runtime, Spring will load application.yml first,
then merge and override it with properties from:

application-dev.yml

⚙️ If You Have Multiple Profile Files
Say your config files are:
application.yml
application-dev.yml
application-dev2.yml
application-prod.yml

Then you can activate one or more profiles in three ways:

spring:
profiles:
active: dev,dev2

➡️ Spring will load them in order
application.yml (base)
application-dev.yml
application-dev2.yml
and later files override earlier ones.
So if both have server.port, the value from application-dev2.yml wins.

Option 3 — Activate via environment or CLI
Instead of changing the YAML each time, you can run:
mvn spring-boot:run -Dspring-boot.run.profiles=dev2   //from ci pipeline
or
java -jar app.jar --spring.profiles.active=dev2
This is best for switching environments easily (e.g., dev, staging, prod).


---------------------


the -D part means:
“define a JVM property named spring.profiles.active with value dev”.

💡 How it works with Spring Boot
Spring Boot automatically reads system properties and environment variables at startup.
So:
-Dspring.profiles.active=dev
sets the active profile at runtime.


--------------------

@Value,
inject properties from ext sources ike application.properties/yml 
or env or cli args


priority
cli args "--build.id=12345"  > java sys properties "-Dbuild.id=12345
> os env vars "export build_id=12345"
> application.properties
> spring cloud config server
> default val in application code


-------------------

cnfig using env vars set

>mvn clean package   //for build jar file
>export BUILD_ID=54321
> java -jar  target/configdemo-0.0.1-SNAPSHOT.jar 

>java -Dbuild.id=9999 -Dbuild.version=1.2.3 -Dbuild.name=dev-jvm-pro -jar target/configdemo-0.0.1-SNAPSHOT.jar //jvm system properties
>java  -jar target/configdemo-0.0.1-SNAPSHOT.jar --build.id=9999 --build.version=1.2.3 --build.name=dev-jvm-pro //cli or program arguments

can set using intellij also before run (both cli and env vars)- 

 -id: 101   #can also do ${ID}, so that dont hardcode and get from env(even though @Value gets from env itslef)
 -#good for setting client secrets etc.
  
env vars
build.id=7777,build.name=dev

program/cli arguments-
--build.id=7777

-----------------------


--spring.profiles.active=prod

----------


Config Server-
-centralized and versioned configuration
-dynamic updates
-security
-application and profile specific configuration

Spring Cloud Config Server backed by Git, filesystem, db
(if git then uses version controlled systems)





