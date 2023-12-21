package shanpark.r2batissample.config;

import io.github.shanpark.r2batis.core.DatabaseIdProvider;
import io.github.shanpark.r2batis.core.VendorDatabaseIdProvider;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class R2batisConfig {

    /**
     * Multi DB를 지원하는 경우 각 DB 별로 mapper를 작성하려면 DB 식별자를 반환하는 DatabaseIdProvider bean을 등록해야 한다.
     * DatabaseIdProvider bean이 있는 경우에 Mapper XML을 로딩할 때 이 bean이 반환하는 databaseId 값과 같은 databaseId를 갖는
     * 구문들만 로딩이 된다.
     * DB가 하나라면 Mapper XML 에서도 databaseId를 지정할 필요 없고 이 bean도 필요가 없다.
     */
    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        // ConnectionFactory의 metadata가 반환하는 name 값이 아래 생성된 Map의 key값을 포함하면
        // 해당 entry의 value 값이 databaseId 값이 되며 이 값이 Mapper XML 에서 databaseId로 사용되는 값이다.
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Map<String, String> databaseIdMap = new HashMap<>();
        databaseIdMap.put("MariaDB", "mariadb");
        databaseIdMap.put("MySQL", "mysql");
        databaseIdMap.put("Oracle", "oracle");
        databaseIdMap.put("SQL Server", "sqlserver");
        databaseIdMap.put("PostgreSql", "postgresql");
        databaseIdMap.put("DB2", "db2");
        databaseIdMap.put("HSQL", "hsqldb");
        databaseIdMap.put("H2", "h2");
        databaseIdMap.put("Derby", "derby");
        databaseIdProvider.setDatabaseIdMap(databaseIdMap);
        return databaseIdProvider;
    }

    /**
     * 2개 이상의 DB에 연결해야 한다면 직접 ConnectionFactory bean을 등록하고 @R2dbcMapper 어노테이션에서 어떤
     * ConnectionFactory를 사용하는지 지정해줘야 한다. 이 때 bean의 이름이 사용되기 떄문에 ConnectionFactory bean을
     * 선언할 때 반드시 name을 지정하도록 한다.
     * 참고로 @Bean 어노테이션에 name을 지정하지 않으면 method 이름이 bean의 이름이 된다.
     */
    @Bean("oracle") // bean 이름을 지정하고 사용하는 것이 좋다.
    public ConnectionFactory oracleConnectionFactory() {
        return ConnectionFactoryBuilder.withUrl("r2dbc:oracle://192.168.1.156:1521/freepdb1")
                .username("TestDB")
                .password("Iotree123!@#")
                .build();
    }

    /**
     * ConnectionFactory가 2개 이상이 되면 @Transactional 어노테이션을 사용할 때도 어노테이션의 transactionManager 값을
     * 지정해줘야 한다. TransactionManager bean을 등록하는 메소드의 파라메터로 ConnectionFactory를 받는데 어떤
     * ConnectionFactory를 받아서 사용할 것인지는 파라메터 이름을 따라가므로 ConnectionFactory bean의 이름과 동일하게 한다.
     *
     * @param oracle TransactionManager를 생성할 ConnectionFactory 객체. 파라메터 이름이 oracle이므로 "oracle" 이라고
     *               이름지어진 ConenctionFactory bean을 받을 것이다.
     * @return 파라메터로 받은 ConnectionFactory 객체의 R2dbcTransactionManager bean 객체.
     */
    @Bean // bean이름을 지정하지 않으면 메소드 이름(oracleTM)이 자동으로 bean의 이름이 된다.
    public R2dbcTransactionManager oracleTM(ConnectionFactory oracle) {
        return new R2dbcTransactionManager(oracle);
    }

    @Bean("mariadb")
    public ConnectionFactory mariaConnectionFactory() {
        return ConnectionFactoryBuilder.withUrl("r2dbc:pool:mariadb://192.168.1.20:3307/TestDB")
                .username("tester")
                .password("Tester123!@#")
                .build();
    }

    @Bean // bean이름을 지정하지 않으면 메소드 이름(mariadbTM)이 자동으로 bean의 이름이 된다.
    public R2dbcTransactionManager mariadbTM(ConnectionFactory mariadb) {
        return new R2dbcTransactionManager(mariadb);
    }

    @Bean("mysql")
    public ConnectionFactory mysqlConnectionFactory() {
        return ConnectionFactoryBuilder.withUrl("r2dbc:pool:mysql://192.168.1.156:3308/TestDB?serverZoneId=Asia/Seoul")
                .username("tester")
                .password("Tester123!@#")
                .build();
    }

    @Bean("mysqlTM") // 직접 이름을 지정해도 된다.
    public R2dbcTransactionManager mysqlTM(ConnectionFactory mysql) {
        return new R2dbcTransactionManager(mysql);
    }
}
