package xyz.proteanbear.demo;

import org.springframework.stereotype.Repository;
import xyz.proteanbear.phecda.DefaultDataMapper;

/**
 * 数据层操作
 *
 * @author ProteanBear
 * @version 1.0.0,2018-04-11
 * @since jdk1.8
 */
@Repository
public interface UserMapper extends DefaultDataMapper<User>
{
}