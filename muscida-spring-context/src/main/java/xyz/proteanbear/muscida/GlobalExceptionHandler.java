package xyz.proteanbear.muscida;

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
import javax.validation.ValidationException;

/**
 * Global exception handler that returns a unified data structure after capture.
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
     * log
     */
    private Logger logger=LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * The prefix of the returned information
     */
    private static final String messagePrefix="{";

    /**
     * The suffix for the returned information
     */
    private static final String messageSuffix="}";

    /**
     * Current attribute description replacement content
     */
    public static final String messageFieldReplace="${field}";

    /**
     * International resources
     */
    @Autowired
    @Qualifier("responseMessageResource")
    private ReloadableResourceBundleMessageSource messageSource;

    /**
     * 400 - Bad Request
     *
     * @param request   web request
     * @param exception the exception object
     * @return Response object
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response httpMessageNotReadableException(
            HttpServletRequest request,HttpMessageNotReadableException exception)
    {
        return exceptionHandler(
                request,
                new ResponseException(exception,"BAD_REQUEST"),
                "BAD_REQUEST"
        );
    }

    /**
     * 405 - Method Not Allowed
     *
     * @param request   web request
     * @param exception the exception object
     * @return Response object
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Response httpRequestMethodNotSupportedException(
            HttpServletRequest request,
            HttpRequestMethodNotSupportedException exception)
    {
        return exceptionHandler(
                request,
                new ResponseException(exception,"METHOD_NOT_ALLOWED"),
                "METHOD_NOT_ALLOWED"
        );
    }

    /**
     * 415 - Unsupported Media Type
     *
     * @param request   web request
     * @param exception the exception object
     * @return Response object
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Response httpMessageNotReadableException(
            HttpServletRequest request,
            HttpMediaTypeNotSupportedException exception)
    {
        return exceptionHandler(
                request,
                new ResponseException(exception,"UNSUPPORTED_MEDIA_TYPE"),
                "UNSUPPORTED_MEDIA_TYPE"
        );
    }

    /**
     * 400 - Bad Request,Bind Exception
     *
     * @param request   web request
     * @param exception the exception object
     * @return Response object
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response bindException(HttpServletRequest request,BindException exception)
    {
        return exceptionHandler(request,exception,"BAD_REQUEST");
    }

    /**
     * 400 - Bad Request,Validation Exception
     *
     * @param request   web request
     * @param exception the exception object
     * @return Response object
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response validationException(HttpServletRequest request,ValidationException exception)
    {
        return exceptionHandler(request,exception,"BAD_REQUEST");
    }

    /**
     * Handle the specified exception thrown by the Controller
     * The return status is 500 (server error)
     *
     * @param request   web request
     * @param exception the exception object
     * @return Response object
     */
    @ExceptionHandler(ResponseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response ResponseException(HttpServletRequest request,ResponseException exception)
    {
        return exceptionHandler(request,exception,"INTERNAL_SERVER_ERROR");
    }

    /**
     * Handle all exceptions thrown by Controller
     * The return status is 500 (server error)
     *
     * @param request   web request
     * @param exception the exception object
     * @return Response object
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(HttpServletRequest request,Exception exception)
    {
        String code="INTERNAL_SERVER_ERROR";
        String message=exception.getMessage();
        code=(message.startsWith(messagePrefix) && message.endsWith(messageSuffix)
                ?message.replace(messagePrefix,"").replace(messageSuffix,"")
                :code);
        return exceptionHandler(request,new ResponseException(exception,code),"INTERNAL_SERVER_ERROR");
    }

    /**
     * Exception handler
     *
     * @param request       web request
     * @param exception     the exception object
     * @param defaultStatus the default response code
     * @return Response object
     */
    private Response exceptionHandler(
            HttpServletRequest request,
            Exception exception,
            String defaultStatus)
    {
        String message=exception.getMessage();
        //If the system error is specified error code
        if(exception instanceof ResponseException)
        {
            ResponseException ResponseException=(ResponseException)exception;
            message=messageSource.getMessage(
                    ResponseException.getCode(),
                    ResponseException.getParams(),
                    RequestContextUtils.getLocale(request)
            );
            defaultStatus=ResponseException.getCode();
        }
        //If you return an error for the validator
        else if(exception instanceof BindException)
        {
            BindException bindException=(BindException)exception;
            message=translateValidatorMessage(
                    request,
                    bindException.getBindingResult()
            );
        }
        //Print error exception
        logger.error(message,exception);
        return new Response(defaultStatus,message,null);
    }

    /**
     * Convert the message generated by the validator to replace the current attribute name
     *
     * @param bindingResult error result for binding
     * @return response message
     */
    private String translateValidatorMessage(HttpServletRequest request,BindingResult bindingResult)
    {
        FieldError fieldError=bindingResult.getFieldError();
        String code=bindingResult.getTarget().getClass().getSimpleName()+"."+fieldError.getField();

        //Return replacement
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
