package xyz.proteanbear.muscida;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.support.RequestContextUtils;
import xyz.proteanbear.muscida.interceptor.AccountAuthorityVerifier;
import xyz.proteanbear.muscida.utils.ClassAndObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Global return processor, return to unified JSON data structure after capture.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/26
 * @since jdk1.8
 */
@ControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object>
{
    /**
     * Record log
     */
    private Logger logger=LoggerFactory.getLogger(GlobalResponseHandler.class);

    /**
     * Jackson
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * International resources
     */
    @Autowired
    @Qualifier("responseMessageResource")
    private ReloadableResourceBundleMessageSource messageSource;

    /**
     * Authority account handler
     */
    @Autowired
    private Authority.AccountHandler accountHandler;

    /**
     * Response wrapper：If empty, use the default implementation.
     */
    @Autowired
    private ResponseWrapper responseWrapper;

    /**
     * Turn on support
     *
     * @param methodParameter the method parameter
     * @param aClass          Message conversion class
     * @return Does it support @ResponseBody capture
     */
    @Override
    public boolean supports(MethodParameter methodParameter,Class<? extends HttpMessageConverter<?>> aClass)
    {
        return true;
    }

    /**
     * Wrap the result before writing the response body
     *
     * @param data               return data
     * @param methodParameter    method parameter
     * @param mediaType          return media type
     * @param aClass             Message conversion class
     * @param serverHttpRequest  http request
     * @param serverHttpResponse http response
     * @return To write the output
     */
    @Override
    public Object beforeBodyWrite(
            Object data,
            MethodParameter methodParameter,
            MediaType mediaType,
            Class<? extends HttpMessageConverter<?>> aClass,
            ServerHttpRequest serverHttpRequest,
            ServerHttpResponse serverHttpResponse)
    {
        //Store the data for the method with authority annotation AutoStore
        if(methodParameter.hasMethodAnnotation(Authority.Set.class))
        {
            Authority.Set authoritySetting=methodParameter.getMethodAnnotation(Authority.Set.class);
            HttpServletRequest request=((ServletServerHttpRequest)serverHttpRequest).getServletRequest();
            HttpServletResponse response=((ServletServerHttpResponse)serverHttpResponse).getServletResponse();

            //Store the token and account object
            if(authoritySetting.autoStore())
            {
                if(accountHandler==null)
                {
                    logger.error("Account handler object is null!");
                }
                else if(!ClassAndObjectUtils.isImplement(data,Authority.Account.class))
                {
                    logger.warn("Return data is not a instance of the class Authority.Account!Data is not stored!");
                }
                else
                {
                    boolean isLogin=(Boolean)request.getAttribute(AccountAuthorityVerifier.ATTRIBUTE_IS_LOGIN);
                    if(!isLogin)
                    {
                        accountHandler.store(response,(Authority.Account)data);
                    }
                }
            }

            //Remove the token and account object
            if(authoritySetting.autoRemove())
            {
                accountHandler.remove(request,response);
            }
        }

        //Has not been packaged
        responseWrapper=(responseWrapper==null)
                ?(new ResponseWrapper.Default())
                :responseWrapper;
        if(!(responseWrapper.isObjectAfterWrap(data)))
        {
            //Use Response for output packaging
            HttpServletRequest request=((ServletServerHttpRequest)serverHttpRequest).getServletRequest();
            data=responseWrapper.wrap(
                    messageSource.getMessage(
                            "SUCCESS",
                            null,
                            RequestContextUtils.getLocale(request)
                    )
                    ,data
            );
        }

        //Debug log
        try
        {
            if(logger.isDebugEnabled())
            {
                logger.debug(
                        "Return content:{}",
                        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
                );
            }
        }
        catch(JsonProcessingException e)
        {
            logger.error(e.getMessage(),e);
        }

        return data;
    }
}