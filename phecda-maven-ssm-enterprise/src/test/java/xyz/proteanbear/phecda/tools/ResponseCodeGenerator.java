package xyz.proteanbear.phecda.tools;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * 返回状态码生成器
 * 载入response_message属性文件并生成ResponseCode类
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/16
 * @since 1.0
 */
public class ResponseCodeGenerator
{
    /**
     * 类模板内容
     */
    private String templateClass="package xyz.proteanbear.phecda.exception;\n\n"+
            "/**\n"+
            " * 返回状态码字典（使用生成器生成）\n"+
            " * @author ProteanBear\n"+
            " * @version 1.0.0,${DATE_CURRENT}\n"+
            " * @since 1.0\n"+
            " */\n"+
            "public class ResponseCode\n";

    /**
     * 属性字段模板内容
     */
    private String templateField="\n"+
            "    /**\n"+
                    "     * ${NAME}:${VALUE}\n"+
                    "     */\n"+
                    "    public static final String ${NAME}=\"${NAME}\";\n";

    /**
     * 属性内容
     */
    private Properties properties;

    /**
     * 生成内容
     */
    private StringBuilder content=new StringBuilder();

    /**
     * 日期格式化
     */
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 构造
     *
     * @param properties
     */
    public ResponseCodeGenerator(Properties properties)
    {
        this.properties=properties;
    }

    /**
     * 生成
     *
     * @return
     */
    public ResponseCodeGenerator generate()
    {
        //清空
        content.delete(0,content.length());

        //类顶部
        content.append(templateClass.replaceAll(
                "\\$\\{DATE_CURRENT\\}",
                dateFormat.format(new Date())
        ));
        content.append("{\n");

        //循环生成静态字段内容
        Object value;
        for(String name : properties.stringPropertyNames())
        {
            value=properties.get(name);
            content.append(
                    templateField
                            .replaceAll("\\$\\{NAME\\}",name)
                            .replaceAll("\\$\\{VALUE\\}",value==null?"":(value+""))
            );
        }

        content.append("}");
        return this;
    }

    /**
     * 保存到指定的位置
     *
     * @param file
     */
    public void saveTo(File file) throws IOException
    {
        //删除已经存在的文件
        if(file.exists()) file.delete();
        //创建新文件
        file.createNewFile();
        //写入文件
        byte bytes[]=new byte[512];
        FileOutputStream outputStream=new FileOutputStream(file);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
        writer.write(content.toString());
        writer.close();
    }

    /**
     * 生成
     *
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("开始生成ResponseCode类！");

        //获取当前项目所在位置
        String projectPath=System.getProperty("user.dir");
        System.out.println("获取当前项目路径:"+projectPath);

        //response_message属性文件路径
        String propertiesFilePath=projectPath
                +File.separator+"src"
                +File.separator+"main"
                +File.separator+"resources"
                +File.separator+"message"
                +File.separator+"ResponseMessage_"
                +Locale.getDefault().getLanguage()
                +"_"+Locale.getDefault().getCountry()
                +".properties";
        System.out.println("获取response_message属性文件路径:"+propertiesFilePath);

        //生成类的文件路径
        String classFilePath=projectPath
                +File.separator+"src"
                +File.separator+"main"
                +File.separator+"java"
                +File.separator+"xyz"
                +File.separator+"proteanbear"
                +File.separator+"phecda"
                +File.separator+"exception"
                +File.separator+"ResponseCode"
                +".java";
        System.out.println("获取ResponseCode类文件路径:"+classFilePath);

        //读取属性文件
        Properties properties=new Properties();
        try
        {
            properties.load(new InputStreamReader(
                    new FileInputStream(propertiesFilePath),"UTF-8")
            );
            new ResponseCodeGenerator(properties)
                    .generate()
                    .saveTo(new File(classFilePath));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("生成ResponseCode类完成！");
    }
}