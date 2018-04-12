package xyz.proteanbear.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数，用于传递Map
 *
 * @author ProteanBear
 * @version 1.0.0,2018-04-11
 * @since 1.0
 */
public class ParamMapPOJO
{
    //通过Map接收上传参数
    private Map<String,Object> condition=new HashMap<>(2);
    //接收页码参数，从1开始
    private Integer            page;
    //接收每页数量参数
    private Integer            size;

    /**
     * 执行页码计算
     */
    private void calculate()
    {
        if(this.page!=null&&this.size!=null){
            condition.put("size",this.size.intValue());
            condition.put("startIndex",(this.page.intValue()-1)*this.size.intValue());
        }
    }

    public Map<String,Object> getCondition()
    {
        return condition;
    }

    public void setCondition(Map<String,Object> condition)
    {
        this.condition=condition;
        if(condition!=null)
        {
            if(condition.containsKey("page"))
            {
                this.setPage(Integer.parseInt(condition.get("page")+""));
            }
            if(condition.containsKey("size"))
            {
                this.setSize(Integer.parseInt(condition.get("size")+""));
            }
        }
    }

    public Integer getPage()
    {
        return page;
    }

    public void setPage(Integer page)
    {
        this.page=page;
        this.calculate();
    }

    public Integer getSize()
    {
        return size;
    }

    public void setSize(Integer size)
    {
        this.size=size;
        this.calculate();
    }
}