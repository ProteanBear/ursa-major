package xyz.proteanbear.muscida.generator;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Code generator
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/16
 * @since 1.0
 */
public interface CodeGenerator
{
    /**
     * The key for the main template
     */
    public static final String TEMPLATE_MAIN="MAIN";

    /**
     * Capture all sub-templates from the template string and generate a map,
     * where main is the last main template and other sub-templates.
     *
     * @param templateTxt template file
     * @return The 'MAIN' key is the last main template and other sub-templates.
     */
    public static Map<String,String> catchTemplate(File templateTxt) throws IOException
    {
        //Read the .txt file
        BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(templateTxt)));

        String line, varStart="${", varEnd="}", subStart="{%", subEnd="%}", curSubName="MAIN";
        StringBuilder mainContent=new StringBuilder(), subContent=new StringBuilder();
        Map<String,String> result=new HashMap<>();
        boolean readingSubContent=false;
        //Read a line
        while((line=reader.readLine())!=null)
        {
            //If start read the sub template content
            if(line.contains(subStart))
            {
                curSubName=line.substring(line.indexOf(subStart)+subEnd.length(),line.lastIndexOf(":"));
                mainContent.append(varStart)
                        .append(curSubName)
                        .append(varEnd)
                        .append("\n");
                readingSubContent=true;
            }
            //If read the sub template content
            else if(readingSubContent)
            {
                //If end read the sub template content
                if(line.contains(subEnd))
                {
                    result.put(curSubName,subContent.toString());
                    subContent=new StringBuilder();
                    readingSubContent=false;
                }
                else
                {
                    subContent.append(line).append("\n");
                }
            }
            //If read the main content
            else
            {
                mainContent.append(line).append("\n");
            }
        }

        result.put("MAIN",mainContent.toString());
        return result;
    }

    /**
     * Load the template file
     *
     * @param file template file
     */
    CodeGenerator loadTemplate(File file);

    /**
     * @return generate content
     */
    String generate();

    /**
     * Save to the specified location
     *
     * @param file the file object for saved
     */
    default void saveTo(File file) throws IOException
    {
        //If the file is exist,delete it
        if(file.exists()) file.delete();
        //create a new file
        file.createNewFile();
        //write into the new file
        FileOutputStream outputStream=new FileOutputStream(file);
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
        writer.write(generate());
        writer.close();
    }
}