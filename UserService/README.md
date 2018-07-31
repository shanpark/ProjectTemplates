# UserService

### Sample for ...
- User Sign In
- User Log In
- User Log Out
- Access control by Spring security
- View rendering by Thymeleaf
- MyBatis basic sample
- Custom Validator by Annotation (Spring Validator)
- Centered box UI (HTML, CSS)
- Animated Input Field (HTML, CSS, JQuery)
- JQuery UI (Datepicker)

### Used Tech
- Spring Boot Framework.
- Spring WebMVC
- Spring Security
- Thymeleaf (View Engine)
- MyBatis Configuration (MariaDB)
- Logback Configuration
- HTML5, CSS, JQuery

### Env
- IDE : IntelliJ IDEA CE
- Build Tool : Gradle
- Packaging : WAR + JAR

### Used DB Table (MariaDB)
<pre><code>
CREATE TABLE `SignUpUser` (
  `email` varchar(512) NOT NULL,
  `password` varchar(130) NOT NULL,
  `nickname` varchar(128) NOT NULL,
  `firstName` varchar(128) NOT NULL,
  `lastName` varchar(128) NOT NULL,
  `modified` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

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
  CONSTRAINT `uid` FOREIGN KEY (`uid`) REFERENCES `User` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8
</code></pre>