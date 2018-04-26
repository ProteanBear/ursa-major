package xyz.proteanbear.phecda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器，捕捉后返回统一的数据结构
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/13
 * @since jdk1.8
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler
{
    /**
     * 记录日志
     */
    private Logger logger=LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 对应返回信息的前缀
     */
    private static final String messagePrefix="{";

    /**
     * 对应返回信息的后缀
     */
    private static final String messageSuffix="}";

    /**
     * 当前属性描述替换内容
     */
    public static final String messageFieldReplace="${field}";

    /**
     * 国际化资源
     */
    @Autowired
    @Qualifier("responseMessageResource")
    private ReloadableResourceBundleMessageSource messageSource;

    /**
     * 400 - Bad Request
     *
     * @param request   web请求
     * @param exception 异常信息
     * @return 返回内容
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response httpMessageNotReadableException(
            HttpServletRequest request,HttpMessageNotReadableException exception)
    {
        return exceptionHandler(
                request,
                new PhecdaException(exception,ResponseCode.BAD_REQUEST),
                ResponseCode.BAD_REQUEST
        );
    }

    /**
     * 405 - Method Not Allowed
     *
     * @param request   web请求
     * @param exception 异常信息
     * @return 返回内容
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Response httpRequestMethodNotSupportedException(
            HttpServletRequest request,
            HttpRequestMethodNotSupportedException exception)
    {
        return exceptionHandler(
                request,
                new PhecdaException(exception,ResponseCode.METHOD_NOT_ALLOWED),
                ResponseCode.METHOD_NOT_ALLOWED
        );
    }

    /**
     * 415 - Unsupported Media Type
     *
     * @param request   web请求
     * @param exception 异常信息
     * @return 返回内容
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Response httpMessageNotReadableException(
            HttpServletRequest request,
            HttpMediaTypeNotSupportedException exception)
    {
        return exceptionHandler(
                request,
                new PhecdaException(exception,ResponseCode.UNSUPPORTED_MEDIA_TYPE),
                ResponseCode.UNSUPPORTED_MEDIA_TYPE
        );
    }

    /**
     * 400 - Bad Request,Bind Exception
     *
     * @param request   web请求
     * @param exception 异常信息
     * @return 返回内容
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response bindException(HttpServletRequest request,BindException exception)
    {
        return exceptionHandler(request,exception,ResponseCode.BAD_REQUEST);
    }

    /**
     * 处理Controller抛出的指定异常
     * 返回状态为500（服务端错误）
     *
     * @param request   web请求
     * @param exception 异常信息
     * @return 返回内容
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
     * @param request   web请求
     * @param exception 异常信息
     * @return 返回内容
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(HttpServletRequest request,Exception exception)
    {
        String code=ResponseCode.INTERNAL_SERVER_ERROR;
        String message=exception.getMessage();
        code=(message.startsWith(messagePrefix) && message.endsWith(messageSuffix)
                ?message.replace(messagePrefix,"").replace(messageSuffix,"")
                :code);
        return exceptionHandler(request,new PhecdaException(exception,code),ResponseCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 异常处理
     *
     * @param request       web请求
     * @param exception     异常信息
     * @param defaultStatus 默认使用的状态码
     * @return 返回内容
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
                    messageSource.getMessage(
                            phecdaException.getCode(),
                            phecdaException.getParams(),
                            RequestContextUtils.getLocale(request)
                    ),
                    null
            );
        }
        //如果为校验器返回错误
        else if(exception instanceof BindException)
        {
            BindException bindException=(BindException)exception;
            return new Response(
                    defaultStatus,
                    translateValidatorMessage(
                            request,
                            bindException.getBindingResult()
                    ),
                    null
            );
        }
        return new Response(defaultStatus,exception.getMessage(),null);
    }

    /**
     * 转换校验器生成的消息，替换当前属性名称
     *
     * @param bindingResult 错误结果
     * @return 输出消息
     */
    private String translateValidatorMessage(HttpServletRequest request,BindingResult bindingResult)
    {
        FieldError fieldError=bindingResult.getFieldError();
        String code=bindingResult.getTarget().getClass().getSimpleName()+"."+fieldError.getField();

        //返回替换
        return fieldError.getDefaultMessage().replace(
                messageFieldReplace,
                messageSource.getMessage(
                        code,
                        null,
                        fieldError.getField(),
                        RequestContextUtils.getLocale(request)
                )
        );
    }
}
