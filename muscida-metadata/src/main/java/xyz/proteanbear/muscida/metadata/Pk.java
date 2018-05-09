package xyz.proteanbear.muscida.metadata;

/**
 * The related attributes of the associated primary key table
 * in the foreign key
 *
 * @author ProteanBear
 * @version 1.00 2017/04/06
 * @since jdk1.4
 */
public class Pk
{
    /**
     * Foreign key table name
     */
    private String fkTableName;

    /**
     * Foreign key column name
     */
    private String fkColumnName;

    /**
     * Primary key column name being imported
     */
    private String pkColumnName;

    /**
     * Foreign key table structure
     */
    private Table fkTable;

    /**
     * Constructor
     *
     * @param fkTableName  foreign key table name
     * @param fkColumnName foreign key column name
     * @param pkColumnName primary key column name being imported
     */
    public Pk(String fkTableName,String fkColumnName,String pkColumnName)
    {
        this.fkTableName=fkTableName;
        this.fkColumnName=fkColumnName;
        this.pkColumnName=pkColumnName;
    }

    /**
     * Constructor
     *
     * @param fkTableName  foreign key table name
     * @param fkColumnName foreign key column name
     * @param pkColumnName primary key column name being imported
     * @param fkTable      foreign key table structure
     */
    public Pk(String fkTableName,String fkColumnName,String pkColumnName,
              Table fkTable)
    {
        this.fkTableName=fkTableName;
        this.fkColumnName=fkColumnName;
        this.pkColumnName=pkColumnName;
        this.fkTable=fkTable;
    }

    public String getFkTableName()
    {
        return fkTableName;
    }

    public Pk setFkTableName(String fkTableName)
    {
        this.fkTableName=fkTableName;
        return this;
    }

    public String getFkColumnName()
    {
        return fkColumnName;
    }

    public Pk setFkColumnName(String fkColumnName)
    {
        this.fkColumnName=fkColumnName;
        return this;
    }

    public String getPkColumnName()
    {
        return pkColumnName;
    }

    public Pk setPkColumnName(String pkColumnName)
    {
        this.pkColumnName=pkColumnName;
        return this;
    }

    public Table getFkTable()
    {
        return fkTable;
    }

    public Pk setFkTable(Table fkTable)
    {
        this.fkTable=fkTable;
        return this;
    }
}
