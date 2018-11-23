# RestService with JWT (Java Web Token)

### Sample for ...
- RESTful API (JSON)
- Access control by Spring Security (Authorization with JWT)
- MyBatis basic sample

### Used Tech
- Spring Boot Framework.
- Spring Security (by JWT)
- MyBatis Configuration (MariaDB)
- Logback Configuration

### Env
- IDE : IntelliJ IDEA CE
- Build Tool : Gradle
- Packaging : WAR + JAR

### Note
- It is assumed that user registration has already been made 
- Tested by Postman. (https://www.getpostman.com/)

### Used DB Table (MariaDB)
<pre><code>
CREATE TABLE `User` (
  `uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(512) NOT NULL,
  `password` varchar(130) NOT NULL,
  `nickname` varchar(128) NOT NULL,
  `firstName` varchar(128) NOT NULL,
  `lastName` varchar(128) NOT NULL,
  `hp` varchar(20) DEFAULT NULL,
  `birth` datetime DEFAULT NULL,
  `status` varchar(10) NOT NULL DEFAULT 'ACTIVE',
  `modified` datetime NOT NULL DEFAULT current_timestamp(),
  `created` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`uid`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `nickname_UNIQUE` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8

CREATE TABLE `UserRole` (
  `uid` bigint(20) NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`uid`,`role`),
  CONSTRAINT `uid` FOREIGN KEY (`uid`) REFERENCES `User` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
</code></pre>
