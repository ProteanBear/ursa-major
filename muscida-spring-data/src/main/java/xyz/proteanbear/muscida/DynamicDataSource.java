package xyz.proteanbear.muscida;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Dynamic data source settings
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/27
 * @since jdk1.8,Spring4.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource
{
    /**
     * Log
     */
    public static final Logger logger=LoggerFactory.getLogger(DynamicDataSource.class);

    /**
     * @return Current data source identifier
     */
    @Override
    protected Object determineCurrentLookupKey()
    {
        return Holder.get();
    }

    /**
     * Dynamic data source holder; ThreadLocal guarantees thread safety
     *
     * @author ProteanBear
     * @version 1.0.0,2018/04/27
     * @since jdk1.8,Spring4.0
     */
    public static class Holder
    {
        /**
         * Use ThreadLocal to record the data source key of the current thread
         */
        static final ThreadLocal<String> holder=new ThreadLocal<>();

        /**
         * @param sourceKey Data Source ID Key Name
         */
        public static void set(String sourceKey)
        {
            logger.debug("Set the data source key is {}",sourceKey);
            holder.set(sourceKey);
        }

        /**
         * @return Get the current data source identifier key name
         */
        public static String get()
        {
            return holder.get();
        }
    }

    /**
     * Dynamic data source annotation annotation,
     * indicating the current data source
     *
     * @author ProteanBear
     * @version 1.0.0,2018/04/28
     * @since jdk1.8,Spring4.0
     */
    @Target({ElementType.TYPE,ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Set
    {
        //Data source name
        String value();
    }

    /**
     * Dynamic data source processing aspects
     *
     * @author ProteanBear
     * @version 1.0.0,2018/04/28
     * @since jdk1.8,Spring4.0
     */
    public static class Aspect
    {
        /**
         * Default set of prefixes from library method names
         */
        private static final String[] defaultSlaveMethodPrefix=new String[]{
                "list","count","query","find","get"
        };

        /**
         * Record all data source index key names
         */
        private String[] dataSourceKeys;

        /**
         * When the data source is separated from read and write,
         * the prefix set from the library method name
         */
        private String[] slaveMethodPrefix;

        /**
         * Perform before entering Service;
         * According to the annotation and the current calling method name,
         * select the data source and master-slave library to set
         *
         * @param joinPoint Point of entry
         */
        public void before(JoinPoint joinPoint)
        {
            //Current execution class
            Class targetClass=joinPoint.getTarget().getClass();
            //Current execution method
            Method method=((MethodSignature)joinPoint.getSignature()).getMethod();

            //Initialize from class
            String dataSourceKey=getSourceSet(targetClass,method);
            //Initialize from interface
            if(dataSourceKey==null)
            {
                for(Class targetInterface : targetClass.getInterfaces())
                {
                    dataSourceKey=getSourceSet(targetInterface,method);
                    if(dataSourceKey!=null) break;
                }
            }

            //Traverse all data sources to find the current service
            // (including master and slave)
            List<String> currentServers=new ArrayList<>();
            for(String key : dataSourceKeys)
            {
                if(key.startsWith(dataSourceKey+DataSourceStringSplitUtils.LINK_SERVER))
                {
                    currentServers.add(key);
                }
            }

            //When there is a master and a slave,
            //and the current is a slave, select a slave
            int index=0;
            if(currentServers.size()>1
                    && isSlaveMethod(method.getName()))
            {
                index=getSlaveIndex(currentServers);
            }

            //Mark the current library
            Holder.set(currentServers.get(index));
        }

        /**
         * @param serverNames All service names
         */
        public void setServerNames(String serverNames)
        {
            this.dataSourceKeys=DataSourceStringSplitUtils.splitDataServers(serverNames);
        }

        /**
         * @param slaveMethodPrefix When the data source is separated from read and write, the prefix set from the library method name
         */
        public void setSlaveMethodPrefix(String...slaveMethodPrefix)
        {
            this.slaveMethodPrefix=slaveMethodPrefix;
        }

        /**
         * @param targetClass Currently executing class
         * @param method      Current method
         * @return The key set in the data source annotation
         */
        private String getSourceSet(Class targetClass,Method method)
        {
            try
            {
                //First get an annotation from the method
                method=targetClass.getMethod(
                        method.getName(),
                        method.getParameterTypes()
                );
                if(method!=null && method.isAnnotationPresent(Set.class))
                {
                    return method.getAnnotation(Set.class).value();
                }

                //Get annotations from the class
                if(targetClass.isAnnotationPresent(Set.class))
                {
                    return ((Set)targetClass.getAnnotation(Set.class)).value();
                }
            }
            catch(NoSuchMethodException e)
            {
                logger.error(e.getMessage(),e);
            }
            return null;
        }

        /**
         * Verify the operation method from the library (ie read the library)
         *
         * @param methodName Method name
         * @return Return true if you need to manipulate the method from the library
         */
        private boolean isSlaveMethod(String methodName)
        {
            String[] methodPrefix=slaveMethodPrefix==null
                    ?defaultSlaveMethodPrefix
                    :slaveMethodPrefix;
            boolean isSlave=false;
            for(String prefix : methodPrefix)
            {
                if(methodName.startsWith(prefix))
                {
                    isSlave=true;
                    break;
                }
            }
            return isSlave;
        }

        /**
         * Get used index from library
         *
         * @param currentServers All current services
         * @return The index of the selected service in currentServers
         */
        private int getSlaveIndex(List<String> currentServers)
        {
            int index=1;

            //The number of libraries
            int slaveTotal=currentServers.size()-1;
            //Only one from the library, return directly
            if(slaveTotal==1) return index;

            //Random algorithm
            index=(new Random()).nextInt(slaveTotal-1)+1;

            return index;
        }
    }
}