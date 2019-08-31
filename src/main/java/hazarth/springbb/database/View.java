package hazarth.springbb.database;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(Views.class)
public @interface View {
    String tableName() default "";
    String name() default "";
    String where() default "";
}
