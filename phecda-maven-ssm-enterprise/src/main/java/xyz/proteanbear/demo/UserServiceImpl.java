package xyz.proteanbear.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.proteanbear.phecda.rest.DataService;

/**
 *
 */
@Service("userService")
public class UserServiceImpl implements DataService<User,UserMapper>
{
    @Autowired
    private UserMapper mapper;

    @Override
    public UserMapper getMapper()
    {
        return mapper;
    }

    @Override
    public String getDefaultOrderBy()
    {
        return "user asc";
    }
}
