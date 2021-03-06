package xyz.proteanbear.demo;

import java.util.Date;

/**
 * Mysql库中的user表对应Bean
 * @author ProteanBear
 */
public class User
{
    //终端位置
    private String host;
    //用户名
    private String user;
    //密码最后更新时间
    private Date   passwordLastChanged;

    public String getHost()
    {
        return host;
    }

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