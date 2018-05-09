package xyz.proteanbear.muscida;

import xyz.proteanbear.muscida.metadata.Column;
import xyz.proteanbear.muscida.metadata.Foreign;
import xyz.proteanbear.muscida.metadata.Pk;
import xyz.proteanbear.muscida.metadata.Table;

import java.sql.*;
import java.util.*;

/**
 * Database metadata loader
 *
 * @author proteanBear
 * @version 1.00 2018/04/19
 * @since jdk1.4
 */
public class MetaDataLoader
{
    /**
     * The supported database type
     */
    public enum DatabaseType
    {
        MYSQL(
                "MYSQL",
                "show table status where name not like 'schema_%'"
        ),
        ORACLE(
                "ORACLE",
                "SELECT table.TABLE_NAME AS 'name',comment.COMMENTS AS 'comment' FROM USER_TABLES table,USER_TAB_COMMENTS comment WHERE table.TABLE_NAME=comment.TABLE_NAME"
        )
        ;

        /**
         * The database name（Uppercase）
         */
        private String name;

        /**
         * The sql for getting metadata
         */
        private String metaDataSql;

        /**
         * Constructor
         *
         * @param name        the database name（Uppercase）
         * @param metaDataSql the sql for getting metadata
         */
        DatabaseType(String name,String metaDataSql)
        {
            this.name=name;
            this.metaDataSql=metaDataSql;
        }

        /**
         * @return the database type information
         */
        @Override
        public String toString()
        {
            return name;
        }

        /**
         * Check current database type is same as other
         * @param other other database type
         * @return If the database name is equal,return true
         */
        public boolean equels(DatabaseType other)
        {
            return getName().equals(other.getName());
        }

        /**
         * @return the database type name
         */
        public String getName()
        {
            return name;
        }

        /**
         * @return the sql for getting metadata
         */
        public String getMetaDataSql()
        {
            return metaDataSql;
        }
    }

    /**
     * The database connection
     */
    private Connection connector;

    /**
     * Constructor
     *
     * @param connector the database connector
     */
    public MetaDataLoader(Connection connector)
    {
        assert connector!=null;
        this.connector=connector;
    }

    /**
     * Get the metadata of the data table with the specified prefix
     *
     * @return the table name mapping to metadata
     */
    public Map<String,Table> tables() throws SQLException
    {
        System.out.println("MetaDataLoader:Start reading the entire data table structure!");

        //the database information
        DatabaseMetaData metaData=connector.getMetaData();
        String catalog=connector.getCatalog();
        
        //database type
        String database=metaData.getDatabaseProductName().toUpperCase();
        DatabaseType databaseType=DatabaseType.valueOf(database);
        String schema=metaData.getSchemaTerm();

        //Traverse the query all prefixes
        ResultSet resultSet, primaryResultSet, keysResultSet, columnResultSet;
        String tableName, tableComment, tablePrimaryKey;
        List<Foreign> foreignList;
        List<Column> columnList;
        Map<String,Table> tableMap=new LinkedHashMap<>();
        Map<String,List<Foreign>> foreignMap=new HashMap<>();
        Table table, foreignTable;

        //Get the table list with the prefix
        Statement statement=connector.createStatement();
        resultSet=statement.executeQuery(databaseType.getMetaDataSql());
        while(resultSet.next())
        {
            //current table name
            tableName=resultSet.getString("name");
            //current table comment
            tableComment=resultSet.getString("comment");

            //current table primary key
            primaryResultSet=metaData.getPrimaryKeys(catalog,schema,tableName);
            tablePrimaryKey=(primaryResultSet.next()
                    ?primaryResultSet.getString("COLUMN_NAME")
                    :"");

            //current table foreign key
            keysResultSet=metaData.getImportedKeys(catalog,schema,tableName);
            foreignList=new ArrayList<>();
            while(keysResultSet.next()) foreignList.add(new Foreign(keysResultSet));
            if(!foreignList.isEmpty()) foreignMap.put(tableName,foreignList);

            //current table columns
            columnResultSet=metaData.getColumns(catalog,schema,tableName,"");
            columnList=new ArrayList<>();
            while(columnResultSet.next()) columnList.add(new Column(columnResultSet));

            //add table information
            table=new Table(tableName,tableComment,tablePrimaryKey,columnList);
            tableMap.put(tableName,table);
        }

        //Set the properties for the foreign
        Column column;
        for(String curTableName : foreignMap.keySet())
        {
            //Get the table object
            table=tableMap.get(curTableName);
            table.setHaveForeignKey(true);

            //Traversing the list of foreign keys at current table
            for(Foreign foreign : foreignMap.get(curTableName))
            {
                foreignTable=tableMap.get(foreign.getPkTableName());
                foreignTable.setBeForeignTable(true);
                //Set the foreign key corresponding table structure
                foreign.setPkTable(foreignTable);
                //Set the foreign key columns in all columns of the current table
                table.getColumn(foreign.getFkColumnName()).setForeign(foreign);
                //Set the foreign key mapping table field
                //and associate it with the current table
                column=foreignTable.getColumn(foreign.getPkColumnName());
                column.getPkList().add(
                        new Pk(
                                curTableName,
                                foreign.getFkColumnName(),
                                column.getName(),
                                table
                        )
                );
            }
        }
        statement.close();
        return tableMap;
    }
}