package xyz.proteanbear.phecda;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 通用的默认实体类接口定义。
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public interface DefaultBean
{
    /**
     * @return 当前数据对象的主键值
     */
    @JsonIgnore
    Object getPrimaryKeyValue();
}