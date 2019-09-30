package xyz.proteanbear.muscida.generator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * The mapper interface for mybatis generator
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/04
 * @since jdk1.8
 */
public class DataMapperGenerator implements CodeGenerator
{
    /**
     * Class template content
     */
    public static final String DEFAULT_TEMPLATE_CLASS=
            "package xyz.proteanbear.${TO_PACKAGE};\n"+
                    "\n"+
                    "import org.springframework.stereotype.Repository;\n"+
                    "import xyz.proteanbear.muscida.DefaultDataMapper;\n"+
                    "\n"+
                    "/**\n"+
                    " * Mapper for ${TABLE_NAME}\n"+
                    " *\n"+
                    " * @author ProteanBear\n"+
                    " * @version 1.0.0,${DATE_CURRENT}\n"+
                    " * @since jdk1.8\n"+
                    " */\n"+
                    "@Repository\n"+
                    "public interface ${BEAN_NAME}Mapper extends DefaultDataMapper<${BEAN_NAME}>\n"+
                    "{\n"+
                    "}";

    /**
     * Template for class
     */
    private String templateClass;

    /**
     * Date formatting
     */
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");

    /**
     * Mapping replacement value
     */
    private final Map<String,String> valueMap;

    /**
     * Constructor
     *
     * @param valueMap Mapping replacement value
     */
    public DataMapperGenerator(Map<String,String> valueMap)
    {
        this.valueMap=valueMap;
    }

    /**
     * Load the template file
     *
     * @param file template file
     */
    @Override
    public CodeGenerator loadTemplate(File file)
    {
        try
        {
            Map<String,String> templates=CodeGenerator.catchTemplate(file);
            this.templateClass=templates.getOrDefault(CodeGenerator.TEMPLATE_MAIN,DEFAULT_TEMPLATE_CLASS);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            this.templateClass=DEFAULT_TEMPLATE_CLASS;
        }
        return this;
    }

    /**
     * @return generate content
     */
    @Override
    public String generate()
    {
        String result=templateClass.replaceAll(
                "\\$\\{DATE_CURRENT\\}",
                dateFormat.format(new Date())
        );

        for(String key:valueMap.keySet())
        {
            result=result.replaceAll(
                    "\\$\\{"+key.toUpperCase()+"\\}",
                    valueMap.get(key)
            );
        }

        return result;
    }
}