package xyz.proteanbear.muscida;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * <p>Redis subscription information receiver</p>
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/13
 * @since jdk1.8
 */
public class RedisMessageReceiver implements MessageListener
{
    /**
     * Log
     */
    private static final Logger logger=LoggerFactory.getLogger(RedisMessageReceiver.class);

    /**
     * Current Redis Command Executor
     */
    private RedisTemplate redisTemplate;

    /**
     * Constructor
     *
     * @param redisTemplate redis command executor
     */
    public RedisMessageReceiver(RedisTemplate redisTemplate)
    {
        this.redisTemplate=redisTemplate;
    }

    /**
     * message handler
     *
     * @param message message object
     * @param bytes   message bytes
     */
    @Override
    public void onMessage(Message message,byte[] bytes)
    {
        //Use serializer deserialize the message content
        Object data=redisTemplate.getKeySerializer()
                .deserialize(message.getBody());
        Object channel=redisTemplate.getKeySerializer()
                .deserialize(message.getChannel());
        logger.info("Receive the message from the channel({}),and the content is:{}",channel,data);


    }
}