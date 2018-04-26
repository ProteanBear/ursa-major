package xyz.proteanbear.phecda.rest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页查询请求参数及返回内容包装。
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public class Page
{
    /**
     * 通过Map接收查询条件
     */
    private Map<String,Object> condition=new HashMap<>(2);

    /**
     * 页码参数，从1开始;
     * 默认为0，表示不分页
     */
    @Min(value=0)
    private Integer page=0;

    /**
     * 分页后每页的数据量;
     * 默认为0，表示不分页
     */
    @Min(value=0)
    private Integer size=0;

    /**
     * 数据的开始索引位置
     */
    private Integer startIndex;

    /**
     * 返回的数据总数
     */
    private Integer total;

    /**
     * 返回的数据列表
     */
    private List data;

    /**
     * @return 数据总页数，不分页返回为空
     */
    public Integer getTotalPage()
    {
        if(size==0) return null;
        return new Double(Math.ceil(total.doubleValue()/size.doubleValue())).intValue();
    }

    /**
     * @return 数据总量
     */
    public Integer getTotal()
    {
        return total;
    }

    /**
     * @param total 数据总量
     * @return 当前对象
     */
    public Page setTotal(Integer total)
    {
        this.total=total;
        return this;
    }

    /**
     * @return 数据列表
     */
    public List getData()
    {
        return data;
    }

    /**
     * @param data 数据列表
     * @return 当前对象
     */
    public Page setData(List data)
    {
        this.data=data;
        return this;
    }

    /**
     * @return 查询条件
     */
    public Map<String,Object> getCondition()
    {
        //Perform page number calculation
        if(size>0
                && !condition.containsKey("size"))
        {
            //Set page default is 1
            page=(page==0?1:page);
            //calculate the start index
            startIndex=(startIndex!=null)?startIndex:((this.page-1)*this.size);

            condition.put("size",size);
            condition.put("startIndex",startIndex);
        }

        return condition;
    }

    /**
     * @param condition 查询条件
     * @return 当前对象
     */
    public Page setCondition(Map<String,Object> condition)
    {
        this.condition=condition;
        return this;
    }

    /**
     * @return 当前页码
     */
    public Integer getPage()
    {
        return page;
    }

    /**
     * @param page 页码
     * @return 当前对象
     */
    public Page setPage(Integer page)
    {
        this.page=page;
        return this;
    }

    /**
     * @return 每页数据量
     */
    public Integer getSize()
    {
        return size;
    }

    /**
     * @param size 每页数据量
     * @return 当前对象
     */
    public Page setSize(Integer size)
    {
        this.size=size;
        return this;
    }

    /**
     * @return 数据开始索引位置
     */
    public Integer getStartIndex()
    {
        return startIndex;
    }

    /**
     * @param startIndex 数据开始索引位置
     * @return 当前对象
     */
    public Page setStartIndex(Integer startIndex)
    {
        this.startIndex=startIndex;
        return this;
    }
}