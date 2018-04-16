package xyz.proteanbear.phecda.rest;

/**
 * 统一的数据返回结构
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/13
 * @since 1.0
 */
public class Response
{
    //状态码
    private String status;
    //信息描述
    private String message;
    //数据内容
    private Object data;

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