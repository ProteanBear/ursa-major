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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

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
        //Has not been packaged
        if(!(data instanceof Response))
        {
            //Use Response for output packaging
            HttpServletRequest request=((ServletServerHttpRequest)serverHttpRequest).getServletRequest();
            data=new Response(
                    "SUCCESS",
                    messageSource.getMessage(
                            "SUCCESS",
                            null,
                            RequestContextUtils.getLocale(request)
                    ),
                    data
            );
        }

        //Debug log
        try
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Return content:{}",objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
            }
        }
        catch(JsonProcessingException e)
        {
            logger.error(e.getMessage(),e);
        }

        return data;
    }
}