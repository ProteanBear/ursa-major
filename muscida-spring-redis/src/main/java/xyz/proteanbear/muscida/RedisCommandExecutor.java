package xyz.proteanbear.muscida;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis command executor
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/10
 * @since jdk1.8
 */
public interface RedisCommandExecutor
{
    /**
     * @return redis template
     */
    RedisTemplate getRedisTemplate();

    /**
     * @param redisTemplate redis template
     */
    void setRedisTemplate(RedisTemplate redisTemplate);

    /**
     * @return key prefix for store
     */
    String getPrefix();

    /**
     * @param prefix key prefix for store
     */
    void setPrefix(String prefix);

    /**
     * serialize the value to byte array
     *
     * @param value        object
     * @param bySerializer redis serializer
     * @return byte array
     */
    byte[] serializeValue(Object value,RedisSerializer bySerializer);

    /**
     * deserialize the value byte array
     *
     * @param value        must not be empty.
     * @param toClass      return class
     * @param <T>          return class
     * @param bySerializer redis serializer
     * @return class object
     */
    <T> T deserializeValue(byte[] value,Class<T> toClass,RedisSerializer bySerializer);

    /**
     * For string command
     */
    interface StringCommand extends RedisCommandExecutor
    {
        /**
         * Get the corresponding value of key and specify the returned object class.
         *
         * @param key     key
         * @param toClass return class
         * @param <T>     return class
         * @return the corresponding value of key
         */
        default <T> T get(String key,Class<T> toClass)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key);

            RedisSerializer keySerializer=redisTemplate.getKeySerializer();
            RedisSerializer valueSerializer=redisTemplate.getValueSerializer();
            return (T)redisTemplate.execute(
                    (RedisCallback<T>)connection -> deserializeValue(
                            connection.get(keySerializer.serialize(getPrefix()+key)),toClass,valueSerializer)
            );
        }

        /**
         * Get multiple keys. Values are returned in the order of the requested keys.
         *
         * @param keys    must not be null.
         * @param toClass return class
         * @return empty List if keys do not exist or when used in pipeline / transaction.
         */
        default <T> List<T> get(Class<T> toClass,String... keys)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=keys);

            RedisSerializer keySerializer=redisTemplate.getKeySerializer();
            RedisSerializer valueSerializer=redisTemplate.getValueSerializer();

            byte[][] byteKeys=new byte[keys.length][];
            for(int i=0;i<keys.length;i++)
            {
                byteKeys[i]=keySerializer.serialize(getPrefix()+keys[i]);
            }

            return (List<T>)redisTemplate.execute(
                    (RedisCallback<List<T>>)connection -> {
                        List<T> result=new ArrayList<>();
                        List<byte[]> byteResult=connection.mGet(byteKeys);
                        byteResult.forEach((byteValue) -> {
                            result.add(deserializeValue(byteValue,toClass,valueSerializer));
                        });
                        return result;
                    }
            );
        }

        /**
         * Get multiple keys matching the given pattern.
         *
         * @param pattern must not be null.
         * @param toClass return class
         * @return empty List if keys do not exist or when used in pipeline / transaction.
         */
        default <T> List<T> getByKeys(String pattern,Class<T> toClass)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=pattern);

            RedisSerializer keySerializer=redisTemplate.getKeySerializer();
            RedisSerializer valueSerializer=redisTemplate.getValueSerializer();

            return (List<T>)redisTemplate.execute(
                    (RedisCallback<List<T>>)connection -> {
                        List<T> result=new ArrayList<>();
                        Set<byte[]> byteKeys=connection.keys(keySerializer.serialize(getPrefix()+pattern));
                        List<byte[]> byteResult=connection.mGet(byteKeys.toArray(new byte[][]{}));
                        byteResult.forEach((byteValue) -> {
                            result.add(deserializeValue(byteValue,toClass,valueSerializer));
                        });
                        return result;
                    }
            );
        }

        /**
         * Get the corresponding string value of key
         *
         * @param key key
         * @return the corresponding string value of key
         */
        default String getString(String key)
        {
            return get(key,String.class);
        }

        /**
         * Set the value of key(default SetOption is EX;not expired)
         *
         * @param key   key
         * @param value value object
         */
        default boolean set(String key,Object value)
        {
            return set(key,value,-1,null,RedisStringCommands.SetOption.ifPresent());
        }

        /**
         * Set the value of key(not expired)
         *
         * @param key       key
         * @param value     value object
         * @param setOption includes SET_IF_ABSENT(NX),SET_IF_PRESENT(EX),UPSERT(NO SET)
         */
        default boolean set(String key,Object value,RedisStringCommands.SetOption setOption)
        {
            return set(key,value,-1,null,setOption);
        }

        /**
         * Set the value of key(default SetOption is EX)
         *
         * @param key        key
         * @param value      value object
         * @param expireTime expire time
         * @param timeUnit   expire time's unit
         */
        default boolean set(String key,Object value,long expireTime,TimeUnit timeUnit)
        {
            return set(key,value,expireTime,timeUnit,RedisStringCommands.SetOption.UPSERT);
        }

        /**
         * Set the value of key
         *
         * @param key        key
         * @param value      value object
         * @param expireTime expire time
         * @param timeUnit   expire time's unit
         */
        default boolean set(String key,Object value,long expireTime,TimeUnit timeUnit,
                            RedisStringCommands.SetOption setOption)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key) && (null!=value);
            return (boolean)redisTemplate.execute(
                    (RedisCallback<Boolean>)connection -> connection.set(
                            redisTemplate.getKeySerializer().serialize(getPrefix()+key),
                            serializeValue(value,redisTemplate.getValueSerializer()),
                            expireTime==-1
                                    ?Expiration.persistent()
                                    :Expiration.from(expireTime,timeUnit),
                            setOption
                    ));
        }

        /**
         * Increment an integer value stored as string value of key by 1.
         *
         * @param key must not be null
         * @return null when used in pipeline / transaction.
         */
        default long increment(String key)
        {
            return incrementBy(key,1);
        }

        /**
         * Increment an integer value stored of key by delta.
         *
         * @param key       must not be null
         * @param increment increment number
         * @return null when used in pipeline / transaction.
         */
        default long incrementBy(String key,long increment)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key);
            return (long)redisTemplate.execute(
                    (RedisCallback<Long>)connection -> connection.incrBy(
                            redisTemplate.getKeySerializer().serialize(getPrefix()+key),increment)
            );
        }

        /**
         * Decrement an integer value stored as string value of key by 1.
         *
         * @param key must not be null
         * @return null when used in pipeline / transaction.
         */
        default long decrement(String key)
        {
            return decrementBy(key,1);
        }

        /**
         * Decrement an integer value stored as string value of key by value.
         *
         * @param key       must not be null
         * @param decrement decrement number
         * @return null when used in pipeline / transaction.
         */
        default long decrementBy(String key,long decrement)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key);
            return (long)redisTemplate.execute(
                    (RedisCallback<Long>)connection -> connection.decrBy(
                            redisTemplate.getKeySerializer().serialize(getPrefix()+key),decrement)
            );
        }
    }

    /**
     * For key command
     */
    interface KeyCommand extends RedisCommandExecutor
    {
        /**
         * Delete given keys.
         *
         * @param keys must not be null
         * @return The number of keys that were removed. null when used in pipeline / transaction.
         */
        default long delete(String... keys)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=keys);

            byte[][] byteKeys=new byte[keys.length][];
            RedisSerializer keySerializer=redisTemplate.getKeySerializer();
            for(int i=0;i<keys.length;i++)
            {
                byteKeys[i]=keySerializer.serialize(getPrefix()+keys[i]);
            }

            return (long)redisTemplate.execute(
                    (RedisCallback<Long>)connection -> connection.del(byteKeys)
            );
        }

        /**
         * Find all keys matching the given pattern and delete it
         *
         * @param pattern must not be null
         * @return The number of keys that were removed. null when used in pipeline / transaction.
         */
        default long deleteByKeys(String pattern)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=pattern);
            RedisSerializer keySerializer=redisTemplate.getKeySerializer();

            return (long)redisTemplate.execute(
                    (RedisCallback<Long>)connection ->
                            connection.del(
                                    connection.keys(keySerializer.serialize(getPrefix()+pattern))
                                            .toArray(new byte[][]{}))
            );
        }

        /**
         * Determine if given key exists.
         *
         * @param key must not be null
         * @return true if key exists. null when used in pipeline / transaction.
         */
        default boolean exists(String key)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key);
            return (boolean)redisTemplate.execute(
                    (RedisCallback<Boolean>)connection ->
                            connection.exists(
                                    redisTemplate.getKeySerializer().serialize(key)
                            )
            );
        }

        /**
         * Set time to live for given key in seconds.
         *
         * @param key     must not be null
         * @param seconds expire time in seconds
         * @return null when used in pipeline / transaction.
         */
        default boolean expire(String key,long seconds)
        {
            return expire(key,seconds,TimeUnit.SECONDS);
        }

        /**
         * Set time to live for given key.
         *
         * @param key        must not be null
         * @param expireTime expire time
         * @param timeUnit   time unit
         * @return null when used in pipeline / transaction.
         */
        default boolean expire(String key,long expireTime,TimeUnit timeUnit)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key);
            Expiration expiration=Expiration.from(expireTime,timeUnit);
            return (boolean)redisTemplate.execute(
                    (RedisCallback<Boolean>)connection ->
                            connection.pExpire(
                                    redisTemplate.getKeySerializer().serialize(key),
                                    expiration.getExpirationTimeInMilliseconds()
                            )
            );
        }

        /**
         * Find all keys matching the given pattern.
         *
         * @param pattern must not be null
         * @return empty Set if no match found. null when used in pipeline / transaction.
         */
        default Set<String> keys(String pattern)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=pattern);
            RedisSerializer keySerializer=redisTemplate.getKeySerializer();

            return (Set<String>)redisTemplate.execute(
                    (RedisCallback<Set<String>>)connection -> {
                        Set<String> result=new LinkedHashSet<>();
                        connection.keys(keySerializer.serialize(getPrefix()+pattern))
                                .forEach(key -> {
                                    result.add(keySerializer.deserialize(key).toString().replace(getPrefix(),""));
                                });
                        return result;
                    }
            );
        }

        /**
         * Remove the expiration from given key.
         *
         * @param key must not be null
         * @return null when used in pipeline / transaction.
         */
        default boolean persist(String key)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key);
            return (boolean)redisTemplate.execute(
                    (RedisCallback<Boolean>)connection ->
                            connection.persist(
                                    redisTemplate.getKeySerializer().serialize(key)
                            )
            );
        }

        /**
         * Get the time to live for key in seconds.
         *
         * @param key must not be null
         * @return null when used in pipeline / transaction.
         */
        default long ttl(String key)
        {
            return ttl(key,TimeUnit.SECONDS);
        }

        /**
         * Get the time to live for key in and convert it to the given TimeUnit.
         *
         * @param key      must not be null
         * @param timeUnit must not be null.
         * @return null when used in pipeline / transaction.
         */
        default long ttl(String key,TimeUnit timeUnit)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key && null!=timeUnit);
            return (long)redisTemplate.execute(
                    (RedisCallback<Long>)connection ->
                            connection.ttl(
                                    redisTemplate.getKeySerializer().serialize(key),
                                    timeUnit
                            )
            );
        }
    }

    /**
     * For map command
     */
    interface MapCommand extends RedisCommandExecutor
    {
        /**
         * Get entire hash stored at key.
         *
         * @param key     must not be null.
         * @param toClass return class
         * @return empty Map if key does not exist or null when used in pipeline / transaction.
         */
        default <T> Map<String,T> mapGet(String key,Class<T> toClass)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key && null!=toClass);

            RedisSerializer keySerializer=redisTemplate.getKeySerializer();
            RedisSerializer hashKeySerializer=redisTemplate.getHashKeySerializer();
            RedisSerializer hashValueSerializer=redisTemplate.getHashValueSerializer();

            return (Map<String,T>)redisTemplate.execute(
                    (RedisCallback<Map<String,T>>)(connection) -> {
                        Map<String,T> result=new HashMap<>();
                        Map<byte[],byte[]> byteResult=connection
                                .hGetAll(keySerializer.serialize(getPrefix()+key));
                        byteResult.forEach((field,value) -> {
                            result.put(
                                    hashKeySerializer.deserialize(field).toString(),
                                    deserializeValue(value,toClass,hashValueSerializer)
                            );
                        });
                        return result;
                    }
            );
        }

        /**
         * Get value for given field from hash at key.
         *
         * @param key     must not be null.
         * @param field   must not be null.
         * @param toClass return class
         * @param <T>     return class
         * @return null when key or field do not exists or when used in pipeline / transaction.
         */
        default <T> T mapGet(String key,String field,Class<T> toClass)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key && null!=field);

            RedisSerializer keySerializer=redisTemplate.getKeySerializer();
            RedisSerializer hashKeySerializer=redisTemplate.getHashKeySerializer();
            RedisSerializer hashValueSerializer=redisTemplate.getHashValueSerializer();

            return (T)redisTemplate.execute(
                    (RedisCallback<T>)(connection) ->
                            deserializeValue(
                                    connection.hGet(
                                            keySerializer.serialize(getPrefix()+key),
                                            hashKeySerializer.serialize(field)
                                    ),
                                    toClass,
                                    hashValueSerializer
                            )
            );
        }

        /**
         * Delete given hash fields.
         *
         * @param key    must not be null.
         * @param fields must not be empty.
         * @return null when used in pipeline / transaction.
         */
        default long mapDelete(String key,String... fields)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key && null!=fields);

            RedisSerializer keySerializer=redisTemplate.getKeySerializer();
            RedisSerializer hashKeySerializer=redisTemplate.getHashKeySerializer();

            byte[][] byteFields=new byte[fields.length][];
            for(int i=0;i<fields.length;i++)
            {
                byteFields[i]=hashKeySerializer.serialize(fields[i]);
            }

            return (long)redisTemplate.execute(
                    (RedisCallback<Long>)(connection) ->
                            connection.hDel(
                                    keySerializer.serialize(key),byteFields)
            );
        }

        /**
         * Determine if given hash field exists.
         *
         * @param key   must not be null.
         * @param field must not be null.
         * @return null when used in pipeline / transaction.
         */
        default boolean mapExists(String key,String field)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key && null!=field);

            RedisSerializer keySerializer=redisTemplate.getKeySerializer();
            RedisSerializer hashKeySerializer=redisTemplate.getHashKeySerializer();

            return (boolean)redisTemplate.execute(
                    (RedisCallback<Boolean>)(connection) ->
                            connection.hExists(
                                    keySerializer.serialize(getPrefix()+key),
                                    hashKeySerializer.serialize(field)
                            )
            );
        }

        /**
         * Set multiple hash fields to multiple values using data provided in hashes
         *
         * @param key   must not be null.
         * @param field must not be null.
         * @param value must not be null.
         */
        default void mapSet(String key,String field,Object value)
        {
            mapSet(key,new HashMap<String,Object>()
            {{
                put(key,value);
            }});
        }

        /**
         * Set multiple hash fields to multiple values using data provided in hashes
         *
         * @param key           must not be null.
         * @param fieldAndValue must not be null.
         */
        default void mapSet(String key,Object... fieldAndValue)
        {
            Map<String,Object> fieldMap=new HashMap<>();
            for(int i=0;i<fieldAndValue.length/2;i++)
            {
                fieldMap.put(fieldAndValue[i].toString(),fieldAndValue[i+1]);
            }
            mapSet(key,fieldAndValue);
        }

        /**
         * Set multiple hash fields to multiple values using data provided in hashes
         *
         * @param key    must not be null.
         * @param fields must not be null.
         */
        default void mapSet(String key,Map<String,Object> fields)
        {
            RedisTemplate redisTemplate=getRedisTemplate();
            assert null!=redisTemplate;
            assert (null!=key && null!=fields);

            RedisSerializer keySerializer=redisTemplate.getKeySerializer();
            RedisSerializer hashKeySerializer=redisTemplate.getHashKeySerializer();
            RedisSerializer hashValueSerializer=redisTemplate.getHashValueSerializer();

            Map<byte[],byte[]> fieldMap=new HashMap<>(fields.size());
            fields.forEach((field,value) -> {
                fieldMap.put(
                        hashKeySerializer.serialize(field),
                        serializeValue(value,hashValueSerializer)
                );
            });

            redisTemplate.execute(
                    (RedisCallback<Boolean>)connection -> {
                        connection.hMSet(
                                keySerializer.serialize(getPrefix()+key),
                                fieldMap
                        );
                        return Boolean.TRUE;
                    }
            );
        }
    }

    /**
     * For list command
     */
    interface ListCommand extends RedisCommandExecutor
    {
        long listAdd(String key,Object... values);
    }
}