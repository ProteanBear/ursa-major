package xyz.proteanbear.muscida;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Generic default entity class interface definition.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public interface DefaultBean
{
    /**
     * @return The primary key value of the current data object
     */
    @JsonIgnore
    Object getPrimaryKeyValue();
}