package xyz.proteanbear.common;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 业务层接口
 *
 * @author ProteanBear
 * @version 1.0.0,2018-04-11
 * @since 1.0
 */
public interface DataService<T>
{
    /**
     * 查询
     *
     * @param condition
     * @return
     * @throws Exception
     */
    List<T> list(Map<String,Object> condition) throws Exception;

    /**
     * 总数
     *
     * @param condition
     * @return
     * @throws Exception
     */
    Integer count(Map<String,Object> condition) throws Exception;

    /**
     * 插入
     *
     * @param data
     * @throws Exception
     */
    T save(T data) throws Exception;

    /**
     * 详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    T get(String id) throws Exception;

    /**
     * 删除
     *
     * @param ids
     * @throws Exception
     */
    void remove(String[] ids) throws Exception;

    /**
     * 更新
     *
     * @param data
     * @throws Exception
     */
    T update(T data) throws Exception;
}