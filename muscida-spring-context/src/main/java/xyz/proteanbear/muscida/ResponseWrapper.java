package xyz.proteanbear.muscida;

/**
 * Return to the content wrapper,
 * wrap the returned data as a custom structure and return.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/10/26
 * @since jdk1.8
 */
public interface ResponseWrapper
{
    /**
     * Check if the specified object is a wrapped object.
     *
     * @param object Can not be empty
     * @return If empty, the default is true
     */
    default boolean isObjectAfterWrap(Object object)
    {
        return (object instanceof Response);
    }

    /**
     * wrap the returned data as a custom structure and return.
     *
     * @param message return description
     * @param data    Arbitrary object
     * @return Wrapped output object
     */
    default Object wrap(String message,Object data)
    {
        return new Response("SUCCESS",message,data);
    }

    /**
     * wrap the returned data as a custom structure and return.
     *
     * @param status  return status code
     * @param message return description
     * @return Wrapped output object
     */
    default Object wrap(String status,String message)
    {
        return new Response(status,message,null);
    }

    /**
     * Default content wrapper implementation.
     */
    class Default implements ResponseWrapper
    {
    }
}