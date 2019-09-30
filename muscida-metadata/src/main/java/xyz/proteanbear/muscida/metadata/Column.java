package xyz.proteanbear.muscida.metadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The column information of data table
 *
 * @author ProteanBear
 * @version 1.00 2017/04/06
 * @since jdk1.4
 */
public class Column
{
    /**
     * The column name
     */
    private String name;

    /**
     * The column type
     */
    private String type;

    /**
     * The column comment
     */
    private String comment;

    /**
     * Table field as a table attribute corresponding to a foreign key
     */
    private Foreign foreign;

    /**
     * Table fields are related attributes as foreign keys in other tables
     */
    private List<Pk> pkList;

    /**
     * Constructor
     *
     * @param name    the column name
     * @param type    the column type
     * @param comment the column comment
     */
    public Column(String name,String type,String comment)
    {
        this.name=name;
        this.type=type.toLowerCase();
        this.comment=comment;
        this.pkList=new ArrayList<>();
    }

    /**
     * Constructor
     *
     * @param resultSet the sql result set
     */
    public Column(ResultSet resultSet) throws SQLException
    {
        this(
                resultSet.getString("COLUMN_NAME"),
                resultSet.getString("TYPE_NAME"),
                resultSet.getString("REMARKS")
        );
    }

    /**
     * Get the read method in the ResultSet
     * corresponding to the current data type
     *
     * @return the get method of the ResultSet object
     */
    public String methodNameForResultSet()
    {
        String result="get";
        switch(this.type)
        {
            case "int":
            case "double":
                result+=this.type.replaceFirst("[a-z]",(this.type.substring(0,1).toUpperCase()));
                break;
            case "varchar":
            case "varchar2":
            case "char":
            case "text":
                result+="String";
                break;
            default:
                result+="Object";
        }
        return result;
    }

    public String getName()
    {
        return name;
    }

    public Column setName(String name)
    {
        this.name=name;
        return this;
    }

    public String getType()
    {
        return type;
    }

    public Column setType(String type)
    {
        this.type=type;
        return this;
    }

    public String getComment()
    {
        return comment;
    }

    public Column setComment(String comment)
    {
        this.comment=comment;
        return this;
    }

    public Foreign getForeign()
    {
        return foreign;
    }

    public Column setForeign(Foreign foreign)
    {
        this.foreign=foreign;
        return this;
    }

    public List<Pk> getPkList()
    {
        return pkList;
    }
}
