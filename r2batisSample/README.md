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
    implementation 'io.github.shanpark:r2batis-spring-boot-starter:0.1.6'
    ...
}
```

## 2. R2dbc Driver 추가

- 2023년 12월 현재 최신 버전이지만 계속 업그레이드 되고 있으며 **상위 버전에서 사라지는 버그가 꽤 있으므로 최신 버전 조회**하여 넣을 것.  
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
- Tomcat에 WAR로 배포하는 경우 Oracle JDBC 드라이버를 못찾는 에러가 발생한다. Jar 로 실행하는 경우에는 잘된다. (원인 불명)
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

- application.yml 파일의 주석을 읽어보면 하나의 DB만 사용할 때에 대한 설명이 있다.
- application.yml 의 설정만 하면 mapper interface, mapper xml 을 정의하여 바로 사용할 수 있다.

### 4-2. DB가 2개 이상인 경우

- 다음 파일들 참고
  - config/R2batisConfig.java : ConnectionFactory, TransactionManager, R2batisProperties bean 등록 방법 참고
  - mapper/UserMapper.java : Mapper 인터페이스 작성 시 @R2dbcMapper 어노테이션 설정 참고
  - service/UserService : Transaction 메서드 작성 시 @Transactional 어노테이션 설정 참고.
- 현재 코드가 여러 DB를 사용할 때를 대비한 샘플 코드이다.
- 연결해야 하는 DB의 수 만큼 ConnectionFactory가 필요하고 각 DB에 대한 ConnectionFactory bean을 등록해야 한다. (R2batisConfig 클래스를 참조.)
- TransactionManager가 필요한, 즉 Transaction 처리를 사용할 DB에 대해서는 해당 ConnectionFactory에 대한 TransactionManager도 bean으로 등록해야 한다. (R2batisConfig 클래스를 참조.)  
  이 경우 @Transactional 어노테이션에서 transactionManager 속성으로 원하는 transactionManager bean을 지정해줘야 한다.

### 4-3. Database ID를 통한 Mapper 설정

- R2batisConfig 클래스의 DatabaseIdProvider bean 을 참조한다.
- DatabaseIdProvider bean이 있으면 자동으로 해당 bean을 사용하며 databaseId 가 일치하는 SQL 들만 로딩하여 인터페이스에 맵핑된다. 

## 5. Mapper Examples

- 소스코드에서 resources/mapper/ 하위에 있는 xml 파일들 참고. 

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<mapper namespace="shanpark.r2batissample.mapper.UserMapper" >

    <select id="getUserCount" resultType="java.lang.Long" databaseId="mysql">
        SELECT COUNT(*)
        FROM User
    </select>

    <select id="getUserCountWithNameStarting" resultType="java.lang.Long" databaseId="mysql">
        SELECT COUNT(*)
        FROM User
        <where>
            <if test="prefix != null">
                name LIKE CONCAT(:prefix, '%')
            </if>
        </where>
    </select>

    <select id="getUser" resultType="shanpark.r2batissample.vo.UserVo" databaseId="mysql">
        SELECT *
        FROM User
        WHERE id = :userId
    </select>

    <select id="getUserById" resultType="shanpark.r2batissample.vo.UserVo" databaseId="mysql">
        SELECT *
        FROM User
        <where>
            <choose>
                <when test="userId == 21L">
                    id = 21
                </when>
                <when test="userId == 22">
                    id = 22
                </when>
                <when test="userId == 23">
                    id = 23
                </when>
                <when test="userId == 24">
                    id = 24
                </when>
                <when test="userId == 25">
                    id = 25
                </when>
                <otherwise>
                    id = 30
                </otherwise>
            </choose>
        </where>
    </select>

    <select id="getUserListByIds" resultType="shanpark.r2batissample.vo.UserVo" databaseId="mysql">
        SELECT *
        FROM User
        <where>
            <if test="!userIds.isEmpty()">
                id IN
                    <foreach collection="userIds" item="userId" separator="," open="(" close=")">
                        :userId
                    </foreach>
            </if>
            OR 1 = 0
        </where>
        ORDER BY id ASC
    </select>

    <select id="getAllUserList" resultType="shanpark.r2batissample.vo.UserVo" databaseId="mysql">
        SELECT *
        FROM User
    </select>

    <select id="getUserWithNameStarting" resultType="shanpark.r2batissample.vo.UserVo" databaseId="mysql">
        SELECT *
        FROM User
        <where>
            <if test="prefix != null">
                name LIKE CONCAT(:prefix, '%')
            </if>
        </where>
    </select>

    <insert id="insertUserAndGetKeyAndInsertTime" useGeneratedKeys="true" keyColumn="id" resultType="java.lang.Long" databaseId="mysql">
        <selectKey keyProperty="userVo.inserted1" resultType="java.util.Date" order="BEFORE">
            SELECT NOW()
        </selectKey>
        <selectKey keyProperty="userVo.inserted2" resultType="java.util.Date" order="BEFORE">
            SELECT STR_TO_DATE(ADDTIME(:userVo.inserted1, '1'), '%Y-%m-%d %H:%i:%s');
        </selectKey>

        INSERT INTO User
            (name, username, email)
        VALUES
            (:name, :username, :email)

        <selectKey keyProperty="userVo.inserted3" resultType="java.util.Date" order="after">
            SELECT STR_TO_DATE(ADDTIME(:userVo.inserted2, '1'), '%Y-%m-%d %H:%i:%s');
        </selectKey>
        <selectKey keyProperty="userVo.inserted4" resultType="java.util.Date" order="AFTER">
            SELECT STR_TO_DATE(ADDTIME(:userVo.inserted3, '1'), '%Y-%m-%d %H:%i:%s');
        </selectKey>
    </insert>

    <insert id="insertUserList" databaseId="mysql"> <!-- default resultType is Long -->
        INSERT INTO User
            (name, username, email)
        VALUES
        <foreach item="item" collection="userList" separator=",">
            (:item.name, :item.username, :item.email)
        </foreach>
    </insert>

    <update id="updateUserAddressWithNameStarting" databaseId="mysql">
        <selectKey keyProperty="userVo.inserted1" resultType="java.util.Date" order="BEFORE">
            SELECT NOW()
        </selectKey>

        UPDATE User
        <set>
            address = :address
        </set>
        WHERE name LIKE CONCAT(:prefix, '%')

        <selectKey keyProperty="userVo.inserted2" resultType="java.util.Date" order="BEFORE">
            SELECT STR_TO_DATE(ADDTIME(:userVo.inserted1, '1'), '%Y-%m-%d %H:%i:%s');
        </selectKey>
    </update>

    <insert id="insertUserForSpecialCase" databaseId="mysql">
        INSERT INTO User
            (name, username, email, password, address)
        VALUES
        <foreach collection="passwordList" item="password" separator="," index="pwIndex">
            <foreach collection="addressList" item="address" separator="," index="addrIndex">
                (
                    CONCAT(:name, '_', :pwIndex, '_', :addrIndex),
                    CONCAT(:username, '_', :pwIndex, '_', :addrIndex),
                    CONCAT(:email, '_', :pwIndex, '_', :addrIndex, "@iotree.co.kr"),
                    :password,
                    :address
                )
            </foreach>
        </foreach>
    </insert>

    <select id="getUserListForSpecialCase" resultType="shanpark.r2batissample.vo.UserVo" databaseId="mysql">
        SELECT *
        FROM `User` u
        WHERE id IN
            <foreach collection="oneList" item="item" separator="," open="(" close=")" >
                <foreach collection="manyList" item="item" separator="," >
                    :item
                </foreach>
                ,:item
            </foreach>
        ORDER BY id ASC
    </select>

    <delete id="deleteUserWithNameStarting" databaseId="mysql">
        DELETE FROM User
        WHERE name LIKE CONCAT(:prefix, '%')
    </delete>

</mapper>
```