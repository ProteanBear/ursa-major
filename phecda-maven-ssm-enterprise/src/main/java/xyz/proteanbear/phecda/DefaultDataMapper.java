package xyz.proteanbear.phecda;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 通用的默认数据管理对象接口定义，适用于MyBatis。
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public interface DefaultDataMapper<Bean>
{
    /**
     * 查询指定条件的数据列表
     *
     * @param condition 指定条件
     * @return 数据列表
     * @throws Exception SQL异常
     */
    List<Bean> list(@Param("condition") Map<String,Object> condition) throws Exception;

    /**
     * 查询指定条件下的数据总数
     *
     * @param condition 指定条件
     * @return 数据总数
     * @throws Exception SQL异常
     */
    Integer count(@Param("condition") Map<String,Object> condition) throws Exception;

    /**
     * 插入一条新的数据到数据库中
     *
     * @param data 数据信息
     * @throws Exception SQL异常
     */
    void save(Bean data) throws Exception;

    /**
     * 获取一条数据的详情信息
     *
     * @param id primary key
     * @return 数据信息
     * @throws Exception SQL异常
     */
    Bean get(Object id) throws Exception;

    /**
     * 删除指定主键的数据
     *
     * @param ids 主键数组
     * @throws Exception SQL异常
     */
    void remove(@Param("ids") String... ids) throws Exception;

    /**
     * 修改一条数据的相关信息
     *
     * @param data 数据信息
     * @throws Exception SQL异常
     */
    void update(Bean data) throws Exception;
}