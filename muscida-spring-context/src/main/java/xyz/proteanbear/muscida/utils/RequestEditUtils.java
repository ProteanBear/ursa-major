package xyz.proteanbear.muscida.utils;

import javax.servlet.ServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

/**
 * Using Reflection to Modify Parameters in HttpRequest.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/17
 * @since jdk1.8
 */
public class RequestEditUtils
{
    /**
     * HttpRequest implementation class private requestField in RequestFacade request
     */
    private static Field requestField;

    /**
     * HttpRequest implementation class private parametersParsedField in RequestFacade's type
     */
    private static Field parametersParsedField;

    /**
     * HttpRequest implementation class private coyoteRequestField in RequestFacade's type
     */
    private static Field coyoteRequestField;

    /**
     * HttpRequest implementation class private parametersField in RequestFacade's type
     */
    private static Field parametersField;

    /**
     * HttpRequest implementation class private paramHashValues in RequestFacade's type
     */
    private static Field paramHashValues;

    /**
     * static get has error
     */
    private static boolean error;

    //Get properties through reflection
    static
    {
        try
        {
            Class clazz=Class.forName("org.apache.catalina.connector.RequestFacade");
            requestField=clazz.getDeclaredField("request");
            requestField.setAccessible(true);

            parametersParsedField=requestField.getType().getDeclaredField("parametersParsed");
            parametersParsedField.setAccessible(true);

            coyoteRequestField=requestField.getType().getDeclaredField("coyoteRequest");
            coyoteRequestField.setAccessible(true);

            parametersField=coyoteRequestField.getType().getDeclaredField("parameters");
            parametersField.setAccessible(true);

            paramHashValues=parametersField.getType().getDeclaredField("paramHashValues");
            paramHashValues.setAccessible(true);

            error=false;
        }
        catch(ClassNotFoundException|NoSuchFieldException e)
        {
            e.printStackTrace();
            error=true;
        }
    }

    /**
     * Get a map can edit from the request
     *
     * @param request request
     * @return a parameter map can edit
     */
    private static Map<String,ArrayList<String>> getRequestMapCanEdit(ServletRequest request)
    {
        try
        {
            if(error) return null;

            Object innerRequest=requestField.get(request);
            parametersParsedField.setBoolean(innerRequest,true);
            Object coyoteRequestObject=coyoteRequestField.get(innerRequest);
            Object parameterObject=parametersField.get(coyoteRequestObject);
            return (Map<String,ArrayList<String>>)paramHashValues.get(parameterObject);
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Write the contents of the map to the request
     *
     * @param parameters  parameter content
     * @param intoRequest request
     */
    public static void put(Map<String,ArrayList<String>> parameters,ServletRequest intoRequest)
    {
        Map<String,ArrayList<String>> paramMap=getRequestMapCanEdit(intoRequest);
        if(paramMap!=null && parameters!=null)
        {
            parameters.forEach(paramMap::put);
        }
    }
}