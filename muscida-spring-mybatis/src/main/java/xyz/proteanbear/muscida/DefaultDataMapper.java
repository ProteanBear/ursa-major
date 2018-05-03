package xyz.proteanbear.muscida;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * The universal default data management object interface definition applies to MyBatis.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public interface DefaultDataMapper<Bean>
{
    /**
     * Query the data list of the specified conditions
     *
     * @param condition Specified conditions
     * @return data list
     * @throws Exception sql exception
     */
    List<Bean> list(@Param("condition") Map<String,Object> condition) throws Exception;

    /**
     * Query the total number of data under specified conditions
     *
     * @param condition Specified conditions
     * @return data total number
     * @throws Exception sql exception
     */
    Integer count(@Param("condition") Map<String,Object> condition) throws Exception;

    /**
     * Insert a new data into the database
     *
     * @param data data information
     * @throws Exception sql exception
     */
    void save(Bean data) throws Exception;

    /**
     * Get details of a piece of data
     *
     * @param id primary key
     * @return data information
     * @throws Exception sql exception
     */
    Bean get(Object id) throws Exception;

    /**
     * Delete the data of the specified primary key
     *
     * @param ids primary key array
     * @throws Exception sql exception
     */
    void remove(@Param("ids") String... ids) throws Exception;

    /**
     * Modify a piece of data related information
     *
     * @param data data information
     * @throws Exception sql exception
     */
    void update(Bean data) throws Exception;
}