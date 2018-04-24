package xyz.proteanbear.phecda.rest;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Paging request parameters and response data.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public class Page
{
    /**
     * Receive condition parameters through Map
     */
    private Map<String,Object> condition=new HashMap<>(2);

    /**
     * Page number parameter, starting from 1;
     * And default is 0,main no paging
     */
    @Min(value=0)
    private Integer page=0;

    /**
     * Number of pages per page after the parameter;
     * 0 means no paging
     */
    @Min(value=0)
    private Integer size=0;

    /**
     * Data start index position
     */
    private Integer startIndex;

    /**
     * Record the data total number
     */
    private Integer total;

    /**
     * Data result
     */
    private List data;

    /**
     * @return total page
     */
    public Integer getTotalPage()
    {
        if(size.intValue()==0) return null;
        return new Double(Math.ceil(total.doubleValue()/size.doubleValue())).intValue();
    }

    public Integer getTotal()
    {
        return total;
    }

    public Page setTotal(Integer total)
    {
        this.total=total;
        return this;
    }

    public List getData()
    {
        return data;
    }

    public Page setData(List data)
    {
        this.data=data;
        return this;
    }

    public Map<String,Object> getCondition()
    {
        //Perform page number calculation
        if(size.intValue()>0
                &&!condition.containsKey("size"))
        {
            //Set page default is 1
            page=(page.intValue()==0?1:page);
            //calculate the start index
            startIndex=(startIndex==null)
                    ?((this.page.intValue()-1)*this.size.intValue())
                    :startIndex;

            condition.put("size",size.intValue());
            condition.put("startIndex",startIndex.intValue());
        }

        return condition;
    }

    public Page setCondition(Map<String,Object> condition)
    {
        this.condition=condition;
        return this;
    }

    public Integer getPage()
    {
        return page;
    }

    public Page setPage(Integer page)
    {
        this.page=page;
        return this;
    }

    public Integer getSize()
    {
        return size;
    }

    public Page setSize(Integer size)
    {
        this.size=size;
        return this;
    }

    public Integer getStartIndex()
    {
        return startIndex;
    }

    public Page setStartIndex(Integer startIndex)
    {
        this.startIndex=startIndex;
        return this;
    }
}