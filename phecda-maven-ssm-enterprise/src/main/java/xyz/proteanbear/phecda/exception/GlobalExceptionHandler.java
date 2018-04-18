package xyz.proteanbear.phecda.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.RequestContext;
import xyz.proteanbear.phecda.rest.Response;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

/**
 * 全局异常处理器，捕捉后返回统一的数据结构
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/13
 * @since 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler
{
    //记录日志
    private Logger logger=LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //对应返回信息的前缀
    private static final String messagePrefix="{";

    //对应返回信息的后缀
    private static final String messageSuffix="}";

    /**
     * 400 - Bad Request
     *
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response httpMessageNotReadableException(
            HttpServletRequest request,HttpMessageNotReadableException exception)
    {
        return exceptionHandler(request,new PhecdaException(exception,ResponseCode.BAD_REQUEST),ResponseCode.BAD_REQUEST);
    }

    /**
     * 405 - Method Not Allowed
     *
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Response httpRequestMethodNotSupportedException(
            HttpServletRequest request,
            HttpRequestMethodNotSupportedException exception)
    {
        return exceptionHandler(request,new PhecdaException(exception,ResponseCode.METHOD_NOT_ALLOWED),ResponseCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 415 - Unsupported Media Type
     *
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Response httpMessageNotReadableException(
            HttpServletRequest request,
            HttpRequestMethodNotSupportedException exception)
    {
        return exceptionHandler(request,new PhecdaException(exception,ResponseCode.UNSUPPORTED_MEDIA_TYPE),ResponseCode.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * 400 - Bad Request,Validation Exception
     *
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response validationException(
            HttpServletRequest request,ValidationException exception)
    {
        return exceptionHandler(request,exception,ResponseCode.BAD_REQUEST);
    }

    /**
     * 处理Controller抛出的指定异常
     * 返回状态为500（服务端错误）
     *
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(PhecdaException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response phecdaException(HttpServletRequest request,PhecdaException exception)
    {
        return exceptionHandler(request,exception,ResponseCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理Controller抛出的全部异常
     * 返回状态为500（服务端错误）
     *
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(HttpServletRequest request,Exception exception)
    {
        String code=ResponseCode.INTERNAL_SERVER_ERROR;
        String message=exception.getMessage();
        code=(message.startsWith(messagePrefix)&&message.endsWith(messageSuffix)
                ?message
                    .replace(messagePrefix,"")
                    .replace(messageSuffix,"")
                :code);
        return exceptionHandler(request,new PhecdaException(exception,code),ResponseCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 异常处理
     *
     * @param request
     * @param exception
     * @return
     */
    private Response exceptionHandler(
            HttpServletRequest request,
            Exception exception,
            String defaultStatus)
    {
        //打印错误信息
        logger.error(exception.getMessage(),exception);
        //如果为指定错误码的系统异常
        if(exception instanceof PhecdaException)
        {
            PhecdaException phecdaException=(PhecdaException)exception;
            return new Response(
                    phecdaException.getCode(),
                    getMessageInLocale(
                            request,
                            phecdaException.getCode(),
                            phecdaException.getParams()),
                    null);
        }
        return new Response(defaultStatus,exception.getMessage(),null);
    }

    /**
     * 获取当前语言环境下的错误描述
     *
     * @param request
     * @return
     */
    private String getMessageInLocale(HttpServletRequest request,String key,Object[] args)
    {
        RequestContext requestContext=new RequestContext(request);
        return requestContext.getMessage(key,args);
    }
}