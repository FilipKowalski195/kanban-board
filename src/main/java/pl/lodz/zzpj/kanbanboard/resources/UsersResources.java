package pl.lodz.zzpj.kanbanboard.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.zzpj.kanbanboard.DTO.DTOUser;
import pl.lodz.zzpj.kanbanboard.service.UserService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UsersResources {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/users", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping(path ="/users/{mail}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getUserByMail(@PathVariable String mail){
        return ResponseEntity.ok(userService.getUserByEmail(mail));
    }

    @PostMapping(path = "/users", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity addUser(@RequestBody DTOUser user){
        try{
            userService.addUser(user);
        }catch (Exception e){
            return ResponseEntity.status(666).build(); //TODO PORZADNE KODY BLEDU MOZE NAWET JAKIES MESSEGE DODAC
        }
        return ResponseEntity.ok(userService.getUserByEmail(user.getEmail()));
    }
}
