each entity is a table in jpa,
each instance is a row in jpa and we have repository that
has all instances/objects of that entity

jpa entities are objects that represent persistent data in rdbms
jpa relies on java bean

default constructor is needed



lazy vs eager fetch, lazy fetch of address and only user fetch
eager address and user all fetched

on startup we dont store entities, on findAll()
only it queries db and fetches all users and puts in memory and returns data
and removes the users




--------------------------------------


#in update facing version issues optimistic locking, as will not delete old data hibernate on restart
#thus use create, deletes and creates new data everytime

#spring.jpa.show-sql = true
#
#means:
#
#Whenever Hibernate executes SQL, Spring will print out that SQL in logs (console)
#
#good for debugging
#
#example printed will look like:
#
#select user0_.id as id1_0_ from user user0_ where ...
#
#spring.jpa.hibernate.ddl-auto = update
#
#this controls how Hibernate handles your DB schema.
#
#update means:
#
#when app starts ? Hibernate will compare your Entities (Java @Entity classes) vs DB schema
#
#and automatically ALTER TABLE / CREATE TABLE to match your entities
#
#Does NOT delete/clean data
#
#This is convenient during development.


---------------------------------------


Q)repository jpa pattern update, it pulls rows and creates entites from it and stores in memory. what if there all million rows? wont the memry get filled and program crash?

good question. NO — not always.

it depends on HOW you query.

1) normal .save() or .findById() → returns ONE row

that’s fine. no memory problem.

2) .findAll() → THIS is the dangerous one

If you do:

List<User> allUsers = userRepository.findAll();


and your user table has 1 million rows → yes, this will try to load 1 million objects into memory → bad → can cause OOM.

So don’t do findAll() blindly in production.

What is the correct way?

Use pagination:

PageRequest pageRequest = PageRequest.of(0, 50);

Page<User> page = userRepository.findAll(pageRequest);


You get only 50 rows.


Spring Data JPA does NOT keep ALL objects you fetched in memory forever.
After the repository returns, and transaction is closed, persistence context flushes.

But still during query execution, if you ask for 1M rows, RAM will explode.

Rule:
large datasets → always use pagination or streaming

Example streaming:
@QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "1000"))
Stream<User> streamAll();


Then you iterate stream, memory stays small.

----------------------------------


controller always uses UserRequest and UserResponse, never User directly
thus no model in controller

inject
1)make final and constructor to get the object
2) or @Autowired spring
3) lombok @RequiredArgsConstructor


-----------------------------------

return users.stream()
.filter(...)
.findFirst()
.map(u -> { u.setName("x"); return true; })
.orElse(false);
flow:
case 1: element found in stream
findFirst() returns Optional<User> with a value

then .map(...) is executed → mapping returns Optional<Boolean> with value true

then .orElse(false) → since Optional already has value → returns that value (true)

result = true
orElse(false) is not executed, it just checks "do I have a value? yes → return it."

case 2: no element found in stream (empty)
findFirst() returns Optional.empty

map is not executed (map is skipped because Optional is empty)

then .orElse(false) → Optional has no value → so it returns false

result = false

You can rewrite mentally like:
java
Copy code
Optional<Boolean> opt = users.findFirst().map(...);
if(opt.hasValue) {
return opt.value;
} else {
return false;
}



--------------------------------------

- can add @Transactional to whole service class or to a method inside it


ChatGPT said:

@Transactional is a Spring annotation that says:

run this method inside a database transaction

what does that mean?

A DB transaction is like doing multiple DB operations as one unit.

Example without transaction:

update A
update B
update C
--> if error in C → A and B are already committed. data becomes inconsistent.


With transaction:

BEGIN
update A
update B
update C
COMMIT


if C fails:

ROLLBACK → revert everything


so DB stays consistent.

what @Transactional does in Spring:

opens a transaction at method start

all DB calls inside run under same transaction

if method ends normally → commit

if exception occurs → rollback

Where to use it?

put on service layer:

@Service
public class UserService {
@Transactional
public void updateUserAddress(...) {
userRepo.save(...)
addressRepo.save(...)
// both are atomic now
}
}

-------------------------------


BigDecimal totalPrice = cartItems.stream()
.map(CartItem::getPrice)
.reduce(BigDecimal.ZERO, BigDecimal::add);


--------------------------------

is springboot actuator used in production?

Yes.
Spring Boot Actuator is absolutely used in production.
In fact → that is the real purpose.

Why is it used in production?
Actuator gives endpoints for:
thingexampleshealth/actuator/healthmetrics/actuator/metricsenvironment info/actuator/envlogs & levels/actuator/loggersthreads/actuator/threaddump
These are critical for:


monitoring


alerts


health checks by load balancers and Kubernetes


troubleshooting



BUT — in production you restrict access to actuator endpoints.
Only allow necessary endpoints → normally health and metrics.
Example in prod application.properties:
management.endpoints.web.exposure.include=health,info,metrics

And you protect with security (auth or allow only internal network).

Summary:
questionanswerIs Actuator used in prod?✅ YesShould all endpoints be open?❌ NoDo we restrict & secure them?✅ Yes
Actuator = Production-grade monitoring component.

------------------------------------


can add custom endpoints,
enable/disable oob endpoints in prod

also like health endpoint, can configure which oob metircs like db, redis connection health to show

actuator knows which db we are using from pom.xml and application.properties files and checks those


info endpoint like version, git config etc. static or dynamic info


logging endpoint for monitoring and managing application logs dynamically, without restarting(if env file)
to update, send post request to loggers/{packages} endpoint with newlevel

enable debug logs in production and switch it back

expose shutdown endpoint with security like springsecurity

expose to IP internal to org, using api gateway/spring security



------------------------------------

docker hub/ amazon ecr for container images

with springboot we dont need dockerfile
we have springboot maven plugin
docker automatically goes through maven plugin and pom.xml
and understands the dependency

springboot uses cloud native built (cnb)packs to build docker images
without the need for creating a docker file

1)cnb - specification, they go though source code and analyzie libraries
and dependencies needed in container, like jdk version etc

2)maven/springboot uses cnb internally
- packages app to jar file, then hands it off to buildpacks
  3)cnb uses layering to create separate different docker image
- eg one layer jvm, another layer application dependencies, 3rd
- layer had application itself
  (used to cache layers for subsequent builds, only create top layers again as needed)


4)paketo buildpacks, implementations of cnb specification
provides tools like jvm to compile springboot app into docker image

5)we get the actual docker image

>./mvnw spring-boot:build-image "-Dspring-boot.build-image.imageName=<dockerhub-username>/<imagename>"

//docker needs to be up and running


------------

| command                                            | purpose                                             |
| -------------------------------------------------- | --------------------------------------------------- |
| `docker pull <image>`                              | pull image from Docker Hub to your machine          |
| `docker push <username>/<image>`                   | push image from your local to Docker Hub            |
| `docker run -it <image>`                           | run container in interactive mode (you get a shell) |
| `docker run -d <image>`                            | run container in detached/background mode           |
| `docker run -p <hostPort>:<containerPort> <image>` | map container port to host port                     |
| `docker run --name <name> <image>`                 | run container with a custom container name          |
| `docker stop <container>`                          | stop a running container                            |
| `docker start <container>`                         | start a stopped container                           |
| `docker rm <container>`                            | remove a stopped container                          |
| `docker rmi <image>`                               | remove image from local storage                     |
| `docker ps`                                        | list running containers                             |
| `docker ps -a`                                     | list all containers (running + stopped)             |
| `docker images`                                    | list all images on the system                       |
| `docker exec -it <container> bash`                 | go inside a running container shell                 |
| `docker build -t <username>/<image> .`             | build an image from Dockerfile in current directory |
| `docker logs <container>`                          | view logs of a container                            |
| `docker inspect <container>`                       | view detailed info (json) about a container         |

small extra tips

if terminal becomes stuck inside container → press Ctrl + P, Ctrl + Q to detach without stop
docker ps -q → gives only container IDs (useful in scripts)
docker stop $(docker ps -q) → stop ALL running containers


--------

run postgres container with docker and map it to 5432 port

>docker run -d --name db -e POSTGRES_PASSWORD=mysecretpassword postgres:14

>docker run -d --name pgadmin -e PGADMIN_DEFAULT_EMAIL=user@domain.com -e PGADMIN_DEFAULT_PASSWORD=SuperSecret dpage/pgadmin4

//pgadmin is web based postgresql management tool
//pgadmin container connects to postgresql container

//create a docker network between both
//or use docker compose depends on

> docker exec -it pgadmin ping db
> docker network create my-network
>docker rm -f db pgadmin

//run container again using/within that network

>docker run -d --name db --network my-network -e POSTGRES_PASSWORD=mysecretpassword postgres:14
>docker run -d --name pgadmin --network my-network -e PGADMIN_DEFAULT_EMAIL=user@domain.com -e PGADMIN_DEFAULT_PASSWORD=SuperSecret dpage/pgadmin4

//if containers in same docker network, can communicate with each other using same container names as the hostname


---------------

>docker network create postgres

// -v pgadmin:/var/lib/pgadmin  //volume: path inside container (not host machine)

//postgres service
> docker run -d \
--name postgres_container \
-e POSTGRES_USER=abhinav \
-e POSTGRES_PASSWORD=abhinav \
-e PGDATA=/data/postgres \
-v postgres:/data/postgres \
-p 5432:5432 \
-network postgres \
--restart unless-stopped \
postgres:14

pgadmin service
> docker run -d \
--name pgadmin_container \
-e PGADMIN_DEFAUT_EMAIL=pgadmin4@pgadmin.org \
-e PGADMIN_DEFAULT_PASSWORD=admin \
-e PGADMIN_CONFIG_SERVER_MODE=False \
-v pgadmin:/var/lib/pgadmin \
-p 5050:80 \
-network postgres \
--restart unless-stopped \
dpage/pgadmin4


//dont run these, use docker compose
//single yaml file for app, services, networks, volumes

//plugin runs
>docker compose up -d //or >docker-compose up pgadmin //for any one service

>docker compose down

//stop local running postgres instance

----------------

1)now spring app is running locally(had created its container earlier)
and connected to postgres container with port oh host
2)and pgadmin container connected to postgres container using docker network hostname=containername >ping postgres


//go to pgadmim>dbname>schema>table>tablename>view/edit data


-------------------

1)microservice, each port for each service,
2)each service different/same db instance

3)codebase different for each service(different docker file/docker-compose service)
or can be same(how?)

//product: 8081
//user: 8082
//order: 8083


--------------------

in monolith, can make foreign key and make many to many and one to one contraint

in microservice, we cant have contraint on foreign keys much as separate db and tables, thus just store ids
can store id, but actually not db related, as internally id only

but main thing is we cant store model directly as dont have model, thus store id

IMP-
and fetch from id and do validation using inter-service communication

cant have join queries, need to do inter service communication and using application logic need to join


----------------------


installs 3 things, 
mongod server
mongos sharded cluster query router
mongodb shell client, mongosh

run as macos service-
> brew services start mongodb-community@6.0
>                stop
>launchctl list | grep mongodb
OR
> brew services list
run manually as a background process-
>mongod --config /opt/local/etc/mongod.conf --fork
> to stop, connect to mongod using mongosh, and run shutdown command


-----------------------


running as macos service vs background process

Great question — these two sound similar but are quite different in how macOS (and Unix in general) manages them.
Let’s break it down clearly:

🧠 1. Background Process
Definition:

A background process is simply a program you start manually (from Terminal or a script) and run “in the background”.
Example:
node app.js &
or
nohup java -jar myapp.jar > out.log 2>&1 &

💡 It’s just your app running in the background of your current shell.
Behavior:
It starts when you start it, not automatically on boot.

It stops when:
You reboot, or
The terminal session (or user) that launched it is killed (unless nohup or daemonized).
You manage it manually with commands like:

ps aux | grep app
kill <pid>

No automatic restart, no logging integration, no lifecycle management.
✅ Good for: testing, temporary scripts, or developer usage.
❌ Bad for: production reliability.

⚙️ 2. macOS Service (Launch Agent / Daemon)
Definition:
A service on macOS is managed by launchd, the system’s service manager.
These are defined in .plist (property list) files in folders like:
/Library/LaunchDaemons/ → system-wide (runs at boot)
~/Library/LaunchAgents/ → per-user

Example:
/Library/LaunchDaemons/com.example.myapp.plist

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" 
"http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>com.example.myapp</string>
    <key>ProgramArguments</key>
    <array>
        <string>/usr/local/bin/java</string>
        <string>-jar</string>
        <string>/path/to/myapp.jar</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
    <key>KeepAlive</key>
    <true/>
    <key>StandardOutPath</key>
    <string>/var/log/myapp.log</string>
    <key>StandardErrorPath</key>
    <string>/var/log/myapp-error.log</string>
</dict>
</plist>


Then:
sudo launchctl load /Library/LaunchDaemons/com.example.myapp.plist
sudo launchctl start com.example.myapp

Behavior:
Starts automatically at boot/login.
Automatically restarts if it crashes (KeepAlive).
Fully managed by macOS.
Has proper logging and control via launchctl.
Runs independent of any terminal session.
✅ Good for: servers, long-running daemons, background APIs.



-------------------------------------


run mongodb client using 
>mongosh

/*
mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.5.9
Using MongoDB:          6.0.26
Using Mongosh:          2.5.9
*/

unlike mongosh/mongos, use compass for gui 


with mongodb, we dont need jpa or postgresql
sql to nosql migration, no joins/entity, just document

no foreign key/obj, directly stored object inside json within json, cant do joins

in mongodb, default id type is string and doesnt support autogeneration of ids which are not string

now we dont need separate table for address as stored inside user only

usr - bhattacharjeeabhinav_db_user
pass - NRCldOxdxeXpZQ3n
connection string - mongodb+srv://bhattacharjeeabhinav_db_user:<db_password>@user-data.ovjmefc.mongodb.net/?appName=user-data

      uri: mongodb://localhost:27017/userdb

----------------------

postgres on cloud
>npx neonctl@latest init


psql 'postgresql://neondb_owner:npg_k7XZvNOwB6gq@ep-wispy-heart-adyd86ek-pooler.c-2.us-east-1.aws.neon.tech/product?sslmode=require&channel_binding=require'

-----------------------

mysql 
pass - abhinav123

url: jdbc:mysql://localhost:3306/productdb
mysql ommunity edition local server 
mysql gui workbench , oracle bought
postgresql open source
--------


we will use docker only for postgresql and local server for mongodb 
postgresql container connected to local port





