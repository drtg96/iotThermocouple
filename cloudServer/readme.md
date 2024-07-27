## SUMMARY
Java curl/http serving app running on an AWS EC2 instance

## Required packages
Java JDK 11+
Maven 3.6+
MariaDB

See pom file for more

## BUILD and RUN
`mvn compile`
`mvn exec:java -Dexec.mainClass="com.project.Server"`.  

## Database
DB identifier: thermodb
Table 1: Configuration [(PK) ID, coolTemp, heatTemo, desc]
Table 2: Status [state, dateTime]
Table 3: Measurement [(PK) ID, temp, dateTime]

## AWS EC2
http://3.132.111.9:8080/

## Commands
index
`curl http://3.132.111.9:8080/ # List all objects`

GET id
`curl http://3.132.111.9:8080/id # query based on id`

POST
`curl http://3.132.111.9:8080/ -Method POST -Body "<string>" #init and add to table`

PUT
`curl http://3.132.111.9:8080/ -Method PUT #init on table`

DELETE
`curl http://3.132.111.9:8080/id -Method DELETE #remove from table`
