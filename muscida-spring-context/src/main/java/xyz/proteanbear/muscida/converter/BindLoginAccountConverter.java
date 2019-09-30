package xyz.proteanbear.muscida.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import xyz.proteanbear.muscida.Authority;

import javax.validation.constraints.NotNull;

/**
 * Read and bind login user to method parameters
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/09
 * @since jdk1.8
 */
public class BindLoginAccountConverter implements Converter<String[],Authority.Account>
{
    /**
     * Log
     */
    private static final Logger logger=LoggerFactory.getLogger(BindLoginAccountConverter.class);

    /**
     * Current account loader
     */
    @NotNull
    private Authority.AccountHandler accountHandler;

    /**
     * @param accountHandler account loader implement
     */
    public void setAccountHandler(Authority.AccountHandler accountHandler)
    {
        assert null!=accountHandler;
        this.accountHandler=accountHandler;
    }

    /**
     * Convert login token to account object
     *
     * @param strings token string and class name
     * @return login account object
     */
    @Override
    public Authority.Account convert(String[] strings)
    {
        if(accountHandler==null || strings.length<2) return null;

        //Get account object by account handler
        Authority.Account account=accountHandler.get(strings[0],strings[1]);

        //logger
        if(logger.isDebugEnabled() && account!=null)
        {
            try
            {
                logger.debug("Current login account is:{}",
                             (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(account));
            }
            catch(JsonProcessingException e)
            {
                logger.warn(e.getMessage(),e);
            }
        }

        return account;
    }
}
