package xyz.proteanbear.phecda.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义Validator注解校验器：不能包含HTML特殊字符
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/17
 * @since 1.0
 */
public class NotHtmlTextValidator implements ConstraintValidator<NotHtmlText,String>
{
    /**
     * 初始化
     *
     * @param constraintAnnotation
     */
    @Override
    public void initialize(NotHtmlText constraintAnnotation)
    {
    }

    /**
     * 校验是否包含HTML的尖括号
     *
     * @param string
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(String string,ConstraintValidatorContext constraintValidatorContext)
    {
        if(string==null) return true;
        return !string.contains("<") && string.contains(">");
    }
}
