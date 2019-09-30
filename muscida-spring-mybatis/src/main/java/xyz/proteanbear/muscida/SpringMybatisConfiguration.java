package xyz.proteanbear.muscida;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Mybatis configuration in spring.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/03
 * @since jdk1.8
 */
@Configuration
@Import(DataSourceConfiguration.class)
public class SpringMybatisConfiguration
{
    /**
     * code base package
     */
    @Value("${project.basePackage}")
    private String basePackage;

    /**
     * Spring uses sqlSessionFactoryBean to manage mybatis's sqlSessionFactory
     * when using mybatis
     * @return sqlSessionFactory
     */
    @Bean("sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
    {
        SqlSessionFactoryBean factory=new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        return factory;
    }

    /**
     * Dynamic proxy implementation without writing "dao"'s implementation.
     * @return scanner configurer
     */
    @Bean("mapperScannerConfigurer")
    public MapperScannerConfigurer mapperScannerConfigurer()
    {
        MapperScannerConfigurer configurer=new MapperScannerConfigurer();
        configurer.setBasePackage(basePackage==null?"xyz.proteanbear":basePackage);
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return configurer;
    }

    /**
     * @param dataSource data source
     * @return Transaction manager
     */
    @Bean("transactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource)
    {
        DataSourceTransactionManager manager=new DataSourceTransactionManager();
        manager.setDataSource(dataSource);
        return manager;
    }
}