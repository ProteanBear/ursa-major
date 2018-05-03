package xyz.proteanbear.muscida;

/**
 * <p>System exception.<p/>
 * <p>The error code is saved in the exception, corresponding to the error description in the response_message internationalization property file.<p/>
 * <p>And support the replacement of parameters passed.<p/>
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/16
 * @since jdk1.8
 */
public class ResponseException extends Exception
{
    /**
     * error code
     */
    private String code;

    /**
     * error parameters
     */
    private Object[] params;

    /**
     * constructor
     *
     * @param other other exception object
     */
    public ResponseException(ResponseException other)
    {
        super(other);
        this.setException(other);
    }

    /**
     * constructor
     *
     * @param ex   exception
     * @param code error code
     */
    public ResponseException(Exception ex,String code)
    {
        super(ex);
        this.code=code;
        if(ex instanceof ResponseException)
        {
            this.setException((ResponseException)ex);
        }
    }

    /**
     * constructor
     *
     * @param ex   exception
     * @param code error code
     */
    public ResponseException(Exception ex,String code,Object... params)
    {
        super(ex);
        this.code=code;
        this.params=params;
        if(ex instanceof ResponseException)
        {
            this.setException((ResponseException)ex);
        }
    }

    /**
     * constructor
     *
     * @param code error code
     */
    public ResponseException(String code)
    {
        super(code,null);
        this.code=code;
    }

    /**
     * constructor
     *
     * @param cause error throw stack
     * @param code  error code
     */
    public ResponseException(Throwable cause,String code)
    {
        super(code,cause);
        this.code=code;
    }

    /**
     * constructor
     *
     * @param code   error code
     * @param params error parameters
     */
    public ResponseException(String code,Object... params)
    {
        super(code,null);
        this.code=code;
        this.params=params;
    }

    /**
     * constructor
     *
     * @param cause  error throw stack
     * @param code   error code
     * @param params error parameters
     */
    public ResponseException(Throwable cause,String code,Object... params)
    {
        super(code,cause);
        this.code=code;
        this.params=params;
    }

    /**
     * constructor
     *
     * @param message error message
     * @param cause   error throw stack
     * @param code    error code
     * @param params  error parameters
     */
    public ResponseException(String message,Throwable cause,String code,Object... params)
    {
        super(message,cause);
        this.code=code;
        this.params=params;
    }

    /**
     * 设置属性
     *
     * @param other exception
     */
    public void setException(ResponseException other)
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