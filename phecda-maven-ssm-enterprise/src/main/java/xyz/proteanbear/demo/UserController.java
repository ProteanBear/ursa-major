package xyz.proteanbear.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.proteanbear.phecda.DataService;
import xyz.proteanbear.phecda.Page;

import javax.validation.Valid;

@Controller
@RequestMapping("/demo")
public class UserController
{
    @Autowired
    @Qualifier("userService")
    private DataService service;

    @RequestMapping(value="/user",method=RequestMethod.GET)
    @ResponseBody
    public Page list(@Valid Page page) throws Exception
    {
        return page.setData(service.list(page.getCondition()))
                .setTotal(service.count(page.getCondition()));
    }
}