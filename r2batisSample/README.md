# R2batis Sample Project

## 1. R2batis 추가

- 최신 버전: [![](https://www.jitpack.io/v/shanpark/r2batis-spring-boot-starter.svg)](https://www.jitpack.io/#shanpark/r2batis-spring-boot-starter)

### build.gradle

```
...

repositories {
    mavenCentral()    
    maven { url 'https://jitpack.io' }
}

dependencies {
    ...
    implementation 'io.github.shanpark:r2batis-spring-boot-starter:0.1.5'
    ...
}
```

## 2. R2dbc Driver 추가

- 2023년 12월 현재 최신 버전이지만 계속 업그레이드 되고 있으며 상위 버전에서 사라지는 버그가 꽤 있으므로 최신 버전 조회하여 넣을 것.  
- 여러 DB를 지원해야 하는 경우 모두 dependency로 추가해도 문제는 없다.

### 2-1. Oracle

```
runtimeOnly 'com.oracle.database.r2dbc:oracle-r2dbc:1.2.0'
```

### 2-2. MySQL

```
runtimeOnly 'io.asyncer:r2dbc-mysql:1.0.4'
```

### 2-3. Maria

```
runtimeOnly 'org.mariadb:r2dbc-mariadb:1.1.4'
```

## 3. R2dbc Driver test notes

### 3-1. Oracle

- Oracle용 r2dbc 드라이버의 공식 문서상으로는 Oracle 18 이후 버전에서 공식 지원한다고 한다.
- java.util.Date 타입의 값을 파라메터로 전달하거나 DB의 시간값을 읽어와서 java.util.Date 타입으로 받거나 모두 타임존 정보를 무시하고 각자의 로컬 타임으로 해석해서 사용한다.

### 3-2. MySQL

- java.util.Date 타입의 파라메터를 전달하면 클라이언트의 타임존 정보가 무시되고 DB 서버의 시간대로 인식된다.
- DB로부터 전달된 DATETIME 값을 java.util.Date로 받으면 DB서버의 타임존 시간이라고 보고 로컬 시간대로 변환되어 받는다.
- ZonedDateTime 타입을 사용하면 타임존 정보가 정상적으로 유지된다.

### 3-3. MariaDB

- 테스트 서버가 같은 시간대로 설정되어 있어서 java.util.Date 가 정상적으로 타임존 정보가 유지되는지 테스트 해보지 못함.
- ZonedDateTime 을 파라메터로 전달하면 r2dbc 드라이버가 codec이 없다고 한다. (미지원으로 보임.)

## 4. Sample 설명

### 4-1. DB가 1개인 경우

- application.yml 파일의 주석을 읽어보면 하나의 DB만 사용할 때에 대한 설명이 있으며 훨씬 간단하다.
- application.yml 의 설정만 하면 mapper interface, mapper xml 을 정의하여 바로 사용할 수 있다.
- 

### 4-2. DB가 2개 이상인 경우

- 현재 코드가 여러 DB를 사용할 때를 대비한 샘플 코드이다. 각 코드의 주석을 잘 읽어보면 대부분 쉽게 사용가능하다.
- default ConnectionFactory를 사용하지 못하고 각 DB에 대한 ConnectionFactory bean을 등록해야 한다. (R2batisConfig 클래스를 참조.)
- TransactionManager가 필요한, Transaction 처리가 필요한 DB에 대해서는 해당 ConnectionFactory에 대한 TransactionManager도 bean으로 등록해야 한다. (R2batisConfig 클래스를 참조.)  
  이 경우 @Transactional 어노테이션에서 transactionManager 속성으로 원하는 transactionManager bean을 지정해줘야 한다.

### 4-3. Database ID를 통한 Mapper 설정

- R2batisConfig 클래스의 DatabaseIdProvider bean 을 참조한다.
- DatabaseIdProvider bean이 있으면 자동으로 해당 bean을 사용하며 databaseId 가 일치하는 SQL 들만 로딩하여 인터페이스에 맵핑된다. 
