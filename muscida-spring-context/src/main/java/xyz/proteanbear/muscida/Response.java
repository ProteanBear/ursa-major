package xyz.proteanbear.muscida;

/**
 * Data return structure packaging.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/13
 * @since jdk1.8
 */
public class Response
{
    /**
     * Status code
     */
    private String status;

    /**
     * return message
     */
    private String message;

    /**
     * data content
     */
    private Object data;

    /**
     * Constructor
     *
     * @param status  status code
     * @param message return message
     * @param data    data content
     */
    public Response(String status,String message,Object data)
    {
        this.status=status;
        this.message=message;
        this.data=data;
    }

    /**
     * constructor
     */
    public Response()
    {
    }

    /**
     * @return status code
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @param status status code
     */
    public void setStatus(String status)
    {
        this.status=status;
    }

    /**
     * @return return message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message return message
     */
    public void setMessage(String message)
    {
        this.message=message;
    }

    /**
     * @return data content
     */
    public Object getData()
    {
        return data;
    }

    /**
     * @param data data content
     */
    public void setData(Object data)
    {
        this.data=data;
    }
}