package xyz.proteanbear.muscida.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Custom Validator Annotator: Cannot Include HTML Special Characters
 *
 * @author ProteanBear
 * @version 1.0.0,2018/04/17
 * @since jdk1.8
 */
public class NotHtmlTextValidator implements ConstraintValidator<NotHtmlText,String>
{
    /**
     * initialization
     *
     * @param constraintAnnotation Annotation object
     */
    @Override
    public void initialize(NotHtmlText constraintAnnotation)
    {
    }

    /**
     * Check whether the angle brackets of the HTML are included
     *
     * @param string                     String content
     * @param constraintValidatorContext Validator context
     * @return If you do not include "/<" or "/>" returns true
     */
    @Override
    public boolean isValid(String string,ConstraintValidatorContext constraintValidatorContext)
    {
        if(string==null) return true;
        return !string.contains("<") && string.contains(">");
    }
}
