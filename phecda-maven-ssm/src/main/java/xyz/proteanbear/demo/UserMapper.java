package xyz.proteanbear.demo;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 数据层操作
 *
 * @author ProteanBear
 * @version 1.0.0,2018-04-11
 * @since 1.0
 */
@Repository
public interface UserMapper
{
    /**
     * 查询
     *
     * @param condition
     * @return
     * @throws Exception
     */
    List<User> list(@Param("condition") Map<String,Object> condition) throws Exception;

    /**
     * 总数
     *
     * @param condition
     * @return
     * @throws Exception
     */
    Integer count(@Param("condition") Map<String,Object> condition) throws Exception;

    /**
     * 插入
     *
     * @param data
     * @throws Exception
     */
    void save(User data) throws Exception;

    /**
     * 详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    User get(String id) throws Exception;

    /**
     * 删除
     *
     * @param ids
     * @throws Exception
     */
    void remove(@Param("ids") String[] ids) throws Exception;

    /**
     * 更新
     *
     * @param data
     * @throws Exception
     */
    void update(User data) throws Exception;
}