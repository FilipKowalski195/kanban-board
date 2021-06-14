package pl.lodz.zzpj.kanbanboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.lodz.zzpj.kanbanboard.utils.DataFiller;

import javax.annotation.PostConstruct;
import java.util.Locale;

@SpringBootApplication
public class KanbanBoardApplication {
    
    private final DataFiller dataFiller;

    public KanbanBoardApplication(DataFiller dataFiller) {
        this.dataFiller = dataFiller;
    }

    @PostConstruct
    void init() {
        dataFiller.fillRepos();
    }
    
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SpringApplication.run(KanbanBoardApplication.class, args);
    }
}
