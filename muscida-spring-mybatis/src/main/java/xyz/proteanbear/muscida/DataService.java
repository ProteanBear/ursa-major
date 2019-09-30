package xyz.proteanbear.muscida;

import java.util.List;
import java.util.Map;

/**
 * The generic default business layer interface definition.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public interface DataService<
        Bean extends DefaultBean,
        Mapper extends DefaultDataMapper<Bean>>
{
    /**
     * @return Current data management objects
     */
    Mapper getMapper();

    /**
     * @return Default sort used when list does not set orderBy when querying data
     */
    String getDefaultOrderBy();

    /**
     * Query the data list of the specified conditions
     *
     * @param condition search condition
     * @return data list
     * @throws Exception sql exception
     */
    default List<Bean> list(Map<String,Object> condition) throws Exception
    {
        condition.put("orderBy",condition.containsKey("orderBy")
                ?condition.get("orderBy")
                //Fill in default sort
                :getDefaultOrderBy());
        return getMapper().list(condition);
    }

    /**
     * Query the total number of data under specified conditions
     *
     * @param condition search condition
     * @return data total number
     * @throws Exception sql exception
     */
    default Integer count(Map<String,Object> condition) throws Exception
    {
        return getMapper().count(condition);
    }

    /**
     * Insert a new data into the database
     *
     * @param data data information
     * @throws Exception sql exception
     */
    default Bean save(Bean data) throws Exception
    {
        getMapper().save(data);
        return get(data.getPrimaryKeyValue());
    }

    /**
     * Get details of a piece of data
     *
     * @param id primary key
     * @return data information
     * @throws Exception sql exception
     */
    default Bean get(Object id) throws Exception
    {
        if(id==null || "".equals(id))
        {
            throw new Exception("{PRIMARY_KEY_MUST_NOT_NULL}");
        }
        return getMapper().get(id);
    }

    /**
     * Delete the data of the specified primary key
     *
     * @param ids primary key array
     * @throws Exception sql exception
     */
    default void remove(String... ids) throws Exception
    {
        getMapper().remove(ids);
    }

    /**
     * Modify a piece of data related information
     *
     * @param data data information
     * @throws Exception sql exception
     */
    default Bean update(Bean data) throws Exception
    {
        getMapper().update(data);
        return get(data.getPrimaryKeyValue());
    }
}