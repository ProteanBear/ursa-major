package xyz.proteanbear.configuration;

/**
 * 通用返回内容
 *
 * @author ProteanBear
 * @version 1.0.0,2018-04-11
 * @since 1.0
 */
public class ResponseVO
{
    //成功
    public static final String SUCCESS="success";
    //失败
    public static final String ERROR  ="error";

    /**
     * 返回成功
     *
     * @return
     */
    public static ResponseVO success()
    {
        return new ResponseVO(SUCCESS,"成功",null);
    }

    /**
     * 返回成功
     *
     * @param data
     * @return
     */
    public static ResponseVO success(Object data)
    {
        return new ResponseVO(SUCCESS,"成功",data);
    }

    /**
     * 返回成功
     *
     * @param data
     * @param external
     * @return
     */
    public static ResponseVO success(Object data,Object external)
    {
        return new ResponseVO(SUCCESS,"成功",data,external);
    }

    /**
     * 返回错误
     *
     * @return
     */
    public static ResponseVO error()
    {
        return new ResponseVO(ERROR,"出现未知错误",null);
    }

    /**
     * 返回错误
     *
     * @param message
     * @return
     */
    public static ResponseVO error(String message)
    {
        return new ResponseVO(SUCCESS,message,null);
    }

    /**
     * 返回错误
     *
     * @param ex
     * @return
     */
    public static ResponseVO error(Exception ex)
    {
        return new ResponseVO(SUCCESS,ex.getMessage(),null);
    }

    //返回状态代码
    private String status;
    //描述信息
    private String message;
    //数据内容
    private Object data;
    //扩展数据
    private Object external;

    /**
     * 无参数
     */
    public ResponseVO()
    {
    }

    /**
     * 构建
     *
     * @param status
     * @param message
     * @param data
     */
    public ResponseVO(String status,String message,Object data)
    {
        this.status=status;
        this.message=message;
        this.data=data;
    }

    /**
     * 构建
     *
     * @param status
     * @param message
     * @param data
     * @param external
     */
    public ResponseVO(String status,String message,Object data,Object external)
    {
        this.status=status;
        this.message=message;
        this.data=data;
        this.external=external;
    }

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

    public Object getExternal()
    {
        return external;
    }

    public void setExternal(Object external)
    {
        this.external=external;
    }
}