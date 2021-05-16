package pl.lodz.zzpj.kanbanboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.util.Locale;

@SpringBootApplication
public class KanbanBoardApplication {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SpringApplication.run(KanbanBoardApplication.class, args);
    }

}
