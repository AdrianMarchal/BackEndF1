package BackEndF1.controller;

import BackEndF1.repositories.UserRepository;
import BackEndF1.models.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/usuarios")
@Tag(name = "Usuario", description = "(IMPORTANTE) Necesario para poder usar el resto de endpoint ")
public class UserController {



    @Autowired
    private UserRepository userRepository;
    Map<String,Object> response = new HashMap<>();


    /*Metodo que nos permite añadir un usuario a la base de datos de mongoDB*/
    @PostMapping(value = "/addUser")
    @Operation(
            summary = "Registra un usuario nuevo",
            description = "Este endpoint registra un usuario nuevo y genera su api_token unica necesaria para usar la api"
    )
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody  User user) {
        response.clear();
        //Aqui encriptamos la contraseña
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        if ( user.getTipoUser() == null || user.getTipoUser().equals("")) user.setTipoUser("USER");
        System.out.println(user);
        if (userRepository.findByEmail(user.getEmail()) == null){
            userRepository.save(user);
            response.put("message","Usuario creado correctamente tu api key es " + user.getAuthToken());
            response.put("status",200);
            return ResponseEntity.ok(response);
        }

        response.put("mensaje", "Usuario ya registrado");
        response.put("status", HttpStatus.CONFLICT);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @PostMapping(value = "/login")
    @Operation(
            summary = "Endpoint que permite hacer un login",
            description = "Este endpoint permite hacer un loggeo y poder volver a visualizar nuestro api_token"
    )
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        response.clear();
        User temp = userRepository.findByEmail(user.getEmail());
        if (temp == null){
            response.put("mensaje", "Usuario o contraseña erroneos");
            response.put("status", HttpStatus.CONFLICT);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        //Compruebo si la contraseña son iguales
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean passwordMatch = passwordEncoder.matches(user.getPassword(), temp.getPassword());


        if (user.getEmail().equals(temp.getEmail()) && passwordMatch){
            response.put("message","Login realizado con exito " + temp.getAuthToken());
            response.put("status",200);
            return ResponseEntity.ok(response);
        }

        response.put("mensaje", "Usuario o contraseña erroneos");
        response.put("status", HttpStatus.CONFLICT);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
