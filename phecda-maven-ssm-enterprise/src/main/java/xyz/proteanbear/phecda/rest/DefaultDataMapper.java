package xyz.proteanbear.phecda.rest;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Universal default data layer interface definition.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public interface DefaultDataMapper<Bean>
{
    /**
     * Query data list according to conditions
     *
     * @param condition the conditions
     * @return data list
     * @throws Exception the sql exception
     */
    List<Bean> list(@Param("condition") Map<String,Object> condition) throws Exception;

    /**
     * Query data total number according to conditions
     *
     * @param condition the conditions
     * @return data total number
     * @throws Exception the sql exception
     */
    Integer count(@Param("condition") Map<String,Object> condition) throws Exception;

    /**
     * Save the data into the database.
     *
     * @param data data object
     * @throws Exception the sql exception
     */
    void save(Bean data) throws Exception;

    /**
     * Get data details.
     *
     * @param id primary key
     * @return data object
     * @throws Exception the sql exception
     */
    Bean get(Object id) throws Exception;

    /**
     * Remove data from database
     *
     * @param ids primary key array
     * @throws Exception
     */
    void remove(@Param("ids") String... ids) throws Exception;

    /**
     * Update data
     *
     * @param data object
     * @throws Exception the sql exception
     */
    void update(Bean data) throws Exception;
}