package xyz.proteanbear.phecda.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import xyz.proteanbear.phecda.message.RedisMessageReceiver;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * <p>Spring与Redis集成配置。</p>
 * <p>使用Configuration式配置便于区分单机和集群配置。</p>
 * <p>支持使用Redis缓存数据。</p>
 * <p>开通订阅服务。</p>
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/12
 * @since jdk1.8
 */
@Configuration
@PropertySource("classpath:redis.properties")
public class SpringRedisConfiguration
{
    //日志
    private static Logger logger=LoggerFactory.getLogger(SpringRedisConfiguration.class);

    //Redis服务模式-单机
    private static final String SERVER_MODE_SIMPLE="simple";
    //Redis服务模式-主备
    private static final String SERVER_MODE_SENTINEL="sentinel";
    //Redis服务模式-集群
    private static final String SERVER_MODE_CLUSTER="cluster";
    //多服务器分隔符
    private static final String SPLIT_SERVER=",";
    //服务与端口分隔符
    private static final String SPLIT_PORT=":";

    /**
     * 指定Redis模式，可设置为simple/cluster
     */
    @Value("${redis.server.mode}")
    private String mode;

    /**
     * 连接超时时间，单位为毫秒
     */
    @Value("${redis.server.connectTimeout}")
    private Long connectTimeout;

    /**
     * 读取超时时间，单位为毫秒
     */
    @Value("${redis.server.readTimeout}")
    private Long readTimeout;

    /**
     * 服务器地址和端口，集群模式使用逗号分隔多个server;
     * mode设置为simple，则即使设置多个server，也会只取第一个
     */
    @Value("${redis.server.address}")
    private String address;

    /**
     * 访问密码
     */
    @Value("${redis.server.password}")
    private String password;

    /**
     * 是否使用连接池
     */
    @Value("${redis.server.isUsePool}")
    private boolean isUsePool;

    /**
     * 是否使用SSL连接
     */
    @Value("${redis.server.isUseSSL}")
    private boolean isUseSSL;

    /**
     * 主备模式下指定
     * 这个值要和Sentinel中指定的master的值一致，不然启动时找不到Sentinel会报错的
     */
    @Value("${redis.server.masterName}")
    private String masterName;

    /**
     * 是否开启事务处理
     */
    @Value("${redis.execute.enableTransactionSupport}")
    private boolean enableTransactionSupport;

    /**
     * 最大连接数：能够同时建立的“最大链接个数”
     */
    @Value("${redis.pool.maxTotal}")
    private Integer maxTotal;

    /**
     * 最大空闲数：空闲链接数大于maxIdle时，将进行回收
     */
    @Value("${redis.pool.maxIdle}")
    private Integer maxIdle;

    /**
     * 最小空闲数：低于minIdle时，将创建新的链接
     */
    @Value("${redis.pool.minIdle}")
    private Integer minIdle;

    /**
     * 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),
     * 如果超时就抛异常,小于零:阻塞不确定的时间,默认-1
     */
    @Value("${redis.pool.maxWaitMillis}")
    private Long maxWaitMillis;

    /**
     * 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
     */
    @Value("${redis.pool.minEvictableIdleTimeMillis}")
    private Long minEvictableIdleTimeMillis;

    /**
     * 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
     */
    @Value("${redis.pool.numTestsPerEvictionRun}")
    private Integer numTestsPerEvictionRun;

    /**
     * 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,
     * 不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
     */
    @Value("${redis.pool.softMinEvictableIdleTimeMillis}")
    private Long softMinEvictableIdleTimeMillis;

    /**
     * 最大连接数：能够同时建立的“最大链接个数”
     */
    @Value("${redis.pool.testOnCreate}")
    private boolean testOnCreate;

    /**
     * 在获取连接的时候检查有效性
     */
    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;

    /**
     * 在返回连接的时候检查有效性
     */
    @Value("${redis.pool.testOnReturn}")
    private boolean testOnReturn;

    /**
     * 在空闲的时候检查有效性
     */
    @Value("${redis.pool.testWhileIdle}")
    private boolean testWhileIdle;

    /**
     * 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
     */
    @Value("${redis.pool.timeBetweenEvictionRunsMillis}")
    private Long timeBetweenEvictionRunsMillis;

    /**
     * 连接耗尽时是否阻塞, false报异常,true阻塞直到超时, 默认true
     */
    @Value("${redis.pool.blockWhenExhausted}")
    private boolean blockWhenExhausted=true;

    /**
     * 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
     */
    @Value("${redis.pool.evictionPolicyClassName}")
    private String evictionPolicyClassName="org.apache.commons.pool2.impl.DefaultEvictionPolicy";

    /**
     * 是否启用pool的jmx管理功能, 默认true
     */
    @Value("${redis.pool.jmxEnabled}")
    private boolean jmxEnabled=true;

    /**
     *
     */
    @Value("${redis.pool.jmxNamePrefix}")
    private String jmxNamePrefix="pool";

    /**
     * 是否启用后进先出, 默认true
     */
    @Value("${redis.pool.lifo}")
    private boolean lifo=true;

    /**
     * 是否启用后进先出, 默认true
     */
    @Value("${redis.cache.enable}")
    private boolean cacheEnable;

    /**
     * 缓存超时时间，单位为分钟，0为不超时
     */
    @Value("${redis.cache.ttl}")
    private Long ttl;

    /**
     * 是否保存空（null）内容
     */
    @Value("${redis.cache.cacheNullValues}")
    private boolean cacheNullValues;

    /**
     * 是否使用键名前缀
     */
    @Value("${redis.cache.usePrefix}")
    private boolean usePrefix;

    /**
     * 指定使用的键名前缀
     */
    @Value("${redis.cache.keyPrefix}")
    private String keyPrefix;

    /**
     * 是否开启消息订阅
     */
    @Value("${redis.subscribe.enable}")
    private boolean subscribeEnable;

    /**
     * 订阅的频道名称前缀
     * 统一使用模式订阅，使用*作为通配符；这里不需要使用*，创建时会自动添加
     * 发送时使用此前缀+具体子频道名称
     */
    @Value("${redis.subscribe.patternPrefix}")
    private String patternPrefix;

    /**
     * Redis连接池配置
     *
     * @return
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
     * 创建连接工厂
     *
     * @return
     */
    @Bean("redisConnectionFactory")
    public JedisConnectionFactory connectionFactory(
            @Qualifier("redisPoolConfig") JedisPoolConfig config)
    {
        JedisConnectionFactory factory=null;

        //获取服务地址
        if(StringUtils.isEmpty(address))
        {
            logger.error("Null redis server address!");
            return factory;
        }
        String[] addressArray=address.split(SPLIT_SERVER);
        String currentAddress, currentPort;

        //连接配置
        JedisClientConfiguration.DefaultJedisClientConfigurationBuilder configurationBuilder=(JedisClientConfiguration.DefaultJedisClientConfigurationBuilder)JedisClientConfiguration
                .builder();
        if(isUsePool) configurationBuilder.usePooling();
        if(isUseSSL)
        {
            configurationBuilder.useSsl();
            //设置SSL
        }
        configurationBuilder.poolConfig(config);
        configurationBuilder.connectTimeout(Duration.ofMillis(connectTimeout));
        configurationBuilder.readTimeout(Duration.ofMillis(readTimeout));

        switch(mode)
        {
            //单机模式
            default:
            case SERVER_MODE_SIMPLE:

                //单例连接
                currentAddress=addressArray[0].split(SPLIT_PORT)[0];
                currentPort=addressArray[0].split(SPLIT_PORT)[1];
                RedisStandaloneConfiguration standaloneConfiguration=
                        new RedisStandaloneConfiguration(
                                currentAddress,
                                Integer.parseInt(currentPort)
                        );
                standaloneConfiguration.setPassword(RedisPassword.of(password));


                //创建工厂
                factory=new JedisConnectionFactory(standaloneConfiguration,configurationBuilder.build());
                logger.info("Redis connect success! The server mode:{}(host:{},port:{})",SERVER_MODE_SIMPLE,
                            currentAddress,currentPort
                );
                break;

            //主备模式
            case SERVER_MODE_SENTINEL:

                //主备连接
                RedisSentinelConfiguration sentinelConfiguration=
                        new RedisSentinelConfiguration(
                                masterName,
                                new LinkedHashSet<>(Arrays.asList(addressArray))
                        );
                sentinelConfiguration.setPassword(RedisPassword.of(password));

                //创建工厂
                factory=new JedisConnectionFactory(sentinelConfiguration,configurationBuilder.build());
                logger.info("Redis connect success! The server mode:{}({})",SERVER_MODE_SENTINEL,addressArray);
                break;

            //集群模式
            case SERVER_MODE_CLUSTER:

                //集群连接
                RedisClusterConfiguration clusterConfiguration=
                        new RedisClusterConfiguration(
                                new LinkedHashSet<>(Arrays.asList(addressArray)));
                clusterConfiguration.setPassword(RedisPassword.of(password));

                //创建工厂
                factory=new JedisConnectionFactory(clusterConfiguration,configurationBuilder.build());
                logger.info("Redis connect success! The server mode:{}({})",SERVER_MODE_CLUSTER,addressArray);
                break;

        }

        return factory;
    }

    /**
     * Redis命令执行器
     * key使用String序列化；value使用jdk序列化
     *
     * @param connectionFactory
     * @return
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
     * Redis命令执行器
     * key使用String序列化；value使用String序列化
     *
     * @param connectionFactory
     * @return
     */
    @Bean("redisStringTemplate")
    public RedisTemplate<?,?> redisStringTemplate(
            @Qualifier("redisConnectionFactory") JedisConnectionFactory connectionFactory)
    {
        RedisTemplate<?,?> template=new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(enableTransactionSupport);
        return template;
    }

    /**
     * Redis数据缓存管理器
     *
     * @param connectionFactory
     * @return
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

        //生成配置
        RedisCacheConfiguration configuration=RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(ttl.longValue()==0?Duration.ZERO:Duration.ofMinutes(ttl))
                .prefixKeysWith(keyPrefix);
        if(!cacheNullValues) configuration=configuration.disableCachingNullValues();
        if(!usePrefix) configuration=configuration.disableKeyPrefix();

        //创建缓存管理器
        RedisCacheManager manager=RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(configuration)
                .build();
        manager.setTransactionAware(enableTransactionSupport);
        logger.info("The redis cache is enabled!");
        return manager;
    }

    /**
     * 自定义的订阅消息接收器
     * 接收String的JSON格式消息内容
     *
     * @param redisTemplate
     * @return
     */
    @Bean("redisMessageReceiver")
    public RedisMessageReceiver redisMessageReceiver(
            @Qualifier("redisStringTemplate") RedisTemplate redisTemplate)
    {
        if(!subscribeEnable) return null;
        return new RedisMessageReceiver(redisTemplate);
    }

    /**
     * Redis订阅消息监听器容器
     *
     * @param connectionFactory
     * @return
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

        //添加统一的自定义消息接收器
        String channelPattern=patternPrefix+"*";
        container.addMessageListener(redisMessageReceiver, new PatternTopic(channelPattern));
        logger.info("The redis subscribe is enabled,and the channel is - {}",channelPattern);

        return container;
    }
}