package hazarth.springbb.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Configuration
@ComponentScan("hazarth.springbb")
public class ViewManager {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ViewManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void construct() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("views.sql");

        if(stream != null) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line = null;
            while ((line = reader.readLine()) != null) {
                this.jdbcTemplate.execute(line);
            }

            reader.close();
        }

    }

}
