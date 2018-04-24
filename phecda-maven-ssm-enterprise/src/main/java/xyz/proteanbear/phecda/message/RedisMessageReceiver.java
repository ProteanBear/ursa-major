package xyz.proteanbear.phecda.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * <p>Redis订阅信息接收器。</p>
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/13
 * @since jdk1.8
 */
public class RedisMessageReceiver implements MessageListener
{
    //日志
    private static final Logger logger=LoggerFactory.getLogger(RedisMessageReceiver.class);

    /**
     * 当前的Redis命令执行器
     */
    private RedisTemplate redisTemplate;

    /**
     * 构造
     *
     * @param redisTemplate
     */
    public RedisMessageReceiver(RedisTemplate redisTemplate)
    {
        this.redisTemplate=redisTemplate;
    }

    /**
     * 消息处理
     *
     * @param message
     * @param bytes
     */
    @Override
    public void onMessage(Message message,byte[] bytes)
    {
        //消息解析
        Object data = redisTemplate.getKeySerializer()
                .deserialize(message.getBody());
        Object channel = redisTemplate.getKeySerializer()
                .deserialize(message.getChannel());
        logger.info("Receive the message from the channel({}),and the content is:{}",channel,data);


    }
}