package xyz.proteanbear.phecda.rest;

/**
 * 统一的数据返回结构
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/13
 * @since jdk1.8
 */
public class Response
{
    //状态码
    private String status;
    //信息描述
    private String message;
    //数据内容
    private Object data;

    public Response(String status,String message,Object data)
    {
        this.status=status;
        this.message=message;
        this.data=data;
    }

    public Response(){}

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status=status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message=message;
    }

    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data=data;
    }
}