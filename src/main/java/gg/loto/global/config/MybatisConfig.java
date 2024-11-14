package gg.loto.global.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"gg.loto.user.mapper"})
public class MybatisConfig {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // Mapper XML 위치 설정
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/**/*.xml"));
        
        // MyBatis Configuration 설정
        org.apache.ibatis.session.Configuration configuration = 
                new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);  // camelCase 자동 매핑
        configuration.setJdbcTypeForNull(JdbcType.NULL);  // NULL 처리
        configuration.setCallSettersOnNulls(true);        // NULL 값도 setter 호출
        configuration.setCacheEnabled(false);             // 캐시 비활성화
        configuration.setDefaultStatementTimeout(30);     // 쿼리 실행 타임아웃 (초)
        
        sessionFactory.setConfiguration(configuration);
        
        return sessionFactory.getObject();
    }
}
