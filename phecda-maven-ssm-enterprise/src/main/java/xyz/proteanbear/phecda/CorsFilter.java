package xyz.proteanbear.phecda;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 过滤器：跨域设置，对请求Header进行设置
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/17
 * @since jdk1.8
 */
public class CorsFilter extends OncePerRequestFilter
{
    /**
     * 执行过滤，设置跨域请求等头部信息
     *
     * @param request     web请求
     * @param response    web返回
     * @param filterChain 过滤器
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        //Access-Control-Allow-Origin
        //指定允许访问的特定域名，即跨域支持
        String allowOrigin=getFilterConfig().getInitParameter("Access-Control-Allow-Origin");
        if(allowOrigin!=null && !"".equals(allowOrigin))
        {
            //全部
            if("*".equals(allowOrigin))
            {
                response.setHeader("Access-Control-Allow-Origin","*");
            }
            //可访问域列表
            else
            {
                List<String> allowOriginList=Arrays.asList(allowOrigin.split(","));
                if(!allowOriginList.isEmpty())
                {
                    String currentOrigin=request.getHeader("Origin");
                    if(allowOriginList.contains(currentOrigin))
                    {
                        response.setHeader("Access-Control-Allow-Origin",currentOrigin);
                    }
                }
            }
        }

        //Access-Control-Allow-Methods
        //访问的资源允许使用的方法或方法列表，如GET、PUT、DELETE等
        String allowMethods=getFilterConfig().getInitParameter("Access-Control-Allow-Methods");
        if(allowMethods!=null && !"".equals(allowMethods))
        {
            response.setHeader("Access-Control-Allow-Methods",allowMethods);
        }

        //Access-Control-Allow-Credentials
        //表示是否可以将对请求的响应暴露给页面。返回true则可以，其他值均不可以。
        String allowCredentials=getFilterConfig().getInitParameter("Access-Control-Allow-Credentials");
        if(allowCredentials!=null && !"".equals(allowCredentials))
        {
            response.setHeader("Access-Control-Allow-Credentials",allowCredentials);
        }

        //Access-Control-Allow-Headers
        //可以支持的消息首部列表，用逗号隔开
        String allowHeaders=getFilterConfig().getInitParameter("Access-Control-Allow-Headers");
        if(allowHeaders!=null && !"".equals(allowHeaders))
        {
            response.setHeader("Access-Control-Allow-Methods",allowHeaders);
        }

        //Access-Control-Expose-Headers
        //列出了哪些首部可以作为响应的一部分暴露给外部
        String exposeHeaders=getFilterConfig().getInitParameter("Access-Control-Expose-Headers");
        if(exposeHeaders!=null && !"".equals(exposeHeaders))
        {
            response.setHeader("Access-Control-Expose-Headers",exposeHeaders);
        }

        filterChain.doFilter(request,response);
    }
}