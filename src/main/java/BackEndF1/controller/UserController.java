package BackEndF1.controller;

import BackEndF1.repositories.UserRepository;
import BackEndF1.models.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public ResponseEntity<Map<String, Object>> addUser(@Valid @RequestBody  User user) {
        response.clear();

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(user.getEmail());

        if (!matcher.matches()) {
            response.put("message", "El email no tiene un formato válido");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }


        //Aqui encriptamos la contraseña
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        if ( user.getTipoUser() == null || user.getTipoUser().equals("")) user.setTipoUser("USER");
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

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(user.getEmail());

        if (!matcher.matches()) {
            response.put("message", "El email no tiene un formato válido");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);
        }

        User temp = userRepository.findByEmail(user.getEmail());
        if (temp == null){
            response.put("mensaje", "Email o contraseña erroneos");
            response.put("status", HttpStatus.CONFLICT);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        //Compruebo si la contraseña son iguales
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean passwordMatch = passwordEncoder.matches(user.getPassword(), temp.getPassword());


        if (user.getEmail().equals(temp.getEmail()) && passwordMatch){
            response.put("user" , temp);
            response.put("message","Login realizado con exito " + temp.getAuthToken());
            response.put("status",200);
            return ResponseEntity.ok(response);
        }

        response.put("mensaje", "Usuario o contraseña erroneos");
        response.put("status", HttpStatus.CONFLICT);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    @PutMapping( value = "/update")
    @Operation(
            summary = "Endpoint que permite al usuario actualizar sus datos",
            description = "Este endpoint permite actualizar los datos del usuario"
    )
    public ResponseEntity<Map<String, Object>> update(@RequestBody User userUpdate, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (userUpdate == null){
            response.put("mensaje", "No hay body el la peticion");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        if (userUpdate.getEmail() != null && !userUpdate.getEmail().isEmpty()) {
            // Validar el formato del email antes de actualizar
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,6}$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(userUpdate.getEmail());

            if (!matcher.matches()) {
                response.put("message", "El email no tiene un formato válido");
                response.put("status", HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.badRequest().body(response);
            }

            // Comprobar si el email ya está en uso
            if (!userUpdate.getEmail().equals(temp.getEmail()) && userRepository.findByEmail(userUpdate.getEmail()) != null) {
                response.put("message", "El email ya está registrado");
                response.put("status", HttpStatus.CONFLICT.value());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            temp.setEmail(userUpdate.getEmail());
        }

        if (userUpdate.getPassword() != null && !userUpdate.getPassword().isEmpty()) {
            // Encriptar la nueva contraseña
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(userUpdate.getPassword());
            temp.setPassword(hashedPassword);
        }
        if (userUpdate.getName() != null && !userUpdate.getName().isEmpty()) {
            temp.setName(userUpdate.getName());
        }


        // Guardar los cambios
        userRepository.save(temp);

        response.put("message", "Usuario actualizado correctamente");
        response.put("user",temp);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
    //Los siguientos metodos son de administracion

    final String USERTYPE = "ROOT";

    @Operation(
            summary = "Endpoint que permite al un administrador borrar un usuario",
            description = "Este endpoint permite borrar un usuario solo lo puede realizar los usuarios administradores"
    )
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestHeader("Authorization") String authToken, @PathVariable String id) {
        response.clear();

        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (!temp.getTipoUser().equals(USERTYPE)) {
            response.put("mensaje", "Esta operación solo la puede realizar un administrador");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        User userBorrar = userRepository.findBy_id(id);

        if (userBorrar == null) {
            response.put("mensaje", "Usuario no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        userRepository.deleteById(id);
        response.put("mensaje", "Usuario eliminado correctamente");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @PutMapping(value = "/cambiarTipoUsuario/{id}")
    @Operation(
            summary = "Endpoint que permite al un administrador cambiar el tipo de usuario de un usuario",
            description = "Este endpoint permite cambiar el tipo de usuario a ROOT o USER solo lo puede realizar los usuarios administradores"
    )
    public ResponseEntity<Map<String, Object>> updateUser(@RequestHeader("Authorization") String authToken, @PathVariable String id,@RequestParam String tipoUser) {

        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (!temp.getTipoUser().equals(USERTYPE)) {
            response.put("mensaje", "Esta operación solo la puede realizar un administrador");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (tipoUser == null || tipoUser.isEmpty()) {
            response.put("mensaje", "Parametro tipoUser es obligatorio");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if(!tipoUser.equals("ROOT") && !tipoUser.equals("USER")){
            response.put("mensaje", "Parametro tipoUser solo puede ser ROOT o USER");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        User userUpdate = userRepository.findBy_id(id);

        if (userUpdate == null) {
            response.put("mensaje", "Usuario no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        userUpdate.setTipoUser(tipoUser);
        userRepository.save(userUpdate);
        response.put("usuario", userUpdate);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
