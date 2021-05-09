package pl.lodz.zzpj.kanbanboard.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.service.UserService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UsersResources {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/meals", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getAllMeals(){
        return ResponseEntity.ok("no kurwa że tak powiem dziala");
    }

}
