package xyz.proteanbear.muscida.generator;

import xyz.proteanbear.muscida.MetaDataLoader;
import xyz.proteanbear.muscida.metadata.Table;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The code generator for MyBatis,includes xml,bean,mapper,service and controller
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/04
 * @since jdk1.8
 */
public class MyBatisCodeGenerator implements CodeGenerator
{
    public enum Type
    {
        MapperXml("Mapper.xml","DataMapperXml.txt"),
        Mapper("Mapper.java","DataMapper.txt"),
        Bean(".java","DataBean.txt"),
        ServiceImpl("ServiceImpl.java","DataServiceImpl.txt"),
        Controller("Controller.java","DataController.txt");

        /**
         * a file suffix
         */
        private String suffix;

        /**
         * template file name
         */
        private String templateFileName;

        /**
         * @param suffix           a file suffix
         * @param templateFileName template file name
         */
        Type(String suffix,String templateFileName)
        {
            this.suffix=suffix;
        }

        /**
         * @return a file suffix
         */
        public String getSuffix()
        {
            return suffix;
        }

        /**
         * @return template file name
         */
        public String getTemplateFileName()
        {
            return templateFileName;
        }
    }

    /**
     * a database connection
     */
    private Connection connector;

    /**
     * Table name filter pattern setting
     */
    private String tablePattern=".*";

    /**
     * Template Map,eg. Mapper~>(MAIN~>'content')
     */
    private Map<String,Map<String,String>> templateMap=new HashMap<>();

    /**
     * The generated content map,eg.package~>(fileName~>'content')
     */
    private Map<String,Map<String,StringBuilder>> contentMap=new HashMap<>();

    /**
     * Constructor
     *
     * @param connector a database connection
     */
    public MyBatisCodeGenerator(Connection connector)
    {
        this.connector=connector;
    }

    /**
     * Load the template file
     *
     * @param directory template file
     */
    @Override
    public CodeGenerator loadTemplate(File directory)
    {
        if(!directory.isDirectory())
        {
            (new IOException("The file parameter must be a directory!")).printStackTrace();
        }

        String rootPath=directory.getAbsolutePath()+File.separator;
        File curTemplateFile;
        //Load a template
        for(Type type : Type.values())
        {
            try
            {
                curTemplateFile=new File(rootPath+type.getTemplateFileName());
                templateMap.put(
                        type.getSuffix(),
                        CodeGenerator.catchTemplate(curTemplateFile)
                );
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return this;
    }

    /**
     * @return generate content
     */
    @Override
    public String generate()
    {
        try
        {
            //Load all the tables of the database
            Map<String,Table> tables=new MetaDataLoader(connector).tables();

            //Traverse all data tables
            Table curTable;
            Map<String,String> variable=new HashMap<>();
            for(String tableName : tables.keySet())
            {
                if(!matchPattern(tableName)) continue;

                //Get the metadata of a table
                curTable=tables.get(tableName);

                //Generate the replacement content map
                variable.clear();
                variableForCurrentTable(curTable,variable);

                //
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Save to the specified location
     *
     * @param file the file object for saved
     */
    @Override
    public void saveTo(File file) throws IOException
    {

    }

    /**
     * @param tablePattern Table name filter pattern setting
     */
    public MyBatisCodeGenerator setTablePattern(String tablePattern)
    {
        this.tablePattern=tablePattern;
        return this;
    }

    /**
     * @param tableName current table name
     * @return If 'tablePatter' is matching 'tableName',return true
     */
    private boolean matchPattern(String tableName)
    {
        return tablePattern.contains("*")
                ?(Pattern.matches(tablePattern,tableName))
                :(tablePattern.equalsIgnoreCase(tableName));
    }

    /**
     * Generate the variable map of current table for the replacement
     *
     * @param table the metadata of a table
     * @param map   the variable map
     */
    private void variableForCurrentTable(Table table,Map<String,String> map)
    {
    }
}