package xyz.proteanbear.phecda;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 通用的默认业务层接口定义。
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
     * @return 当前的数据管理对象
     */
    Mapper getMapper();

    /**
     * @return list查询数据时未设置orderBy的情况下使用的默认排序
     */
    String getDefaultOrderBy();

    /**
     * 查询指定条件的数据列表
     *
     * @param condition 查询条件
     * @return 数据列表
     * @throws Exception SQL异常
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
     * 查询指定条件下的数据总数
     *
     * @param condition 执行条件
     * @return 数据总数
     * @throws Exception SQL异常
     */
    default Integer count(Map<String,Object> condition) throws Exception
    {
        return getMapper().count(condition);
    }

    /**
     * 插入一条新的数据到数据库中
     *
     * @param data 数据对象
     * @throws Exception SQL异常
     */
    @Transactional(rollbackFor=Exception.class)
    default Bean save(Bean data) throws Exception
    {
        getMapper().save(data);
        return get(data.getPrimaryKeyValue());
    }

    /**
     * 获取一条数据的详情信息
     *
     * @param id 主键
     * @return 数据详情信息
     * @throws Exception SQL异常
     */
    default Bean get(Object id) throws Exception
    {
        if(StringUtils.isEmpty(id)) throw new PhecdaException(ResponseCode.PRIMARY_KEY_MUST_NOT_NULL);
        return getMapper().get(id);
    }

    /**
     * 删除指定主键的数据
     *
     * @param ids 主键数组
     * @throws Exception SQL异常
     */
    @Transactional(rollbackFor=Exception.class)
    default void remove(String... ids) throws Exception
    {
        getMapper().remove(ids);
    }

    /**
     * 修改一条数据的相关信息
     *
     * @param data 数据内容
     * @throws Exception SQL异常
     */
    @Transactional(rollbackFor=Exception.class)
    default Bean update(Bean data) throws Exception
    {
        getMapper().update(data);
        return get(data.getPrimaryKeyValue());
    }
}