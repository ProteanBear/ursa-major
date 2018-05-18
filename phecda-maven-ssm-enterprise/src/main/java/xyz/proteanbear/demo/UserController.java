package xyz.proteanbear.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.proteanbear.muscida.Authority;
import xyz.proteanbear.muscida.DataService;
import xyz.proteanbear.muscida.Page;

import javax.validation.Valid;

@RestController
@RequestMapping("/demo")
public class UserController
{
    @Autowired
    @Qualifier("userService")
    private DataService<User,UserMapper> service;

    @RequestMapping(value="/user",method=RequestMethod.GET)
    @Authority.Set(allow=true,accountClass=User.class)
    public Page list(@Valid Page page)
            throws Exception
    {
        return page.setData(service.list(page.getCondition()))
                .setTotal(service.count(page.getCondition()));
    }

    @RequestMapping(value="/login",method=RequestMethod.GET)
    @Authority.Set(mustLogin=false,autoStore=true)
    public User login(String user,
                      @RequestParam("loginAccount") Authority.Account account)
            throws Exception
    {
        return null==account?service.get(user):account.getBean(User.class);
    }

    @RequestMapping(value="/logout",method=RequestMethod.GET)
    @Authority.Set(mustLogin=false,autoRemove=true)
    public void logout(@RequestParam("loginAccount") Authority.Account account) throws Exception
    {

    }
}