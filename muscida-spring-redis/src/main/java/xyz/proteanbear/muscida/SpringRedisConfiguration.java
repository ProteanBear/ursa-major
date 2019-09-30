package xyz.proteanbear.muscida;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * <p>Spring and Redis integration configuration. </p>
 * <p>Use Configuration-based configuration to make it easier to distinguish between stand-alone and cluster configurations. </p>
 * <p>Supports using Redis to cache data. </p>
 * <p>Support for opening subscription services. </p>
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/12
 * @since jdk1.8
 */
@Configuration
@PropertySource("classpath:redis.properties")
public class SpringRedisConfiguration
{
    /**
     * Log
     */
    private static Logger logger=LoggerFactory.getLogger(SpringRedisConfiguration.class);

    /**
     * Redis service model - stand-alone
     */
    private static final String SERVER_MODE_SIMPLE="simple";

    /**
     * Redis Service Mode - Master and slave
     */
    private static final String SERVER_MODE_SENTINEL="sentinel";

    /**
     * Redis service model - cluster
     */
    private static final String SERVER_MODE_CLUSTER="cluster";

    /**
     * Separator between multiple servers
     */
    private static final String SPLIT_SERVER=",";

    /**
     * Separator between server and port
     */
    private static final String SPLIT_PORT=":";

    /**
     * Specify Redis mode, set to simple/sentinel/cluster
     */
    @Value("${redis.server.mode}")
    private String mode;

    /**
     * Connection timeout in milliseconds
     */
    @Value("${redis.server.connectTimeout}")
    private Long connectTimeout;

    /**
     * Read timeout, in milliseconds
     */
    @Value("${redis.server.readTimeout}")
    private Long readTimeout;

    /**
     * Server address and port, cluster mode uses commas to separate multiple servers;
     * Mode is set to simple, even if you set up more than one server, it will only take the first one
     */
    @Value("${redis.server.address}")
    private String address;

    /**
     * Access password
     */
    @Value("${redis.server.password}")
    private String password;

    /**
     * Whether to use connection pool
     */
    @Value("${redis.server.isUsePool}")
    private boolean isUsePool;

    /**
     * Whether to use SSL connection
     */
    @Value("${redis.server.isUseSSL}")
    private boolean isUseSSL;

    /**
     * Specified in active and standby mode
     * This value must be consistent with the value of the master specified in Sentinel, otherwise the Sentinel will not be reported when starting.
     */
    @Value("${redis.server.masterName}")
    private String masterName;

    /**
     * Whether to open the transaction
     */
    @Value("${redis.execute.enableTransactionSupport}")
    private boolean enableTransactionSupport;

    /**
     * Maximum connections: The maximum number of links
     * that can be established at the same time
     */
    @Value("${redis.pool.maxTotal}")
    private Integer maxTotal;

    /**
     * Maximum idle number: When the number of free links is greater than maxIdle,
     * it will be recycled
     */
    @Value("${redis.pool.maxIdle}")
    private Integer maxIdle;

    /**
     * Minimum number of idles: A new link will be created when minIdle is below
     */
    @Value("${redis.pool.minIdle}")
    private Integer minIdle;

    /**
     * The maximum number of milliseconds to wait while getting a connection (if set to BlockWhenExhausted when blocking),
     * If the time out throws an exception, less than zero: blocking indefinite time, default -1
     */
    @Value("${redis.pool.maxWaitMillis}")
    private Long maxWaitMillis;

    /**
     * Minimum idle time for eviction connection Default 1800000 milliseconds (30 minutes)
     */
    @Value("${redis.pool.minEvictableIdleTimeMillis}")
    private Long minEvictableIdleTimeMillis;

    /**
     * The maximum number of evictions at the time of each eviction check is negative if it is: 1/abs(n), default 3
     */
    @Value("${redis.pool.numTestsPerEvictionRun}")
    private Integer numTestsPerEvictionRun;

    /**
     * How long the object is idle after it is evicted, when the idle time> the value and idle connection> maximum idle number
     * No longer based on MinEvictableIdleTimeMillis (default eviction policy)
     */
    @Value("${redis.pool.softMinEvictableIdleTimeMillis}")
    private Long softMinEvictableIdleTimeMillis;

    /**
     * Maximum connections: The maximum number of links that can be established at the same time
     */
    @Value("${redis.pool.testOnCreate}")
    private boolean testOnCreate;

    /**
     * Check validity when getting a connection
     */
    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;

    /**
     * Check validity at return connection
     */
    @Value("${redis.pool.testOnReturn}")
    private boolean testOnReturn;

    /**
     * Check validity in your free time
     */
    @Value("${redis.pool.testWhileIdle}")
    private boolean testWhileIdle;

    /**
     * Departing scan interval (milliseconds) If negative, eviction thread is not run, default -1
     */
    @Value("${redis.pool.timeBetweenEvictionRunsMillis}")
    private Long timeBetweenEvictionRunsMillis;

    /**
     * Whether or not the connection is blocked when exhausted. false reports an exception, and true blocks until it times out. The default value is true.
     */
    @Value("${redis.pool.blockWhenExhausted}")
    private boolean blockWhenExhausted=true;

    /**
     * Sets the eviction policy class name, t
     * he default DefaultEvictionPolicy (when the connection exceeds the maximum idle time,
     * or the number of connections exceeds the maximum number of idle connections)
     */
    @Value("${redis.pool.evictionPolicyClassName}")
    private String evictionPolicyClassName="org.apache.commons.pool2.impl.DefaultEvictionPolicy";

    /**
     * Whether to enable the pool's jmx management function, the default is true
     */
    @Value("${redis.pool.jmxEnabled}")
    private boolean jmxEnabled=true;

    /**
     *
     */
    @Value("${redis.pool.jmxNamePrefix}")
    private String jmxNamePrefix="pool";

    /**
     * Whether to enable last in first out, default is true
     */
    @Value("${redis.pool.lifo}")
    private boolean lifo=true;

    /**
     * Whether to enable last in first out, default is true
     */
    @Value("${redis.cache.enable}")
    private boolean cacheEnable;

    /**
     * Cache timeout in minutes, 0 is not timeout
     */
    @Value("${redis.cache.ttl}")
    private Long ttl;

    /**
     * Whether to save null content
     */
    @Value("${redis.cache.cacheNullValues}")
    private boolean cacheNullValues;

    /**
     * Whether to use the key name prefix
     */
    @Value("${redis.cache.usePrefix}")
    private boolean usePrefix;

    /**
     * Specifies the key name prefix to use
     */
    @Value("${redis.cache.keyPrefix}")
    private String keyPrefix;

    /**
     * Whether to enable message subscription
     */
    @Value("${redis.subscribe.enable}")
    private boolean subscribeEnable;

    /**
     * Subscribed channel name prefix
     * Uniformly use schema subscriptions, use'*' as a wildcard; there is no need to use'*', it will be added automatically when created
     * Use this prefix + specific sub-channel name when sending
     */
    @Value("${redis.subscribe.patternPrefix}")
    private String patternPrefix;

    /**
     * @return Redis connection pool configuration
     */
    @Bean("redisPoolConfig")
    public JedisPoolConfig poolConfig()
    {
        JedisPoolConfig config=new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
        config.setTestOnCreate(testOnCreate);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setTestWhileIdle(testWhileIdle);
        config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        config.setBlockWhenExhausted(blockWhenExhausted);
        config.setEvictionPolicyClassName(evictionPolicyClassName);
        config.setJmxEnabled(jmxEnabled);
        config.setJmxNamePrefix(jmxNamePrefix);
        config.setLifo(lifo);

        return config;
    }

    /**
     * @return redis connection factory
     */
    @Bean("redisConnectionFactory")
    public JedisConnectionFactory connectionFactory(
            @Qualifier("redisPoolConfig") JedisPoolConfig config)
    {
        JedisConnectionFactory factory;

        //Get service address
        if(StringUtils.isEmpty(address))
        {
            logger.error("Null redis server address!");
            return null;
        }
        String[] addressArray=address.split(SPLIT_SERVER);
        String currentAddress, currentPort;

        //Connection configuration
        JedisClientConfiguration.DefaultJedisClientConfigurationBuilder configurationBuilder=(JedisClientConfiguration.DefaultJedisClientConfigurationBuilder)JedisClientConfiguration
                .builder();
        if(isUsePool) configurationBuilder.usePooling();
        if(isUseSSL)
        {
            configurationBuilder.useSsl();
            //Set up SSL
        }
        configurationBuilder.poolConfig(config);
        configurationBuilder.connectTimeout(Duration.ofMillis(connectTimeout));
        configurationBuilder.readTimeout(Duration.ofMillis(readTimeout));

        switch(mode)
        {
            //Simple
            default:
            case SERVER_MODE_SIMPLE:

                //Singleton connection
                currentAddress=addressArray[0].split(SPLIT_PORT)[0];
                currentPort=addressArray[0].split(SPLIT_PORT)[1];
                RedisStandaloneConfiguration standaloneConfiguration=
                        new RedisStandaloneConfiguration(
                                currentAddress,
                                Integer.parseInt(currentPort)
                        );
                standaloneConfiguration.setPassword(RedisPassword.of(password));


                //Create factory
                factory=new JedisConnectionFactory(standaloneConfiguration,configurationBuilder.build());
                logger.info("Redis connect success! The server mode:{}(host:{},port:{})",SERVER_MODE_SIMPLE,
                            currentAddress,currentPort
                );
                break;

            //Sentinel
            case SERVER_MODE_SENTINEL:

                //Master and slave connections
                RedisSentinelConfiguration sentinelConfiguration=
                        new RedisSentinelConfiguration(
                                masterName,
                                new LinkedHashSet<>(Arrays.asList(addressArray))
                        );
                sentinelConfiguration.setPassword(RedisPassword.of(password));

                //create factory
                factory=new JedisConnectionFactory(sentinelConfiguration,configurationBuilder.build());
                logger.info("Redis connect success! The server mode:{}({})",SERVER_MODE_SENTINEL,addressArray);
                break;

            //Cluster
            case SERVER_MODE_CLUSTER:

                //cluster connections
                RedisClusterConfiguration clusterConfiguration=
                        new RedisClusterConfiguration(
                                new LinkedHashSet<>(Arrays.asList(addressArray)));
                clusterConfiguration.setPassword(RedisPassword.of(password));

                //create factory
                factory=new JedisConnectionFactory(clusterConfiguration,configurationBuilder.build());
                logger.info("Redis connect success! The server mode:{}({})",SERVER_MODE_CLUSTER,addressArray);
                break;

        }

        return factory;
    }

    /**
     * Redis command executor
     * Key using String serialization; value using jdk serialization
     *
     * @param connectionFactory connection factory
     * @return command executor (value serialized using jdk)
     */
    @Bean("redisTemplate")
    public RedisTemplate<?,?> redisTemplate(
            @Qualifier("redisConnectionFactory") JedisConnectionFactory connectionFactory)
    {
        RedisTemplate<?,?> template=new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(enableTransactionSupport);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }

    /**
     * Redis command executor
     * Key using String serialization; value using String serialization
     *
     * @param connectionFactory connection factory
     * @return command executor (value string serialization)
     */
    @Bean("redisStringTemplate")
    public StringRedisTemplate redisStringTemplate(
            @Qualifier("redisConnectionFactory") JedisConnectionFactory connectionFactory)
    {
        StringRedisTemplate template=new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(enableTransactionSupport);
        return template;
    }

    /**
     * Redis command executor using stringRedisTemplate
     *
     * @param redisTemplate redis template
     * @return command executor (value serialized using jdk)
     */
    @Bean("redisExecutor")
    public RedisExecutor redisExecutor(
            @Value("${redis.store.prefix}") String keyPrefix,
            @Qualifier("redisTemplate") RedisTemplate redisTemplate)
    {
        RedisExecutor executor=new RedisExecutor.DefaultImpl();
        executor.setRedisTemplate(redisTemplate);
        executor.setPrefix(keyPrefix);
        return executor;
    }

    /**
     * Redis command executor
     * Key using String serialization; value using String serialization
     *
     * @param redisTemplate redis template
     * @return command executor (value string serialization)
     */
    @Bean("redisStringExecutor")
    public RedisExecutor redisStringExecutor(
            @Value("${redis.store.prefix}") String keyPrefix,
            @Qualifier("redisStringTemplate") StringRedisTemplate redisTemplate)
    {
        RedisExecutor executor=new RedisExecutor.JsonImpl();
        ((RedisExecutor.JsonImpl)executor).setStringRedisTemplate(redisTemplate);
        executor.setPrefix(keyPrefix);

        CustomJacksonObjectMapper objectMapper=new CustomJacksonObjectMapper();
        objectMapper.init();
        ((RedisExecutor.JsonImpl)executor).setObjectMapper(objectMapper);

        return executor;
    }

    /**
     * Redis Data Cache Manager
     *
     * @param connectionFactory connection factory
     * @return data cache manager
     */
    @Bean("redisCacheManager")
    public RedisCacheManager redisCacheManager(
            @Qualifier("redisConnectionFactory") JedisConnectionFactory connectionFactory)
    {
        if(!cacheEnable)
        {
            logger.info("The redis cache is not able!");
            return null;
        }

        //Generate configuration
        RedisCacheConfiguration configuration=RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(ttl==0?Duration.ZERO:Duration.ofMinutes(ttl))
                .prefixKeysWith(keyPrefix);
        if(!cacheNullValues) configuration=configuration.disableCachingNullValues();
        if(!usePrefix) configuration=configuration.disableKeyPrefix();

        //Create Cache Manager
        RedisCacheManager manager=RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(configuration)
                .build();
        manager.setTransactionAware(enableTransactionSupport);
        logger.info("The redis cache is enabled!");
        return manager;
    }

    /**
     * Custom subscription message receiver
     * Receive String JSON message content
     *  
     *
     * @param redisTemplate command executor
     * @return subscription message receiver
     */
    @Bean("redisMessageReceiver")
    public RedisMessageReceiver redisMessageReceiver(
            @Qualifier("redisStringTemplate") RedisTemplate redisTemplate)
    {
        if(!subscribeEnable) return null;
        return new RedisMessageReceiver(redisTemplate);
    }

    /**
     * Redis subscription message listener container
     *
     * @param connectionFactory    connection factory
     * @param redisMessageReceiver message sink
     * @return subscription message listener container
     */
    @Bean("redisMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("redisConnectionFactory") JedisConnectionFactory connectionFactory,
            @Qualifier("redisMessageReceiver") RedisMessageReceiver redisMessageReceiver)
    {
        if(!subscribeEnable)
        {
            logger.info("The redis subscribe is not able!");
            return null;
        }
        RedisMessageListenerContainer container=new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        //Add a unified custom message receiver
        String channelPattern=patternPrefix+"*";
        container.addMessageListener(redisMessageReceiver,new PatternTopic(channelPattern));
        logger.info("The redis subscribe is enabled,and the channel is - {}",channelPattern);

        return container;
    }
}