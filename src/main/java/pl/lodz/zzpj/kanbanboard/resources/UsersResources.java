package pl.lodz.zzpj.kanbanboard.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.entity.User;
import pl.lodz.zzpj.kanbanboard.restModel.RESTUser;
import pl.lodz.zzpj.kanbanboard.service.UserService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UsersResources {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/users", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping(path = "/users", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity addUser(@RequestBody RESTUser user){
        try{
            userService.addUser(user);
        }catch (Exception e){
            return ResponseEntity.status(666).build();
        }

        return ResponseEntity.ok(userService.getUserByEmail(user.getEmail()));
    }

}
