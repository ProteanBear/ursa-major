package xyz.proteanbear.muscida.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Tools: Load Properties Properties file for easy access to properties.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/27
 * @since jdk1.8
 */
public class PropertiesUtils
{
    /**
     * log record
     */
    public static final Logger logger=LoggerFactory.getLogger(PropertiesUtils.class);

    /**
     * Property file cache
     */
    private static final Map<String,Properties> cachedPropertiesMap=new HashMap<>(4);

    /**
     * Preload all property configuration files under classpath.
     */
    static
    {
        logger.info("Start preload property configuration files!");

        //Get the path of the runtime classes
        String classPath=PropertiesUtils.class.getClassLoader().getResource("/").getPath();
        classPath=classPath.substring(0,classPath.lastIndexOf("classes")+"classes".length());
        File directory=new File(classPath);
        logger.debug("Classpath is {}",classPath);

        //Traverse all property configuration files in a folder
        if(directory.exists() && directory.isDirectory())
        {
            File[] files=directory.listFiles(
                    (dir,name) -> name.endsWith(".properties")
            );
            files=files!=null?files:new File[0];
            logger.debug("The number of .properties file is {}",files.length);

            for(File file : files)
            {
                try
                {
                    PropertiesUtils.load(file.getName());
                }
                catch(IOException e)
                {
                    logger.error("Preload property file({}) has a error:{}",file.getName(),e.getMessage());
                }
            }
        }

        logger.info("End preload,and cached properties number is {}",cachedPropertiesMap.size());
    }

    /**
     * Load the specified property file; obtained from the classpath.
     *
     * @param baseName file name, do not have to include the .properties suffix
     * @return property access object 
     */
    public static Properties load(String baseName) throws IOException
    {
        Properties properties=null;

        //File expansion name
        assert baseName!=null;
        baseName+=(baseName.contains(".properties")?"":".properties");

        //Read in cache
        if(cachedPropertiesMap.containsKey(baseName))
        {
            return cachedPropertiesMap.get(baseName);
        }

        //Get the file under classpath
        InputStream input=PropertiesUtils.class.getClassLoader().getResourceAsStream("/"+baseName);
        properties=new Properties();
        properties.load(input);
        input.close();

        //Save in cache
        cachedPropertiesMap.put(baseName,properties);

        return properties;
    }
}