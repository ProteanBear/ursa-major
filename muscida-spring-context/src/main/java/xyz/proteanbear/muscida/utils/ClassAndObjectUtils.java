package xyz.proteanbear.muscida.utils;

/**
 * Tools:Class and object related tools
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/09
 * @since jdk1.8
 */
public class ClassAndObjectUtils
{
    /**
     * Whether a class implements the specified interface.
     *
     * @param object         object
     * @param interfaceClass interface class
     * @return If the class implement interface,return true
     */
    public static boolean isImplement(Object object,Class interfaceClass)
    {
        assert null!=object;
        return isImplement(object.getClass(),interfaceClass);
    }

    /**
     * Whether a class implements the specified interface.
     *
     * @param targetClass    class
     * @param interfaceClass interface class
     * @return If the class implement interface,return true
     */
    public static boolean isImplement(Class targetClass,Class interfaceClass)
    {
        assert null!=targetClass;
        assert null!=interfaceClass;
        Class[] classes=targetClass.getInterfaces();
        boolean result=false;
        for(Class aClass : classes)
        {
            if(interfaceClass.isAssignableFrom(aClass))
            {
                result=true;
                break;
            }
        }
        return result;
    }

    /**
     * Whether a class implements the specified interface.
     *
     * @param object             object
     * @param interfaceClassName interface class name
     * @return If the class implement interface,return true
     */
    public static boolean isImplement(Object object,String interfaceClassName)
    {
        assert null!=object;
        return isImplement(object.getClass(),interfaceClassName);
    }

    /**
     * Whether a class implements the specified interface.
     *
     * @param targetClass        class
     * @param interfaceClassName interface class name
     * @return If the class implement interface,return true
     */
    public static boolean isImplement(Class targetClass,String interfaceClassName)
    {
        assert null!=targetClass;
        assert null!=interfaceClassName;
        Class[] classes=targetClass.getInterfaces();
        boolean result=false;
        for(Class aClass : classes)
        {
            if(interfaceClassName.equalsIgnoreCase(aClass.getSimpleName()))
            {
                result=true;
                break;
            }
        }
        return result;
    }
}