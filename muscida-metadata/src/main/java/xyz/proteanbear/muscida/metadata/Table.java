package xyz.proteanbear.muscida.metadata;

import java.util.List;

/**
 * Table Information Structure
 *
 * @author ProteanBear
 * @version 1.00 2017/04/06
 * @since jdk1.4
 */
public class Table
{
    /**
     * Data table name
     */
    private String name;

    /**
     * Data table comment
     */
    private String comment;

    /**
     * The primary key
     */
    private String primaryKey;

    /**
     * The columns of data table
     */
    private List<Column> columnList;

    /**
     * Whether the table has field-associated foreign keys
     * (that is, many-to-one)
     */
    private boolean haveForeignKey=false;

    /**
     * Whether the table has foreign keys for other tables
     */
    private boolean beForeignTable=false;

    /**
     * Constructor
     *
     * @param name       the table name
     * @param comment    the table comment
     * @param primaryKey the primary key of the table
     * @param columnList the columns of data table
     */
    public Table(String name,String comment,String primaryKey,
                 List<Column> columnList)
    {
        this.name=name;
        this.comment=comment;
        this.primaryKey=primaryKey;
        this.columnList=columnList;
    }

    /**
     * Get the column searching by the name
     *
     * @param columnName the target name
     * @return the column.If not have,return null.
     */
    public Column getColumn(String columnName)
    {
        assert columnName!=null;
        Column result=null;
        if(columnList==null) return result;

        for(Column column:columnList)
        {
            if(column.getName().equalsIgnoreCase(columnName))
            {
                result=column;
                break;
            }
        }
        return result;
    }

    public String getName()
    {
        return name;
    }

    public String getComment()
    {
        return comment;
    }

    public List<Column> getColumnList()
    {
        return columnList;
    }

    public String getPrimaryKey()
    {
        return primaryKey;
    }

    public boolean isHaveForeignKey()
    {
        return haveForeignKey;
    }

    public Table setHaveForeignKey(boolean haveForeignKey)
    {
        this.haveForeignKey=haveForeignKey;
        return this;
    }

    public boolean isBeForeignTable()
    {
        return beForeignTable;
    }

    public Table setBeForeignTable(boolean beForeignTable)
    {
        this.beForeignTable=beForeignTable;
        return this;
    }
}
