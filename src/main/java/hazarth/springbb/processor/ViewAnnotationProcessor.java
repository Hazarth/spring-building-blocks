package hazarth.springbb.processor;

import hazarth.springbb.database.View;
import hazarth.springbb.database.Views;
import org.springframework.stereotype.Component;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.persistence.Table;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

@Component
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"hazarth.springbb.database.View","hazarth.springbb.database.Views"})
public class ViewAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {

        String packageName = null;
        Set<String> queries = new HashSet<>();

        for(TypeElement element : set){
            if(element.getQualifiedName().toString().equals("hazarth.springbb.database.View")){
                for(Element entity : env.getElementsAnnotatedWith(View.class)){
                    if(packageName == null) packageName = ((PackageElement)entity.getEnclosingElement()).getQualifiedName().toString();
                    View view = entity.getAnnotation(View.class);
                    queries.add(processElementIntoSQL(entity,view));
                }
            }else if (element.getQualifiedName().toString().equals("hazarth.springbb.database.Views")){
                for(Element entity : env.getElementsAnnotatedWith(Views.class)){
                    if(packageName == null) packageName = ((PackageElement)entity.getEnclosingElement()).getQualifiedName().toString();
                    Views views = entity.getAnnotation(Views.class);
                    for(View view : views.value()){
                        queries.add(processElementIntoSQL(entity,view));
                    }
                }
            }
        }

        if(!queries.isEmpty()) {

            try {
                FileObject jfo = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,"","views.sql");

                Writer writer = jfo.openWriter();

                for(String sql : queries){
                    writer.write(sql);
                }

                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }

    public String processElementIntoSQL(Element e, View view){
        String tableName = tableNameResolution(e);
        return String.format("CREATE OR REPLACE VIEW %s AS SELECT * FROM %s WHERE %s;\n", view.name(), tableName, view.where());
    }

    public String tableNameResolution(Element e){

        View view = e.getAnnotation(View.class);
        if(view != null && !view.tableName().isEmpty()) return view.tableName();

        Table table = e.getAnnotation(Table.class);
        if(table != null) return table.name();

        String snakeCase = e.getSimpleName().toString().replaceAll("([A-Z])","_$1").toLowerCase();

        if(snakeCase.charAt(0) == '_'){
            snakeCase = snakeCase.substring(1);
        }

        return snakeCase;
    }

}
