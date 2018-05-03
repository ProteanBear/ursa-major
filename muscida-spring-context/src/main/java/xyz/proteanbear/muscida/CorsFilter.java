package xyz.proteanbear.muscida;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter: Cross-domain settings, set the request header
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/17
 * @since jdk1.8
 */
public class CorsFilter extends OncePerRequestFilter
{
    /**
     * Perform filtering and set cross-domain request header information.
     *
     * @param request     Web request
     * @param response    Web response
     * @param filterChain filter object
     * @throws ServletException Servlet exceptions
     * @throws IOException      IO Exceptions
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        //Access-Control-Allow-Origin
        //Specifies the specific domain name allowed to access,
        //that is, cross-domain support
        String allowOrigin=getFilterConfig().getInitParameter("Access-Control-Allow-Origin");
        if(allowOrigin!=null && !"".equals(allowOrigin))
        {
            //All
            if("*".equals(allowOrigin))
            {
                response.setHeader("Access-Control-Allow-Origin","*");
            }
            //Accessible domain list
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
        //A list of methods or methods allowed by the accessed resource,
        //such as GET, PUT, DELETE, etc.
        String allowMethods=getFilterConfig().getInitParameter("Access-Control-Allow-Methods");
        if(allowMethods!=null && !"".equals(allowMethods))
        {
            response.setHeader("Access-Control-Allow-Methods",allowMethods);
        }

        //Access-Control-Allow-Credentials
        //Indicates whether the response to the request can be exposed to the page.
        //Return true if you can, other values are not.
        String allowCredentials=getFilterConfig().getInitParameter("Access-Control-Allow-Credentials");
        if(allowCredentials!=null && !"".equals(allowCredentials))
        {
            response.setHeader("Access-Control-Allow-Credentials",allowCredentials);
        }

        //Access-Control-Allow-Headers
        //List of message headers that can be supported, separated by commas
        String allowHeaders=getFilterConfig().getInitParameter("Access-Control-Allow-Headers");
        if(allowHeaders!=null && !"".equals(allowHeaders))
        {
            response.setHeader("Access-Control-Allow-Methods",allowHeaders);
        }

        //Access-Control-Expose-Headers
        //Lists which headers can be exposed to the outside as part of the response.
        String exposeHeaders=getFilterConfig().getInitParameter("Access-Control-Expose-Headers");
        if(exposeHeaders!=null && !"".equals(exposeHeaders))
        {
            response.setHeader("Access-Control-Expose-Headers",exposeHeaders);
        }

        filterChain.doFilter(request,response);
    }
}