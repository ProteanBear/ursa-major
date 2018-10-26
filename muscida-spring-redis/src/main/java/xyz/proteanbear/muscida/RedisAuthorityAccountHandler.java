package xyz.proteanbear.muscida;

import xyz.proteanbear.muscida.utils.ClassAndObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * Use Redis to store login account information
 * and use the Token method to implement the Authority.AccountHandler interface.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/10
 * @since jdk1.8
 */
public class RedisAuthorityAccountHandler implements Authority.AccountHandler
{
    /**
     * Token Key
     */
    private String tokenKey="token";

    /**
     * Redis command executor
     */
    private RedisExecutor redisExecutor;

    /**
     * token expire time
     */
    private long expireTime=-1;

    /**
     * Time unit
     */
    private TimeUnit timeUnit=TimeUnit.SECONDS;

    /**
     * Get the account object from a redis by using a token from web request header or parameter.
     *
     * @param request web request
     * @return current account object
     */
    @Override
    public Authority.Account get(HttpServletRequest request)
    {
        //Token
        String token=getParameterFrom(request,tokenKey);

        //Token Type
        String className=getParameterFrom(request,getStoreTypeKey());

        //Get
        return get(token,className);
    }

    /**
     * Get the account object through token string
     *
     * @param fromToken token string
     * @param forClass  class name
     * @return current account object
     */
    @Override
    public Authority.Account get(String fromToken,String forClass)
    {
        if(fromToken==null||"".equals(fromToken)) return null;
        if(forClass==null||"".equals(forClass)) return null;

        Class classType=null;
        try
        {
            classType=Class.forName(forClass);
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        if(classType==null) return null;

        //Get the account object by using the token
        Object result=redisExecutor.get(getStoreTokenKey(fromToken),classType);
        return result==null?null
                :(ClassAndObjectUtils.isImplement(classType,Authority.Account.class)
                ?((Authority.Account)result)
                :null);
    }

    /**
     * @param request web request
     * @return token string
     */
    @Override
    public String token(HttpServletRequest request)
    {
        return getParameterFrom(request,tokenKey);
    }

    /**
     * @param request web request
     * @return account object type string
     */
    @Override
    public String type(HttpServletRequest request)
    {
        return getParameterFrom(request,getStoreTypeKey());
    }

    /**
     * Store the account object into the redis
     *
     * @param account current account object
     */
    @Override
    public void store(HttpServletResponse response,Authority.Account account)
    {
        assert null!=account;
        String token=account.customToken();
        if(token!=null)
        {
            //Redis
            redisExecutor.set(getStoreTokenKey(token),account,expireTime,timeUnit);
            //Cookie
            response.addCookie(new Cookie(tokenKey,token));
            response.addCookie(new Cookie(getStoreTypeKey(),account.getClass().getName()));
            //Header
            response.addHeader(tokenKey,token);
            response.addHeader(getStoreTypeKey(),account.getClass().getName());
        }
    }

    /**
     * Remove the account object
     *
     * @param request  web request
     * @param response web response
     */
    @Override
    public void remove(HttpServletRequest request,HttpServletResponse response)
    {
        //Token
        String token=getParameterFrom(request,tokenKey);
        if(null!=token && !"".equals(token))
        {
            //Remove Header
            response.setHeader(tokenKey,null);
            response.addHeader(getStoreTypeKey(),null);
            //Remove Cookie
            response.addCookie(new Cookie(tokenKey,null));
            response.addCookie(new Cookie(getStoreTypeKey(),null));
            //Remove from redis
            redisExecutor.delete(getStoreTokenKey(token));
        }
    }

    /**
     * @param tokenKey token key stored in request header or cookie
     */
    public void setTokenKey(String tokenKey)
    {
        this.tokenKey=tokenKey;
    }

    /**
     * @param redisExecutor redis command executor
     */
    public void setRedisExecutor(RedisExecutor redisExecutor)
    {
        this.redisExecutor=redisExecutor;
    }

    /**
     * @param expireTime expire time
     */
    public void setExpireTime(long expireTime)
    {
        this.expireTime=expireTime;
    }

    /**
     * @param timeUnit expire time unit
     */
    public void setTimeUnit(TimeUnit timeUnit)
    {
        this.timeUnit=timeUnit;
    }

    /**
     * @param token token string
     * @return token store key
     */
    private String getStoreTokenKey(String token)
    {
        return tokenKey+"_"+token;
    }

    /**
     * @return token store object type key
     */
    private String getStoreTypeKey()
    {
        return tokenKey+"_type";
    }

    /**
     * Get the parameter value named paramName from http request.
     * From header/cookie/parameter
     *
     * @param request   web http request
     * @param paramName parameter name
     * @return the parameter value
     */
    private String getParameterFrom(HttpServletRequest request,String paramName)
    {
        //Get the value from the web request header
        String result=request.getHeader(paramName);

        //If value is null,get from cookie
        if(null==result || "".equals(result))
        {
            if(request.getCookies()!=null)
            {
                for(Cookie cookie : request.getCookies())
                {
                    if(paramName.equalsIgnoreCase(cookie.getName()))
                    {
                        result=cookie.getValue();
                        break;
                    }
                }
            }
        }

        //If value is null,get from request parameter
        if(null==result || "".equals(result))
        {
            result=request.getParameter(paramName);
        }

        return result;
    }
}