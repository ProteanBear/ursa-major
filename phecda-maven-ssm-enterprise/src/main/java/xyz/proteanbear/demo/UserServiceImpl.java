package xyz.proteanbear.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.proteanbear.muscida.DataService;

/**
 * Business service implement for
 *
 * @author ProteanBear
 * @version 1.0.0,${DATE_CURRENT}
 * @since jdk1.8
 */
@Service("userService")
public class UserServiceImpl implements DataService<User,UserMapper>
{
    /**
     * Data mapper
     */
    @Autowired
    private UserMapper mapper;

    /**
     * @return data mapper
     */
    @Override
    public UserMapper getMapper()
    {
        return mapper;
    }

    /**
     * @return default order string
     */
    @Override
    public String getDefaultOrderBy()
    {
        return "user asc";
    }
}
