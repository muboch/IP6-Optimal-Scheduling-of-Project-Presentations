# IP6-Optimal-Scheduling-of-Project-Presentations
Optimal Scheduling of Project Presentations

## Setup for Test Environment

### DB
```
docker run -d -p 6033:3306 --name=docker-mysql --env="MYSQL_ROOT_PASSWORD=root" --env="MYSQL_PASSWORD=root" --env="MYSQL

docker exec -it docker-mysql bash;

mysql -uroot -proot

CREATE DATABASE osppdb;

USE DATABASE osppdb;

CREATE USER 'ospp'@'localhost' IDENTIFIED BY 'Change_me_2020';

GRANT ALL PRIVILEGES ON * . * TO 'ospp'@'localhost';
 
FLUSH PRIVILEGES;
```

### Adminer
```
docker run --link docker-mysql:db -p 9000:9000 adminer:fastcgi
```
### Application
```
docker run -e "SPRING_PROFILES_ACTIVE=test" -d -p 8080:8080 bananenhoschi/ospp-application
```