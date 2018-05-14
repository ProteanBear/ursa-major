package xyz.proteanbear.muscida;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis command executor
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/10
 * @since jdk1.8
 */
public interface RedisExecutor extends RedisCommandExecutor.StringCommand,
        RedisCommandExecutor.KeyCommand,
        RedisCommandExecutor.MapCommand
{
    /**
     * Redis command executor default implement.
     * Use JdkSerializationRedisSerializer deserialize value.
     */
   class DefaultImpl implements RedisExecutor
    {
        /**
         * redis template
         */
        private RedisTemplate redisTemplate;

        /**
         * redis key prefix
         */
        private String prefix;

        /**
         * @return redis template
         */
        @Override
        public RedisTemplate getRedisTemplate()
        {
            return redisTemplate;
        }

        /**
         * @param redisTemplate redis template
         */
        public void setRedisTemplate(RedisTemplate redisTemplate)
        {
            this.redisTemplate=redisTemplate;
        }

        /**
         * @return redis key prefix
         */
        @Override
        public String getPrefix()
        {
            return prefix;
        }

        /**
         * @param prefix key prefix for store
         */
        @Override
        public void setPrefix(String prefix)
        {
            this.prefix=prefix;
        }

        /**
         * deserialize the value byte array
         *
         * @param value        must not be empty.
         * @param toClass      return class
         * @param <T>          return class
         * @param bySerializer redis serializer
         * @return class object
         */
        @Override
        public <T> T deserializeValue(byte[] value,Class<T> toClass,RedisSerializer bySerializer)
        {
            if(value==null) return null;
            return (T)bySerializer.deserialize(value);
        }
    }

    /**
     * Redis command executor with string redis template,and deserialize the value using json convert.
     */
    class JsonImpl extends DefaultImpl
    {
        /**
         * Jackson convert
         */
        private ObjectMapper objectMapper;

        /**
         * @param redisTemplate string redis template
         */
        public void setStringRedisTemplate(StringRedisTemplate redisTemplate)
        {
            super.setRedisTemplate(redisTemplate);
        }

        /**
         * @param objectMapper Jackson convert
         */
        public void setObjectMapper(ObjectMapper objectMapper)
        {
            this.objectMapper=objectMapper;
        }

        /**
         * deserialize the value byte array
         *
         * @param value        must not be empty.
         * @param toClass      return class
         * @param <T>          return class
         * @param bySerializer redis serializer
         * @return class object
         */
        @Override
        public <T> T deserializeValue(byte[] value,Class<T> toClass,RedisSerializer bySerializer)
        {
            if(value==null) return null;
            Object object=bySerializer.deserialize(value);
            if(object==null) return null;

            try
            {
                return objectMapper.readValue(object.toString(),toClass);
            }
            catch(IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }
}