package xyz.proteanbear.phecda;

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
 * 全局返回处理器，捕捉后返回统一的JSON数据结构
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/26
 * @since jdk1.8
 */
@ControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object>
{
    /**
     * 记录日志
     */
    private Logger logger=LoggerFactory.getLogger(GlobalResponseHandler.class);

    /**
     * 国际化资源
     */
    @Autowired
    @Qualifier("responseMessageResource")
    private ReloadableResourceBundleMessageSource messageSource;

    /**
     * 开启支持
     *
     * @param methodParameter 方法参数
     * @param aClass          消息转换类
     * @return 是否支持@ResponseBody捕捉
     */
    @Override
    public boolean supports(MethodParameter methodParameter,Class<? extends HttpMessageConverter<?>> aClass)
    {
        return true;
    }

    /**
     * 写入response body前对结果进行包装处理
     *
     * @param data               返回数据
     * @param methodParameter    方法参数
     * @param mediaType          返回类型
     * @param aClass             消息转换器
     * @param serverHttpRequest  请求
     * @param serverHttpResponse 返回
     * @return 要写入输出的内容
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
        //已经包装过，则直接返回
        if(data instanceof Response) return data;

        //使用Response进行输出包装
        HttpServletRequest request=((ServletServerHttpRequest)serverHttpRequest).getServletRequest();
        return new Response(
                ResponseCode.SUCCESS,
                messageSource.getMessage(
                        ResponseCode.SUCCESS,
                        null,
                        RequestContextUtils.getLocale(request)
                ),
                data
        );
    }
}