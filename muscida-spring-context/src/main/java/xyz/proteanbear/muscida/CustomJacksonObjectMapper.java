package xyz.proteanbear.muscida;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import xyz.proteanbear.muscida.utils.PropertiesUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * According to the configuration custom settings Json conversion processing.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/16
 * @since jdk1.8
 */
public class CustomJacksonObjectMapper extends ObjectMapper
{
    /**
     * log record
     */
    public static final Logger logger=LoggerFactory.getLogger(CustomJacksonObjectMapper.class);

    /**
     * Initialization settings
     */
    public void init()
    {
        //Loading .properties file
        Properties jackson=null;
        try
        {
            jackson=PropertiesUtils.load("jackson");
        }
        catch(IOException e)
        {
            logger.warn("Jackson custom file not found, default settings will be used!",e);
            return;
        }


        //Formatting
        //Decide whether parser will allow parsing using Java/C++ style
        //comments (including '/'+'*' and '//' variables)
        configure(JsonParser.Feature.ALLOW_COMMENTS,Boolean.parseBoolean(jackson.getProperty("jackson.allowComments")));

        //Deciding if the parser will allow the use
        //of non-double quoted attribute names
        //(this form is allowed in Javascript, but not in the JSON standard specification)
        configure(
                JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
                Boolean.parseBoolean(jackson.getProperty("jackson.allowUnquotedFieldNames"))
        );

        //Determines whether parser allows single quotes
        //to wrap attribute names and string values
        configure(
                JsonParser.Feature.ALLOW_SINGLE_QUOTES,
                Boolean.parseBoolean(jackson.getProperty("jackson.allowSingleQuotes"))
        );

        //Decide if parser allows JSON strings
        //to contain non-quoted control characters
        //(ASCII characters with values less than 32, including tabs and line breaks)
        configure(
                JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,
                Boolean.parseBoolean(jackson.getProperty("jackson.allowUnquotedControlChars"))
        );

        //Can accept all quoted characters, use ‘backslash\’ mechanism
        //If not allowed, only the characters listed in the JSON standard specification can be bypassed
        configure(
                JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,
                Boolean.parseBoolean(jackson.getProperty("jackson.allowBackslashEscapingAnyCharacter"))
        );

        //Allow parser to recognize the "Not-a-Number" (NaN) identity set as a valid floating point number
        configure(
                JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS,
                Boolean.parseBoolean(jackson.getProperty("jackson.allowNonNumericNumbers"))
        );


        //Deserialization settings
        //Determined the standard serialization mechanism using enumeration values
        //If allowed, the enumeration assumes that the value returned by Enum.toString() is used as the serialization structure
        //If prohibited, returns the value of Enum.name()
        configure(
                DeserializationFeature.READ_ENUMS_USING_TO_STRING,
                Boolean.parseBoolean(jackson.getProperty("jackson.readEnumsUsingToString"))
        );

        //Decided when an unknown property was encountered (no mapping to attribute, no setter, or any handler that could handle it)
        //Should I throw a JsonMappingException
        configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                Boolean.parseBoolean(jackson.getProperty("jackson.failOnUnknownProperties"))
        );

        //Deciding whether or not to throw an exception when the JSON null object is of java primitive type
        //When false, use 0 for 'int', 0.0 for double to set the initial value of the original object
        configure(
                DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                Boolean.parseBoolean(jackson.getProperty("jackson.failOnNulForPrimitives"))
        );

        //Determines whether the JSON integer is a valid value when used to deserialize Java enumeration values
        //If false, the number is acceptable and mapped to an enumerated value ordinal()
        configure(
                DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,
                Boolean.parseBoolean(jackson.getProperty("jackson.failOnNumbersForEnums"))
        );

        //Decide whether to accept mandatory non-array (JSON) values to Java collection type
        //Collection deserialization will attempt to process non-array values if allowed
        configure(
                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                Boolean.parseBoolean(jackson.getProperty("jackson.acceptSingleValueAsArray"))
        );

        //Can Convert JSON Empty String to POJO Object to Null
        //If disabled, standard POJOs will only be converted from JSON nulls or JSON objects
        //If allowed, an empty JSON string can be equivalent to JSON null
        configure(
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                Boolean.parseBoolean(jackson.getProperty("jackson.acceptEmptyStringAsNullObject"))
        );


        //Serialization settings
        //Exclude null attributes
        if(!Boolean.parseBoolean(jackson.getProperty("jackson.includeNull")))
        {
            setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }

        //Indented output
        configure(SerializationFeature.INDENT_OUTPUT,Boolean.parseBoolean(jackson.getProperty("jackson.indentOutput")));
        //Format date
        String dateFormat=jackson.getProperty("jackson.dateFormat");
        if(dateFormat!=null && !"".equals(dateFormat))
        {
            setDateFormat(new SimpleDateFormat(dateFormat));
        }

        //Deciding to use standard serialization mechanisms for Enum enumeration values
        //If true, returns the Enum.toString() value, otherwise it's Enum.name()
        configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING,
                  Boolean.parseBoolean(jackson.getProperty("jackson.writeEnumsUsingToString")));

        //This property determines whether the JsonGenerator.flush() method is called after the writeValue() method.
        //When we need to compress and then flush, it may need to be false
        configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE,
                  Boolean.parseBoolean(jackson.getProperty("jackson.flushAfterWriteValue")));

        //Decide whether to serialize Date-based values to timestamp numeric values, or as text representations
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                  Boolean.parseBoolean(jackson.getProperty("jackson.writeDatesAsTimestamps")));

        //Whether the value of key in the Map is Date is also serialized into timestamps
        // (otherwise it will be serialized as a text value)
        configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS,
                  Boolean.parseBoolean(jackson.getProperty("jackson.writeDateKeysAsTimestamps")));

        //This property determines how to handle type char[] serialization, is it serialized as an explicit JSON array, or as a string by default
        configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS,
                  Boolean.parseBoolean(jackson.getProperty("jackson.writeCharArraysAsJsonArrays")));
    }
}