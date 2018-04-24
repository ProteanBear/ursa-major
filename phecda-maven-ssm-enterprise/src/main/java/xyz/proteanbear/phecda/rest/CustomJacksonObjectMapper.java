package xyz.proteanbear.phecda.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;
import xyz.proteanbear.phecda.configuration.SpringRedisConfiguration;

import java.text.SimpleDateFormat;

/**
 * 根据配置自定义设置Json转换的处理
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/16
 * @since jdk1.8
 */
public class CustomJacksonObjectMapper extends ObjectMapper
{
    /**
     * 决定parser将是否允许解析使用Java/C++ 样式的注释（包括'/'+'*' 和'//' 变量）
     */
    @Value("${jackson.allowComments}")
    private boolean allowComments;

    /**
     * 决定parser是否将允许使用非双引号属性名字（这种形式在Javascript中被允许，但是JSON标准说明书中没有）
     */
    @Value("${jackson.allowUnquotedFieldNames}")
    private boolean allowUnquotedFieldNames;

    /**
     * 决定parser是否允许单引号来包住属性名称和字符串值
     */
    @Value("${jackson.allowSingleQuotes}")
    private boolean allowSingleQuotes;

    /**
     * 决定parser是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
     */
    @Value("${jackson.allowUnquotedControlChars}")
    private boolean allowUnquotedControlChars;

    /**
     * 可以允许接受所有引号引起来的字符，使用‘反斜杠\’机制
     * 如果不允许，只有JSON标准说明书中 列出来的字符可以被避开约束
     */
    @Value("${jackson.allowBackslashEscapingAnyCharacter}")
    private boolean allowBackslashEscapingAnyCharacter;

    /**
     * 允许parser可以识别"Not-a-Number" (NaN)标识集合作为一个合法的浮点数
     */
    @Value("${jackson.allowNonNumericNumbers}")
    private boolean allowNonNumericNumbers;

    /**
     * 决定了使用枚举值的标准序列化机制
     * 如果允许，则枚举假定使用Enum.toString()返回的值作为序列化结构
     * 如果禁止, 则返回Enum.name()的值
     */
    @Value("${jackson.readEnumsUsingToString}")
    private boolean readEnumsUsingToString;

    /**
     * 决定了当遇到未知属性（没有映射到属性，没有任何setter或者任何可以处理它的handler）
     * 是否应该抛出一个JsonMappingException异常
     */
    @Value("${jackson.failOnUnknownProperties}")
    private boolean failOnUnknownProperties;

    /**
     * 决定当遇到JSON null的对象是java 原始类型，则是否抛出异常
     * 当false时，则使用0 for 'int', 0.0 for double 来设定原始对象初始值
     */
    @Value("${jackson.failOnNulForPrimitives}")
    private boolean failOnNulForPrimitives;

    /**
     * 决定JSON 整数是否是一个有效的值，当被用来反序列化Java枚举值
     * 如果false，数字可以接受，并且映射为枚举的值ordinal()
     */
    @Value("${jackson.failOnNumbersForEnums}")
    private boolean failOnNumbersForEnums;

    /**
     * 决定是否接受强制非数组（JSON）值到Java集合类型
     * 如果允许，集合反序列化将尝试处理非数组值
     */
    @Value("${jackson.acceptSingleValueAsArray}")
    private boolean acceptSingleValueAsArray;

    /**
     * 可以允许JSON空字符串转换为POJO对象为null
     * 如果禁用，则标准POJO只会从JSON null或者JSON对象转换过来
     * 如果允许，则空JSON字符串可以等价于JSON null
     */
    @Value("${jackson.acceptEmptyStringAsNullObject}")
    private boolean acceptEmptyStringAsNullObject;

    /**
     * 是否排除空属性
     */
    @Value("${jackson.includeNull}")
    private boolean includeNull;

    /**
     * 缩进输出
     */
    @Value("${jackson.indentOutput}")
    private boolean indentOutput;

    /**
     * 格式化日期
     */
    @Value("${jackson.dateFormat}")
    private String dateFormat;

    /**
     * 决定对Enum 枚举值使用标准的序列化机制
     * 如果true，则返回Enum.toString()值，否则为Enum.name()
     */
    @Value("${jackson.writeEnumsUsingToString}")
    private boolean writeEnumsUsingToString;

    /**
     * 该特性决定是否在writeValue()方法之后就调用JsonGenerator.flush()方法
     * 当我们需要先压缩，然后再flush，则可能需要false。
     */
    @Value("${jackson.flushAfterWriteValue}")
    private boolean flushAfterWriteValue;

    /**
     * 决定是否将基于Date的值序列化为timestamp数字式的值，或者作为文本表示
     */
    @Value("${jackson.writeDatesAsTimestamps}")
    private boolean writeDatesAsTimestamps;

    /**
     * 是否将Map中得key为Date的值，也序列化为timestamps形式（否则，会被序列化为文本形式的值）
     */
    @Value("${jackson.writeDateKeysAsTimestamps}")
    private boolean writeDateKeysAsTimestamps;

    /**
     * 该特性决定怎样处理类型char[]序列化，是否序列化为一个显式的JSON数组，还是默认作为一个字符串
     */
    @Value("${jackson.writeCharArraysAsJsonArrays}")
    private boolean writeCharArraysAsJsonArrays;

    /**
     * 初始化设置
     */
    public void init()
    {
        //格式设置
        configure(JsonParser.Feature.ALLOW_COMMENTS,allowComments);
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,allowUnquotedFieldNames);
        configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,allowSingleQuotes);
        configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,allowUnquotedControlChars);
        configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,allowBackslashEscapingAnyCharacter);
        configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS,allowNonNumericNumbers);

        //反序列化设置
        configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING,readEnumsUsingToString);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,failOnUnknownProperties);
        configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,failOnNulForPrimitives);
        configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,failOnNumbersForEnums);
        configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,acceptSingleValueAsArray);
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,acceptEmptyStringAsNullObject);

        //序列化设置
        if(!includeNull) setSerializationInclusion(JsonInclude.Include.NON_NULL);
        configure(SerializationFeature.INDENT_OUTPUT,indentOutput);
        if(!StringUtils.isEmpty(dateFormat))
            setDateFormat(new SimpleDateFormat(dateFormat));
        configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING,writeEnumsUsingToString);
        configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE,flushAfterWriteValue);
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,writeDatesAsTimestamps);
        configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS,writeDateKeysAsTimestamps);
        configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS,writeCharArraysAsJsonArrays);
    }
}