package xyz.proteanbear.phecda;

/**
 * 数据返回结构包装。
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/13
 * @since jdk1.8
 */
public class Response
{
    /**
     * 状态码
     */
    private String status;

    /**
     * 信息描述
     */
    private String message;

    /**
     * 数据内容
     */
    private Object data;

    /**
     * 构造函数
     *
     * @param status  状态码
     * @param message 信息描述
     * @param data    数据内容
     */
    public Response(String status,String message,Object data)
    {
        this.status=status;
        this.message=message;
        this.data=data;
    }

    /**
     * 构造函数
     */
    public Response()
    {
    }

    /**
     * @return 状态码
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @param status 状态码
     */
    public void setStatus(String status)
    {
        this.status=status;
    }

    /**
     * @return 信息描述
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message 信息描述
     */
    public void setMessage(String message)
    {
        this.message=message;
    }

    /**
     * @return 数据内容
     */
    public Object getData()
    {
        return data;
    }

    /**
     * @param data 数据内容
     */
    public void setData(Object data)
    {
        this.data=data;
    }
}