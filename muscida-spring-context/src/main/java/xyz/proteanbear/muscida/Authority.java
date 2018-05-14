package xyz.proteanbear.muscida;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.annotation.*;

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
     * An annotation on the method in the Controller；
     * Automatically call accountHandler to store account information
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface AutoStore{}

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
         * @return custom token generate method
         */
        default String customToken()
        {
            return null;
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
         * Store the account object
         *
         * @param account current account object
         * @return return the token string of account
         */
        String store(Account account);
    }
}