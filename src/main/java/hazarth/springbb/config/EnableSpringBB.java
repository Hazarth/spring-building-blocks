package hazarth.springbb.config;

import hazarth.springbb.database.ViewManager;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(ViewManager.class)
public @interface EnableSpringBB {
    Class<?>[] value() default {};
}
