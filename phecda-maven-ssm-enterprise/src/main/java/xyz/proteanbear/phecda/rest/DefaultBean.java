package xyz.proteanbear.phecda.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Universal default entity interface definition.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/24
 * @since jdk1.8
 */
public interface DefaultBean
{
    /**
     * @return data primary key value in this data object.
     */
    @JsonIgnore
    Object getPrimaryKeyValue();
}