package com.jobdam.jobdam_be.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@MapperScan(basePackages = "com.jobdam.jobdam_be.user.mapper") // Mapper 인터페이스 경로 지정
@RequiredArgsConstructor
public class MyBatisConfig {

    private final DataSource dataSource;

//    @Bean
//    public SqlSessionFactory sqlSessionFactory() throws Exception {
//        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
//        factoryBean.setDataSource(dataSource);
//        org.apache.ibatis.type.TypeAliasRegistry aliasRegistry = Objects.requireNonNull(factoryBean.getObject()).getConfiguration().getTypeAliasRegistry();
//        aliasRegistry.registerAlias("user", com.jobdam.jobdam_be.user.model.User.class);
//        return factoryBean.getObject();
//    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // ✅ XML 매퍼 위치 설정
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:/mappers/*.xml")
        );

        // ✅ alias 등록 (선택 사항)
        org.apache.ibatis.type.TypeAliasRegistry aliasRegistry =
                Objects.requireNonNull(factoryBean.getObject()).getConfiguration().getTypeAliasRegistry();
        aliasRegistry.registerAlias("user", com.jobdam.jobdam_be.user.model.User.class);

        return factoryBean.getObject();
    }
}
