package xyz.proteanbear.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.proteanbear.common.DataService;
import xyz.proteanbear.configuration.ParamMapPOJO;
import xyz.proteanbear.configuration.ResponseVO;

/**
 * 接口
 *
 * @author ProteanBear
 * @version 1.0.0,2018-04-11
 * @since 1.0
 */
@Controller
@RequestMapping("/demo")
public class UserController
{
    /**
     * 业务接口
     */
    @Autowired
    @Qualifier("userService")
    private DataService service;

    /**
     * 查询
     *
     * @param paramPOJO
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/user",method=RequestMethod.GET)
    @ResponseBody
    public ResponseVO list(ParamMapPOJO paramPOJO) throws Exception
    {
        //参数验证

        return ResponseVO.success(
                service.list(paramPOJO.getCondition()),
                service.count(paramPOJO.getCondition()));
    }

    /**
     * 插入
     *
     * @param data
     * @throws Exception
     */
    @RequestMapping(value="/user",method=RequestMethod.POST)
    @ResponseBody
    public ResponseVO save(User data) throws Exception
    {
        throw new Exception("Not Support!");
    }

    /**
     * 详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/user/${id}",method=RequestMethod.GET)
    @ResponseBody
    public ResponseVO get(@PathVariable("id") String id) throws Exception
    {
        throw new Exception("Not Support!");
    }

    /**
     * 删除
     *
     * @param ids
     * @throws Exception
     */
    @RequestMapping(value="/user/${idArray}",method=RequestMethod.DELETE)
    @ResponseBody
    public ResponseVO remove(@PathVariable("idArray") String[] ids) throws Exception
    {
        throw new Exception("Not Support!");
    }

    /**
     * 更新
     *
     * @param data
     * @throws Exception
     */
    @RequestMapping(value="/user/${id}",method=RequestMethod.PUT)
    @ResponseBody
    public ResponseVO update(@PathVariable("id") String id
            ,User data)
            throws Exception
    {
        throw new Exception("Not Support!");
    }
}