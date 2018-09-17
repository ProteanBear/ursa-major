package xyz.proteanbear.muscida;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Message related processing
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/09
 * @since jdk1.8
 */
public class Message
{
    /**
     * Message source type such as Redis/WebSocket etc.
     */
    enum SourceType
    {
        RedisSubscribe,WebSocket,RPC,gRPC,MessageQuery
    }

    /**
     * Annotation for message receiver
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Component
    public @interface Receiver
    {
        /**
         * @return message source type array
         */
        @AliasFor("sourceType")
        SourceType[] value();

        /**
         * @return message source type array
         */
        SourceType[] sourceType();

        /**
         * @return Message labels are used to distinguish different messages
         */
        String[] tags();

        /**
         * @return handle methods when receiving the message.
         */
        String[] methods() default "onReceive";
    }


}