package xyz.proteanbear.muscida;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Paging query request parameters and return content packaging
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public class Page
{
    /**
     * Receive query conditions via Map
     */
    private Map<String,Object> condition=new HashMap<>(2);

    /**
     * Page number parameter, starting from 1;
     * Default is 0, which means no paging
     */
    @Min(value=0)
    private Integer page=0;

    /**
     * The amount of data per page after paging;
     * Default is 0, which means no paging
     */
    @Min(value=0)
    private Integer size=0;

    /**
     * The starting index position of the data
     */
    private Integer startIndex;

    /**
     * The total number of data returned
     */
    private Integer total;

    /**
     * Returned data list
     */
    private List data;

    /**
     * @return The total number of data pages, no pages return empty
     */
    public Integer getTotalPage()
    {
        if(size==0) return null;
        return new Double(Math.ceil(total.doubleValue()/size.doubleValue())).intValue();
    }

    /**
     * @return data total
     */
    public Integer getTotal()
    {
        return total;
    }

    /**
     * @param total data total
     * @return object
     */
    public Page setTotal(Integer total)
    {
        this.total=total;
        return this;
    }

    /**
     * @return data list
     */
    public List getData()
    {
        return data;
    }

    /**
     * @param data data list
     * @return object
     */
    public Page setData(List data)
    {
        this.data=data;
        return this;
    }

    /**
     * @return Query conditions
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
     * @param condition Query conditions
     * @return current object
     */
    public Page setCondition(Map<String,Object> condition)
    {
        this.condition=condition;
        return this;
    }

    /**
     * @return current page number
     */
    public Integer getPage()
    {
        return page;
    }

    /**
     * @param page page number
     * @return current object
     */
    public Page setPage(Integer page)
    {
        this.page=page;
        return this;
    }

    /**
     * @return data volume per page
     */
    public Integer getSize()
    {
        return size;
    }

    /**
     * @param size data volume per page
     * @return current object
     */
    public Page setSize(Integer size)
    {
        this.size=size;
        return this;
    }

    /**
     * @return Data start index position
     */
    public Integer getStartIndex()
    {
        return startIndex;
    }

    /**
     * @param startIndex Data start index position
     * @return current object
     */
    public Page setStartIndex(Integer startIndex)
    {
        this.startIndex=startIndex;
        return this;
    }
}