package xyz.proteanbear.phecda.rest;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xyz.proteanbear.phecda.exception.PhecdaException;
import xyz.proteanbear.phecda.exception.ResponseCode;

import java.util.List;
import java.util.Map;

/**
 * Universal business implementation layer interface definition.
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
     * @return Current data mapper implementation
     */
    Mapper getMapper();

    /**
     * @return return default orderBy setting using when condition is not include it.
     */
    String getDefaultOrderBy();

    /**
     * Query data list according to conditions
     *
     * @param condition the conditions
     * @return data list
     * @throws Exception the sql exception
     */
    default List<Bean> list(Map<String,Object> condition) throws Exception
    {
        condition.put("orderBy",condition.containsKey("orderBy")
                ?condition.get("orderBy")
                //填写默认排序
                :getDefaultOrderBy());
        return getMapper().list(condition);
    }

    /**
     * Query data total number according to conditions
     *
     * @param condition the conditions
     * @return data total number
     * @throws Exception the sql exception
     */
    default Integer count(Map<String,Object> condition) throws Exception
    {
        return getMapper().count(condition);
    }

    /**
     * Save the data into the database.
     *
     * @param data data object
     * @throws Exception the sql exception
     */
    @Transactional(rollbackFor=Exception.class)
    default Bean save(Bean data) throws Exception
    {
        getMapper().save(data);
        return get(data.getPrimaryKeyValue());
    }

    /**
     * Get data details.
     *
     * @param id primary key
     * @return data object
     * @throws Exception the sql exception
     */
    default Bean get(Object id) throws Exception
    {
        if(StringUtils.isEmpty(id)) throw new PhecdaException(ResponseCode.PRIMARY_KEY_MUST_NOT_NULL);
        return getMapper().get(id);
    }

    /**
     * Remove data from database
     *
     * @param ids primary key array
     * @throws Exception
     */
    @Transactional(rollbackFor=Exception.class)
    default void remove(String... ids) throws Exception
    {
        getMapper().remove(ids);
    }

    /**
     * Update data
     *
     * @param data object
     * @throws Exception the sql exception
     */
    @Transactional(rollbackFor=Exception.class)
    default Bean update(Bean data) throws Exception
    {
        getMapper().update(data);
        return get(data.getPrimaryKeyValue());
    }
}