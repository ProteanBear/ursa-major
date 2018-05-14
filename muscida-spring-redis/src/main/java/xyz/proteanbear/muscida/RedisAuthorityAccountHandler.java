package xyz.proteanbear.muscida;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
        //Get the token from the web request header
        String token=request.getHeader(tokenKey);

        //If token is null,get from cookie
        if(null==token || "".equals(token))
        {
            for(Cookie cookie : request.getCookies())
            {
                if(tokenKey.equalsIgnoreCase(cookie.getName()))
                {
                    token=cookie.getValue();
                    break;
                }
            }
        }

        //If token is null
        if(token==null) return null;

        //Get the account object by using the token
        return redisExecutor.get(getStoreTokenKey(token),Authority.Account.class);
    }

    /**
     * Store the account object into the redis
     *
     * @param account current account object
     */
    @Override
    public String store(Authority.Account account)
    {
        assert null!=account;
        String token=account.customToken();
        if(token!=null) redisExecutor.set(getStoreTokenKey(token),account,expireTime,timeUnit);
        return token;
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
}