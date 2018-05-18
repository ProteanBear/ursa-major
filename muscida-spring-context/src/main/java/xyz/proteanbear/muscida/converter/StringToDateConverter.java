package xyz.proteanbear.muscida.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * String to date converter used in the SpringMVC
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/09
 * @since jdk1.8
 */
public class StringToDateConverter implements Converter<String,Date>
{
    /**
     * Log
     */
    private static final Logger logger=LoggerFactory.getLogger(StringToDateConverter.class);

    /**
     * Default date pattern
     */
    private static final String DEFAULT_PATTERN="yyyy-MM-dd HH:mm:ss";

    /**
     * date formatter
     */
    private SimpleDateFormat dateFormat=new SimpleDateFormat(DEFAULT_PATTERN);

    /**
     * @param datePattern date format pattern
     */
    public void setDatePattern(String datePattern)
    {
        dateFormat.applyPattern(datePattern);
    }

    /**
     * Convert date string to date object
     *
     * @param dateString date string
     * @return Date object
     */
    @Override
    public Date convert(String dateString)
    {
        try
        {
            return dateFormat.parse(dateString);
        }
        catch(ParseException e)
        {
            logger.error(e.getMessage(),e);
            throw new IllegalArgumentException("{DATE_PARAMETER_FORMAT_IS_WRONG}");
        }
    }
}
