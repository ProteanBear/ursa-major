package xyz.proteanbear.phecda.tools;

import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 多语言环境信息获取工具
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/25
 * @since jdk1.8
 */
public class LocaleMessageUtils
{
    /**
     * 信息文件配置
     */
    private static final String baseNames="message.ResponseMessages,message.ValidationMessages";

    /**
     * 获取指定code对应的消息，并使用args进行内容替换
     *
     * @param code 消息代码
     * @param args 替换内容
     * @return 默认系统语言环境下的信息内容
     */
    public static String message(String code,Object... args)
    {
        return message(Locale.getDefault(),code,args);
    }

    /**
     * 获取指定code对应的消息，并使用args进行内容替换
     *
     * @param code 消息代码
     * @param args 替换内容
     * @return 当前设置语言环境下的信息内容
     */
    public static String message(HttpServletRequest request,String code,Object... args)
    {
        return message(RequestContextUtils.getLocale(request),code,args);
    }

    /**
     * 获取指定code对应的消息，并使用args进行内容替换
     *
     * @param code 消息代码
     * @param args 替换内容
     * @return 指定语言环境下的信息内容
     */
    public static String message(Locale locale,String code,Object... args)
    {
        //遍历全部消息文件
        String[] baseNameArray=baseNames.split(",");
        ResourceBundle bundle;
        String result=code;
        for(String baseName:baseNameArray)
        {
            bundle=ResourceBundle.getBundle(baseName+"_"+locale,locale);
            try
            {
                result=MessageFormat.format(new String(bundle.getString(code).getBytes("ISO-8859-1"),"UTF-8"),args);
                if(!code.equals(result)) break;
            }
            catch(UnsupportedEncodingException e)
            {
                result="Un supported encoding exception";
                break;
            }
        }
        return result;
    }
}