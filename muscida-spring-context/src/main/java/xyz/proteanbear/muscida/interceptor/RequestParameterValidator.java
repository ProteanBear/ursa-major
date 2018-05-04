package xyz.proteanbear.muscida.interceptor;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.support.RequestContextUtils;
import xyz.proteanbear.muscida.GlobalExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interceptors: Unified validation of Web request parameters;
 * Extend the normal parameters of Controller using Validation to verify.
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/03
 * @since jdk1.8
 */
public class RequestParameterValidator implements HandlerInterceptor
{
    /**
     * Log
     */
    public static final Logger logger=LoggerFactory.getLogger(RequestParameterValidator.class);

    /**
     * Request mapping handler adapter
     */
    @Autowired
    private RequestMappingHandlerAdapter adapter;

    /**
     * International resources
     */
    @Autowired
    @Qualifier("responseMessageResource")
    private ReloadableResourceBundleMessageSource messageSource;

    /**
     * Argument resolver cache
     */
    private final Map<MethodParameter,HandlerMethodArgumentResolver> argumentResolverCache=new ConcurrentHashMap<>(256);

    /**
     * Init binder cache
     */
    private final Map<Class<?>,Set<Method>> initBinderCache=new ConcurrentHashMap<>(64);

    /**
     * Validator factory
     */
    private ValidatorFactory validatorFactory;
    /**
     * Parse and get the parameters of the current call method and verify
     *
     * @param request  web request
     * @param response web response
     * @param handler  handle method
     * @return If all parameters is right ,return true
     * @throws Exception If one parameter is wrong,throw exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception
    {
        //check handler
        if(!(handler instanceof HandlerMethod)) return true;

        //Get all method parameter values
        HandlerMethod method=(HandlerMethod)handler;
        List<Object> values=resolveValues(request,response,method);

        //Validate all parameters
        ExecutableValidator validator=validatorFactory.getValidator().forExecutables();
        Set<ConstraintViolation<Object>> validResult=validator.validateParameters(method.getBean(),method.getMethod(),values.toArray());
        //If has error,throw exception
        if(!validResult.isEmpty())
        {
            ConstraintViolationImpl violation=(ConstraintViolationImpl)validResult.iterator().next();
            //Use message resource replace ${field} information
            throw new ValidationException(
                    violation.getMessage().replace(
                            GlobalExceptionHandler.messageFieldReplace,
                            messageSource.getMessage(
                                    violation.getRootBeanClass().getSimpleName()+"."+violation.getPropertyPath().toString(),
                                    null,
                                    RequestContextUtils.getLocale(request)
                            )
                    )
            );
        }

        return true;
    }

    /**
     * Get all method parameter values
     *
     * @param request http request
     * @param response http response
     * @param method handler method
     * @return value list
     */
    private ArrayList<Object> resolveValues(
            HttpServletRequest request,HttpServletResponse response,
            HandlerMethod method)
            throws Exception
    {
        //record result
        ArrayList<Object> result=new ArrayList<>();

        //Parameter array is empty
        MethodParameter[] parameters=method.getMethodParameters();
        if(parameters.length<1) return result;

        //Traversal to get all parameters
        ServletWebRequest webRequest=new ServletWebRequest(request,response);
        for(MethodParameter parameter : parameters)
        {
            //Get resolver
            HandlerMethodArgumentResolver resolver=getArgumentResolver(parameter);
            Assert.notNull(resolver,"Unknown parameter type ["+parameter.getParameterType().getName()+"]");

            //Resolving parameter content
            ModelAndViewContainer mavContainer=new ModelAndViewContainer();
            mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
            WebDataBinderFactory webDataBinderFactory=getDataBinderFactory(method);
            Object content=null;
            try
            {
                content=resolver.resolveArgument(parameter,mavContainer,webRequest,webDataBinderFactory);
            }
            catch(Exception ignored){}

            //Add to result
            result.add(content);
        }

        return result;
    }

    /**
     * Get handler method argument resolver.
     *
     * @param parameter method parameter
     * @return argument resolver
     */
    private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter)
    {
        //Get from cache
        HandlerMethodArgumentResolver result=this.argumentResolverCache.get(parameter);
        if(result==null)
        {
            for(HandlerMethodArgumentResolver methodArgumentResolver : adapter.getArgumentResolvers())
            {
                //log
                if(logger.isTraceEnabled())
                {
                    logger.trace(
                            "Testing if argument resolver [{}] supports [{}]",
                            methodArgumentResolver,
                            parameter.getGenericParameterType());
                }

                //If a resolver support this parameter,get it and save into cache
                if(methodArgumentResolver.supportsParameter(parameter))
                {
                    result=methodArgumentResolver;
                    this.argumentResolverCache.put(parameter,result);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Get data binder factory
     *
     * @param handlerMethod handler method
     * @return binder factory
     * @throws Exception exceptions
     */
    private WebDataBinderFactory getDataBinderFactory(HandlerMethod handlerMethod)
    {
        //Get from cache
        Class<?> handlerType=handlerMethod.getBeanType();
        Set<Method> methods=this.initBinderCache.get(handlerType);
        if(methods==null)
        {
            methods=MethodIntrospector.selectMethods(handlerType,RequestMappingHandlerAdapter.INIT_BINDER_METHODS);
            this.initBinderCache.put(handlerType,methods);
        }

        //Init binder methods
        List<InvocableHandlerMethod> initBinderMethods=new ArrayList<>();
        for(Method method : methods)
        {
            Object bean=handlerMethod.getBean();
            initBinderMethods.add(new InvocableHandlerMethod(bean,method));
        }

        return new ServletRequestDataBinderFactory(initBinderMethods,adapter.getWebBindingInitializer());
    }

    /**
     * @param validatorFactory validator factory
     */
    public void setValidatorFactory(ValidatorFactory validatorFactory)
    {
        this.validatorFactory=validatorFactory;
    }
}