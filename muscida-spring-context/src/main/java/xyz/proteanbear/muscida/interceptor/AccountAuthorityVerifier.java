package xyz.proteanbear.muscida.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.proteanbear.muscida.Authority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * Interceptors: Verify user permission verification for the current invoking interface
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/09
 * @since jdk1.8
 */
public class AccountAuthorityVerifier implements HandlerInterceptor
{
    /**
     * Log
     */
    private static final Logger logger=LoggerFactory.getLogger(AccountAuthorityVerifier.class);

    /**
     * If set to true, the system only performs privilege verification on interface calls with annotations;
     * If set to false, the system will only perform no privilege verification on the interface with annotations;
     */
    private boolean publicRestful=false;

    /**
     * Current account loader
     */
    @NotNull
    private Authority.AccountHandler accountHandler;

    /**
     * Get the annotation of method and verify it authority
     *
     * @param request  web request
     * @param response web response
     * @param handler  handle method
     * @return If account authority is right ,return true
     * @throws Exception If one parameter is wrong,throw exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception
    {
        boolean result=publicRestful;

        //check handler
        if(!(handler instanceof HandlerMethod))
        {
            logger.warn("Handler object is not HandlerMethod!");
            return result;
        }

        //Get @Authority.Set annotation
        HandlerMethod method=(HandlerMethod)handler;
        if(!method.hasMethodAnnotation(Authority.Set.class))
        {
            logger.warn("Have not a authority annotation!Use default setting:",publicRestful?"All allow!":"All Reject!");
            return result;
        }

        //Handle @Authority.Set annotation
        Authority.Set authority=method.getMethodAnnotation(Authority.Set.class);
        //Log current authority content
        if(logger.isDebugEnabled())
        {
            RequestMapping requestMapping=method.getMethodAnnotation(RequestMapping.class);
            logger.debug("Get method {}'s annotation @authority of a url '{}',and setting is:{}",method.getMethod().getName(),getLogContent(requestMapping.value()),authority);
        }

        //If not must login
        if(!authority.mustLogin()) return true;

        //if must login
        Authority.Account account=accountHandler.get(request);
        Class accountClass=account.getClass();
        //Check account class
        result=false;
        for(Class curClass:authority.accountClass())
        {
            if(curClass.isAssignableFrom(accountClass))
            {
                //Only allow accountClass access
                if(authority.allow()){result=true;break;}
                //Not allow accountClass access
                else throw new Exception(authority.message());
            }
        }
        //Check account class name
        if(!result)
        {
            for(String curClass:authority.accountClassName())
            {
                if(curClass.equalsIgnoreCase(accountClass.getSimpleName()))
                {
                    //Only allow accountClass access
                    if(authority.allow()){result=true;break;}
                    //Not allow accountClass access
                    else throw new Exception(authority.message());
                }
            }
        }
        //Custom check
        if(result) result=account.ok(authority.customData());

        //Handle result
        if(!result) throw new Exception(authority.message());

        return true;
    }

    /**
     * @param publicRestful is public
     */
    public void setPublicRestful(boolean publicRestful)
    {
        this.publicRestful=publicRestful;
    }

    /**
     * @param accountHandler account loader implement
     */
    public void setAccountHandler(Authority.AccountHandler accountHandler)
    {
        assert null!=accountHandler;
        this.accountHandler=accountHandler;
    }

    /**
     * @param content content array
     * @return Comma-separated string content
     */
    private String getLogContent(Object[] content)
    {
        StringBuilder result=new StringBuilder();
        for(int i=0;i<content.length;i++)
        {
            result.append(i==0?"":",").append(content[i]);
        }
        return result.toString();
    }
}