package xyz.proteanbear.muscida;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.proteanbear.muscida.utils.PropertiesUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Spring data source integration configuration. </p>
 * <p>Use Configuration-based configuration to make it easier to distinguish between stand-alone and cluster configurations. </p>
 * <p>Supports dynamic configuration of multiple data sources. </p>
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/27
 * @since jdk1.8,Spring4.0
 */
@Configuration
public class DataSourceConfiguration
{
    /**
     * log
     */
    private static Logger logger=LoggerFactory.getLogger(DataSourceConfiguration.class);

    /**
     * Specify the full index name of the database, ie the name after each jdbc configuration ‘jdbc.’
     * Multiple data sources are separated by commas, such as mysql, oracle
     * Read and write separated master and slave separated by a colon and a vertical bar (write the first library), such as mysql:master|slave01|slave02
     * Single source read and write separation in multiple data sources, such as mysql:master|slave01,oracle:master|slave02
     */
    @Value("${jdbc.server.names}")
    private String serverNames;

    /**
     * Base package location
     */
    @Value("${project.basePackage}")
    private String basePackage;

    /**
     * Generate dynamic data sources based on configuration
     *
     * @return data source
     */
    @Bean("dataSource")
    public DataSource dynamicDataSource() throws IOException, SQLException
    {
        DynamicDataSource dataSource=new DynamicDataSource();

        //All data sources used for recording
        Map<Object,Object> targetDataSources=new LinkedHashMap<>();

        //Decomposition data source
        String[] names=DataSourceStringSplitUtils.splitDataServers(serverNames);
        for(String name : names) targetDataSources.put(name,druidDataSource(name));

        //Set all data sources
        dataSource.setTargetDataSources(targetDataSources);
        //Set the default data source for the main library of the first data service
        dataSource.setDefaultTargetDataSource(targetDataSources.values().iterator().next());
        if(logger.isDebugEnabled())
        {
            for(Object serverName : targetDataSources.keySet())
            {
                logger.debug("Add data source '{}' for url '{}'",serverName,
                             ((DruidDataSource)targetDataSources.get(serverName)).getUrl());
            }
        }

        return dataSource;
    }

    /**
     * @return Data source switching aspect
     */
    @Bean("dataSourceAspect")
    public DynamicDataSource.Aspect dataSourceAspect()
    {
        DynamicDataSource.Aspect aspect=new DynamicDataSource.Aspect();
        aspect.setServerNames(serverNames);
        aspect.setSlaveMethodPrefix("list","count","get","query","find");
        return aspect;
    }

    /**
     * Build a data source connection pool object
     *
     * @param serverName service name
     * @return Data source connection pool
     */
    private DruidDataSource druidDataSource(String serverName) throws IOException, SQLException
    {
        //Loading attribute configuration file
        Properties jdbc=PropertiesUtils.load("jdbc");
        Properties pool=PropertiesUtils.load("pool");

        //Create a data source object
        DruidDataSource dataSource=new DruidDataSource();

        //Configure connection properties
        String jdbcPrefix="jdbc."+serverName+".";
        dataSource.setUrl(jdbc.getProperty(jdbcPrefix+"url"));
        dataSource.setDriverClassName(jdbc.getProperty(jdbcPrefix+"driver"));
        dataSource.setUsername(jdbc.getProperty(jdbcPrefix+"user"));
        dataSource.setPassword(jdbc.getProperty(jdbcPrefix+"password"));

        //Configure connection pool properties
        //Configure the initialization size, minimum, maximum
        dataSource.setInitialSize(Integer.parseInt(pool.getProperty("pool.initialSize")));
        dataSource.setMaxActive(Integer.parseInt(pool.getProperty("pool.maxActive")));
        dataSource.setMinIdle(Integer.parseInt(pool.getProperty("pool.minIdle")));

        //Configure the time to get connection timeout
        dataSource.setMaxWait(Integer.parseInt(pool.getProperty("pool.maxWait")));

        //How long does the configuration interval take to perform a test
        //and detect idle connections that need to be closed in milliseconds?
        dataSource.setTimeBetweenEvictionRunsMillis(
                Long.parseLong(pool.getProperty("pool.timeBetweenEvictionRunsMillis"))
        );

        //Configure the minimum lifetime for a connection in the pool, in milliseconds
        dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(pool.getProperty("pool.minEvictableIdleTimeMillis")));

        //Verify the validity of the database connection and require a query statement
        dataSource.setValidationQuery(pool.getProperty("pool.validationQuery"));
        dataSource.setTestOnBorrow(Boolean.parseBoolean(pool.getProperty("pool.testOnBorrow")));
        dataSource.setTestOnReturn(Boolean.parseBoolean(pool.getProperty("pool.testOnReturn")));
        dataSource.setTestWhileIdle(Boolean.parseBoolean(pool.getProperty("pool.testWhileIdle")));

        //Whether to cache preparedStatement, PSCache.
        //SCache greatly improves the performance of databases that support cursors, such as oracle. Suggest closed in mysql.
        dataSource.setPoolPreparedStatements(Boolean.parseBoolean(pool.getProperty("pool.poolPreparedStatements")));
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(
                Integer.parseInt(pool.getProperty("pool.maxPoolPreparedStatementPerConnectionSize"))
        );

        //Idle connection off
        dataSource.setRemoveAbandoned(Boolean.parseBoolean(pool.getProperty("pool.removeAbandoned")));
        dataSource.setRemoveAbandonedTimeout(Integer.parseInt(pool.getProperty("pool.removeAbandonedTimeout")));
        dataSource.setLogAbandoned(Boolean.parseBoolean(pool.getProperty("pool.logAbandoned")));

        //Configure monitoring statistics interception filters; removed after the monitoring interface sql can not statistics
        dataSource.setFilters("stat,slf4j");
        dataSource.setUseGlobalDataSourceStat(Boolean.parseBoolean(pool.getProperty("pool.useGlobalDataSourceStat")));
        dataSource.setConnectionProperties(
                "druid.stat.mergeSql="
                        +pool.getProperty("pool.mergeSql")+";"
                        +"druid.stat.slowSqlMillis="
                        +pool.getProperty("pool.slowSqlMillis")+";"
                        +"druid.stat.logSlowSql="
                        +pool.getProperty("pool.logSlowSql")
        );

        return dataSource;
    }
}