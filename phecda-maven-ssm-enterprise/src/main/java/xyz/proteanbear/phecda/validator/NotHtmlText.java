package xyz.proteanbear.phecda.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 自定义Validator注解：不能包含HTML特殊字符
 * @author ProteanBear
 * @version 1.0.0,2018/04/17
 * @since 1.0
 */
@Constraint(validatedBy=NotHtmlTextValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(NotHtmlText.List.class)
public @interface NotHtmlText
{
    String message() default "{xyz.proteanbear.phecda.validator.NotHtmlText.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        NotHtmlText[] value();
    }
}