package xyz.proteanbear.phecda;

/**
 * <p>系统异常<p/>
 * <p>异常中保存错误代码，对应response_message国际化属性文件中的错误描述<p/>
 * <p>并支持替换参数传递<p/>
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/16
 * @since jdk1.8
 */
public class PhecdaException extends Exception
{
    /**
     * 错误代码
     */
    private String code;

    /**
     * 错误参数
     */
    private Object[] params;

    /**
     * 构造
     *
     * @param other 异常
     */
    public PhecdaException(PhecdaException other)
    {
        super(other);
        this.setPhecdaException(other);
    }

    /**
     * 构造
     *
     * @param ex   异常
     * @param code 错误代码
     */
    public PhecdaException(Exception ex,String code)
    {
        super(ex);
        this.code=code;
        if(ex instanceof PhecdaException)
        {
            this.setPhecdaException((PhecdaException)ex);
        }
    }

    /**
     * 构造
     *
     * @param ex   异常
     * @param code 错误代码
     */
    public PhecdaException(Exception ex,String code,Object... params)
    {
        super(ex);
        this.code=code;
        this.params=params;
        if(ex instanceof PhecdaException)
        {
            this.setPhecdaException((PhecdaException)ex);
        }
    }

    /**
     * 构造
     *
     * @param code 错误代码
     */
    public PhecdaException(String code)
    {
        super(code,null);
        this.code=code;
    }

    /**
     * 构造
     *
     * @param cause 错误抛出堆栈
     * @param code  错误代码
     */
    public PhecdaException(Throwable cause,String code)
    {
        super(code,cause);
        this.code=code;
    }

    /**
     * 构造
     *
     * @param code   错误代码
     * @param params 错误参数
     */
    public PhecdaException(String code,Object... params)
    {
        super(code,null);
        this.code=code;
        this.params=params;
    }

    /**
     * 构造
     *
     * @param cause  错误抛出堆栈
     * @param code   错误代码
     * @param params 错误参数
     */
    public PhecdaException(Throwable cause,String code,Object... params)
    {
        super(code,cause);
        this.code=code;
        this.params=params;
    }

    /**
     * 构造
     *
     * @param message 错误信息
     * @param cause   错误抛出堆栈
     * @param code    错误代码
     * @param params  错误参数
     */
    public PhecdaException(String message,Throwable cause,String code,Object... params)
    {
        super(message,cause);
        this.code=code;
        this.params=params;
    }

    /**
     * 设置属性
     *
     * @param other 异常
     */
    public void setPhecdaException(PhecdaException other)
    {
        this.code=other.code;
        this.params=other.params;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code=code;
    }

    public Object[] getParams()
    {
        return params;
    }

    public void setParams(Object[] params)
    {
        this.params=params;
    }
}