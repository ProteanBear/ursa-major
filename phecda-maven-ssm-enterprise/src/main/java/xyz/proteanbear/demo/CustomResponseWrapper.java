package xyz.proteanbear.demo;

import org.springframework.stereotype.Component;
import xyz.proteanbear.muscida.ResponseWrapper;

public class CustomResponseWrapper implements ResponseWrapper
{
    @Override
    public boolean isObjectAfterWrap(Object object)
    {
        return (object instanceof CustomResponse);
    }

    @Override
    public Object wrap(String message,Object data)
    {
        return new CustomResponse("success",message,data);
    }

    @Override
    public Object wrap(String status,String message)
    {
        return new CustomResponse(status,message,null);
    }
}