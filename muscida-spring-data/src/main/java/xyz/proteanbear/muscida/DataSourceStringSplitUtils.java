package xyz.proteanbear.muscida;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tool: Disassemble some text in the attribute
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/28
 * @since jdk1.8
 */
public class DataSourceStringSplitUtils
{
    /**
     * Separating multiple data sources
     */
    private static final String SPLIT_SERVER=",";

    /**
     * Separating the master and slave services
     */
    private static final String SPLIT_SERVER_MASTER=":";

    /**
     * Separating Master-slave service in data sources
     */
    private static final String SPLIT_MASTER_SLAVE="|";

    /**
     * Separate connectors in the data source
     */
    public static final String LINK_SERVER=".";

    /**
     * Decompose the data source configuration string
     * to the final data source index key name
     *
     * @param serverNames eg. mysql:master|slave,oracle:master|slave
     * @return eg. ['mysql.master','mysql.slave','oracle.master','oracle.slave']
     */
    public static String[] splitDataServers(String serverNames)
    {
        String[] serverNameArray=serverNames.split(SPLIT_SERVER),names;
        List<String> result=new ArrayList<>();
        for(String serverName : serverNameArray)
        {
            names=splitDataMasterAndSlave(serverName);
            result.addAll(Arrays.asList(names));
        }
        return result.toArray(new String[]{});
    }

    /**
     * Decompose the data source configuration string
     * to the final data source index key name
     *
     * @param serverName eg. mysql:master|slave
     * @return eg. ['mysql.master','mysql.slave','oracle.master','oracle.slave']
     */
    public static String[] splitDataMasterAndSlave(String serverName)
    {
        //Whether the current database uses read-write separation
        boolean sentinel=serverName.contains(SPLIT_SERVER_MASTER)
                && serverName.contains(SPLIT_MASTER_SLAVE);
        if(!sentinel) return new String[]{serverName};

        //Dismantling the master-slave name
        int position=serverName.indexOf(SPLIT_SERVER_MASTER);
        String server=serverName.substring(0,position);
        String[] names=serverName.substring(position+1).split(SPLIT_MASTER_SLAVE);
        String[] result=new String[names.length];
        for(int i=0;i<names.length;i++) result[i]=server+LINK_SERVER+names[i];
        return result;
    }
}