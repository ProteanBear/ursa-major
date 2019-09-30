package xyz.proteanbear.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xyz.proteanbear.common.DataService;

import java.util.List;
import java.util.Map;

/**
 * 业务层实现
 *
 * @author ProteanBear
 * @version 1.0.0,2018-04-11
 * @since 1.0
 */
@Service("userService")
@Transactional(rollbackFor=Exception.class)
public class UserServiceImpl implements DataService<User>
{
    /**
     * 数据管理
     */
    @Autowired
    private UserMapper mapper;

    /**
     * 查询
     *
     * @param condition
     * @return
     * @throws Exception
     */
    public List<User> list(Map<String,Object> condition) throws Exception
    {
        condition.put("orderBy",condition.containsKey("orderBy")
                ?condition.get("orderBy")
                //填写默认排序
                :"");
        return mapper.list(condition);
    }

    /**
     * 总数
     *
     * @param condition
     * @return
     * @throws Exception
     */
    public Integer count(Map<String,Object> condition) throws Exception
    {
        return mapper.count(condition);
    }

    /**
     * 插入
     *
     * @param data
     * @throws Exception
     */
    public User save(User data) throws Exception
    {
        //数据验证

        //设置默认值

        //添加数据并返回最新
        mapper.save(data);
        return get(data.getUser());
    }

    /**
     * 详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    public User get(String id) throws Exception
    {
        if(StringUtils.isEmpty(id)) throw new Exception("主键不能为空！");
        return mapper.get(id);
    }

    /**
     * 删除
     *
     * @param ids
     * @throws Exception
     */
    public void remove(String[] ids) throws Exception
    {
        mapper.remove(ids);
    }

    /**
     * 更新
     *
     * @param data
     * @throws Exception
     */
    public User update(User data) throws Exception
    {
        //数据验证

        //设置默认值

        //添加数据并返回最新
        mapper.update(data);
        return get(data.getUser());
    }
}