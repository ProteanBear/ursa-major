package xyz.proteanbear.muscida;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.lang.annotation.*;
import java.util.UUID;

/**
 * User's functional authority processing, including annotations and interfaces;
 * User Bean for login needs to implement interface.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/09
 * @since jdk1.8
 */
public class Authority
{
    /**
     * An annotation on the method in the Controller, indicating the login user rights required for the call
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Set
    {
        /**
         * @return Error message
         */
        String message() default "{NOT_LOGIN_ACCESS}";

        /**
         * @return Whether you must log in, the default return true
         */
        boolean mustLogin() default true;

        /**
         * @return If set to true, only the class specified by accountClass or accountClassName is allowed to access the interface;If set to false, the class specified by accountClass or accountClassName is not allowed to access the interface;
         */
        boolean allow() default false;

        /**
         * @return If this is a interface for user login,you can set it true and return user object will be stored automatic.
         */
        boolean autoStore() default false;

        /**
         * @return If this is a interface for user logout,you can set it true and remove the user object stored automatic.
         */
        boolean autoRemove() default false;

        /**
         * @return Related account class array
         */
        Class[] accountClass() default {};

        /**
         * @return Related account class name array
         */
        String[] accountClassName() default {};

        /**
         * @return Sets detailed data for custom authentication to use this interface
         */
        String customData() default "";
    }

    /**
     * Account interface and extend from Serializable
     */
    public interface Account extends Serializable
    {
        /**
         * Check the custom data
         *
         * @param customData custom data from method annotation
         * @return whether this interface method can be used
         */
        default boolean ok(String customData)
        {
            return true;
        }

        /**
         * @return Custom token generate method.Default token is uuid
         */
        default String customToken()
        {
            return String.valueOf(UUID.randomUUID()).replaceAll("-","");
        }

        /**
         * @param toClass class
         * @param <T>     class
         * @return bean object
         */
        default <T> T getBean(Class<T> toClass)
        {
            return this.getClass().isAssignableFrom(toClass)?((T)this):null;
        }
    }

    /**
     * Account Handler
     */
    public interface AccountHandler
    {
        /**
         * Get the account object through web request
         *
         * @param request web request
         * @return current account object
         */
        Account get(HttpServletRequest request);

        /**
         * Get the account object through token string
         *
         * @param fromToken token string
         * @param forClass  class name
         * @return current account object
         */
        Account get(String fromToken,String forClass);

        /**
         * @param request web request
         * @return token string
         */
        String token(HttpServletRequest request);

        /**
         * @param request web request
         * @return account object type string
         */
        String type(HttpServletRequest request);

        /**
         * Store the account object
         *
         * @param response response
         * @param account  current account object
         */
        void store(HttpServletResponse response,Account account);

        /**
         * Remove the account object
         *
         * @param request  web request
         * @param response response
         */
        void remove(HttpServletRequest request,HttpServletResponse response);
    }
}