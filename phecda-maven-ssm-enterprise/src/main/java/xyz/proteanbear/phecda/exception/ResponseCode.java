package xyz.proteanbear.phecda.exception;

/**
 * 返回状态码字典（使用生成器生成）
 * @author ProteanBear
 * @version 1.0.0,2018/04/16
 * @since 1.0
 */
public class ResponseCode
{
    /**
     * SUCCESS:成功
     */
    public static final String SUCCESS="SUCCESS";
    /**
     * INTERNAL_SERVER_ERROR:服务运行异常
     */
    public static final String INTERNAL_SERVER_ERROR="INTERNAL_SERVER_ERROR";
    /**
     * BAD_REQUEST:参数解析错误
     */
    public static final String BAD_REQUEST="BAD_REQUEST";
    /**
     * UNAUTHORIZED:未授权
     */
    public static final String UNAUTHORIZED="UNAUTHORIZED";
    /**
     * METHOD_NOT_ALLOWED:不支持当前请求方法
     */
    public static final String METHOD_NOT_ALLOWED="METHOD_NOT_ALLOWED";
    /**
     * UNSUPPORTED_MEDIA_TYPE:不支持当前媒体类型
     */
    public static final String UNSUPPORTED_MEDIA_TYPE="UNSUPPORTED_MEDIA_TYPE";
}