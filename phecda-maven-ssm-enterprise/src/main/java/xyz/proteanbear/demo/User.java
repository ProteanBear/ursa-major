package xyz.proteanbear.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import xyz.proteanbear.muscida.Authority;
import xyz.proteanbear.muscida.DefaultBean;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean for
 *
 * @author ProteanBear
 * @version 1.0.0,${DATE_CURRENT}
 * @since jdk1.8
 */
public class User implements DefaultBean,Authority.Account,Serializable
{
    /**
     *
     */
    private String host;

    //用户名
    private String user;
    //密码最后更新时间
    private Date   passwordLastChanged;

    /**
     * @return the primary key value of the current data object
     */
    @Override
    public Object getPrimaryKeyValue()
    {
        return getUser();
    }

    /**
     * @return
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host
     */
    public void setHost(String host)
    {
        this.host=host;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user=user;
    }

    public Date getPasswordLastChanged()
    {
        return passwordLastChanged;
    }

    public void setPasswordLastChanged(Date passwordLastChanged)
    {
        this.passwordLastChanged=passwordLastChanged;
    }
}