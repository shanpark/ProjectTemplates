package shanpark.multidb.mapper.db1;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Slf4j
@Configuration
@MapperScan(value = "shanpark.multidb.mapper.db1", sqlSessionFactoryRef = "db1SqlSessionFactory") // mapper interface package 지정
public class Db1Config {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${spring.db1.datasource.mapper-locations}")
    private String mapperLocations;
    @Value("${spring.db1.datasource.config}")
    private String configPath;

    @Bean(name = "db1DataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.db1.datasource")
    public DataSource db1DataSource() {
        // 메소드의 어노테이션 @ConfigurationProperties 에서 지정된 값으로 datasource 생성
        // - {prefix}.driver-class-name
        // - {prefix}.jdbc-url
        // - {prefix}.username
        // - {prefix}.password
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "db1SqlSessionFactory")
    @Primary
    public SqlSessionFactory db1SqlSessionFactory(@Qualifier("db1DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources(mapperLocations)); // mapper xml 리소스 지정.

        // 이 session factory의 custom 옵션을 지정하려면 아래 2줄처럼 지정한다. 기본 그대로 사용한다면 삭제해도 좋다.
        Resource myBatisConfig = new PathMatchingResourcePatternResolver().getResource(configPath);
        sqlSessionFactoryBean.setConfigLocation(myBatisConfig);

        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "db1SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate db1SqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
