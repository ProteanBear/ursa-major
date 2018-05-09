package xyz.proteanbear.muscida.metadata;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Foreign key table related attributes
 *
 * @author ProteanBear
 * @version 1.00 2017/04/06
 * @since jdk1.4
 */
public class Foreign
{
    /**
     * Record the name of the foreign key constraint
     */
    private String foreignName;

    /**
     * Primary key table name being imported
     */
    private String pkTableName;

    /**
     * Primary key column name being imported
     */
    private String pkColumnName;

    /**
     * Foreign key column name
     */
    private String fkColumnName;

    /**
     * The related structure of the primary key table
     */
    private Table pkTable;

    /**
     * Constructor
     *
     * @param foreignName  the name of the foreign key constraint
     * @param pkTableName  primary key table name being imported
     * @param pkColumnName Primary key column name being imported
     * @param fkColumnName foreign key column name
     */
    public Foreign(String foreignName,String pkTableName,String pkColumnName,String fkColumnName)
    {
        this.foreignName=foreignName;
        this.pkTableName=pkTableName;
        this.pkColumnName=pkColumnName;
        this.fkColumnName=fkColumnName;
    }

    /**
     * Constructor
     *
     * @param resultSet the sql result set
     */
    public Foreign(ResultSet resultSet) throws SQLException
    {
        this(
                resultSet.getString("FK_NAME"),
                resultSet.getString("PKTABLE_NAME"),
                resultSet.getString("PKCOLUMN_NAME"),
                resultSet.getString("FKCOLUMN_NAME")
        );
    }

    public String getPkTableName()
    {
        return pkTableName;
    }

    public Foreign setPkTableName(String pkTableName)
    {
        this.pkTableName=pkTableName;
        return this;
    }

    public String getPkColumnName()
    {
        return pkColumnName;
    }

    public Foreign setPkColumnName(String pkColumnName)
    {
        this.pkColumnName=pkColumnName;
        return this;
    }

    public String getFkColumnName()
    {
        return fkColumnName;
    }

    public Foreign setFkColumnName(String fkColumnName)
    {
        this.fkColumnName=fkColumnName;
        return this;
    }

    public Table getPkTable()
    {
        return pkTable;
    }

    public Foreign setPkTable(Table pkTable)
    {
        this.pkTable=pkTable;
        return this;
    }

    public String getForeignName()
    {
        return foreignName;
    }

    public Foreign setForeignName(String foreignName)
    {
        this.foreignName=foreignName;
        return this;
    }
}
