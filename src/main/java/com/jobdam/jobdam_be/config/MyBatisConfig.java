package com.jobdam.jobdam_be.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@MapperScan(basePackages = "com.jobdam.jobdam_be.**") // Mapper 인터페이스 경로 지정
public class MyBatisConfig {

    private final DataSource dataSource;
    // 수정 *
    public MyBatisConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        org.apache.ibatis.type.TypeAliasRegistry aliasRegistry = Objects.requireNonNull(factoryBean.getObject()).getConfiguration().getTypeAliasRegistry();
        aliasRegistry.registerAlias("user", com.jobdam.jobdam_be.user.model.User.class);
        return factoryBean.getObject();
    }
}
