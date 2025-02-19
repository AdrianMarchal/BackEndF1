package BackEndF1.controller;


import BackEndF1.models.Equipo;
import BackEndF1.models.User;
import BackEndF1.repositories.EquipoRepository;
import BackEndF1.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/equipos")
@Tag(name = "Equipos", description = "Operaciones con los equipos")
public class EquipoController {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private UserRepository userRepository;

    private final Map<String, Object> response = new HashMap<>();

    @GetMapping("/all")
    @Operation(
            summary = "Obtiene todos los equipos",
            description = "Este endpoint permite obtener una lista de todos los equipos."
    )
    public ResponseEntity<Map<String, Object>> getEquipos(@RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Equipo> equipos = equipoRepository.findAllBy();

        response.put("equipos", equipos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtiene un equipo por ID",
            description = "Este endpoint permite obtener un equipo especificado por su ID."
    )
    public ResponseEntity<Map<String, Object>> getEquipoById(@PathVariable int id, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Equipo equipo = equipoRepository.findEquipoById(id);
        if (equipo == null) {
            response.put("mensaje", "Equipo no existente con es id");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("equipo", equipo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("search/{nombre}")
    @Operation(
            summary = "Obtiene un equipo por nombre",
            description = "Este endpoint permite buscar un equipo por su nombre completo. Busca coincidencias parciales dentro del nombre."
    )
    public ResponseEntity<Map<String, Object>> getEquipoByName(@PathVariable String nombre, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Equipo> equipo = equipoRepository.findEquipoByNombreCompletoContainingIgnoreCase(nombre);
        if (equipo.isEmpty()) {
            response.put("mensaje", "Equipo no existente con ese nombre");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("equipo", equipo);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("search/alias/{alias}")
    @Operation(
            summary = "Obtiene un equipo por alias",
            description = "Este endpoint permite buscar un equipo por su alias."
    )
    public ResponseEntity<Map<String, Object>> getEquipoByAlias(@PathVariable String alias, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Equipo equipo = equipoRepository.findEquipoByAliasEqualsIgnoreCase(alias);
        if (equipo == null) {
            response.put("mensaje", "Equipo no existente con ese alias");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("equipo", equipo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    };

    @GetMapping("search/motor/{motor}")
    @Operation(
            summary = "Obtiene un equipo por motor",
            description = "Este endpoint permite obtener los equipos que utilizan un motor específico cuyo nombre comience con el valor proporcionado."
    )
    public ResponseEntity<Map<String, Object>> getEquipoByMotor(@PathVariable String motor, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Equipo> equipo = equipoRepository.findEquipoByMotorIsStartingWithIgnoreCase(motor);
        if (equipo.isEmpty()) {
            response.put("mensaje", "No hay equipos motorizados por " + motor);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("equipo", equipo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    };

    @Operation(
            summary = "Obtiene equipos por cantidad de campeonatos",
            description = "Este endpoint permite obtener una lista de equipos que hayan ganado un número de campeonatos constructores mayor o igual al especificado."
    )
    @GetMapping("search/campeonatos/{campeonatos}")
    public ResponseEntity<Map<String, Object>> getEquiposByCampeonatos(@PathVariable int campeonatos, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Equipo> tempEquipo = equipoRepository.findAllBy();
        ArrayList<Equipo> equipos = new ArrayList<>();
        for (Equipo equipo : tempEquipo) {
            if (equipo.getCampeonatosConstructores() >= campeonatos) equipos.add(equipo);
        }

        if (equipos.isEmpty()) {
            response.put("mensaje", "No hay equipos que tengas esos campeonantos o mas");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("Equipo", equipos);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    //Los siguientos metodos solo los podra realizar un usuario administrador

    final String USERTYPE = "ROOT";

    @PostMapping(value = "/add")

    @Operation(
            summary = "Agrega un nuevo equipo",
            description = "Este endpoint permite agregar un nuevo equipo a la base de datos. Solo un usuario administrador puede realizar esta acción."
    )
    public ResponseEntity<Map<String, Object>> addEquipo(@RequestHeader("Authorization") String authToken, @RequestBody Equipo equipo) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);

        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (!temp.getTipoUser().equals(USERTYPE)) {
            response.put("mensaje", "Esta operacion solo la puede realizar un administrador");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        //Aqui genero un id que siempre sera la id mas grande mas 1
        int id = 0;
        ArrayList<Equipo> equipos = equipoRepository.findAllBy();
        for (Equipo e : equipos) {
            if (e.getId() > id) id = e.getId();
        }
        id++;

        equipo.setId(id);
        equipoRepository.save(equipo);
        response.put("mensaje", "Equipo creado correctamente");
        response.put("resumen_equipo", equipo);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping(value = "/update/{id}")

    @Operation(
            summary = "Actualiza un equipo",
            description = "Este endpoint permite actualizar los detalles de un equipo existente. Solo un usuario administrador puede realizar esta acción."
    )
    public ResponseEntity<Map<String, Object>> updateEquipo(@RequestHeader("Authorization") String authToken, @PathVariable int id, @RequestBody Equipo equipo) {
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

        Equipo tempEquipo = equipoRepository.findEquipoById(id);

        if (tempEquipo == null) {
            response.put("mensaje", "Equipo no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        tempEquipo.setNombreCompleto(equipo.getNombreCompleto() != null ? equipo.getNombreCompleto() : tempEquipo.getNombreCompleto());
        tempEquipo.setAlias(equipo.getAlias() != null ? equipo.getAlias() : tempEquipo.getAlias());
        tempEquipo.setSede(equipo.getSede() != null ? equipo.getSede() : tempEquipo.getSede());
        tempEquipo.setMotor(equipo.getMotor() != null ? equipo.getMotor() : tempEquipo.getMotor());
        tempEquipo.setChasis(equipo.getChasis() != null ? equipo.getChasis() : tempEquipo.getChasis());
        tempEquipo.setCampeonatosConstructores(equipo.getCampeonatosConstructores() != 0 ? equipo.getCampeonatosConstructores() : tempEquipo.getCampeonatosConstructores());
        tempEquipo.setPiloto1(equipo.getPiloto1() != null ? equipo.getPiloto1() : tempEquipo.getPiloto1());
        tempEquipo.setPiloto2(equipo.getPiloto2() != null ? equipo.getPiloto2() : tempEquipo.getPiloto2());
        tempEquipo.setImg(equipo.getImg() != null ? equipo.getImg() : tempEquipo.getImg());

        equipoRepository.save(tempEquipo);

        response.put("mensaje", "Equipo actualizado correctamente");
        response.put("resumen_equipo", tempEquipo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(
            summary = "Elimina un equipo",
            description = "Este endpoint permite eliminar un equipo de la base de datos. Solo un usuario administrador puede realizar esta acción."
    )

    public ResponseEntity<Map<String, Object>> deleteEquipo(@RequestHeader("Authorization") String authToken, @PathVariable int id) {

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

        Equipo tempEquipo = equipoRepository.findEquipoById(id);

        if (tempEquipo == null) {
            response.put("mensaje", "Equipo no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        equipoRepository.deleteById(id);
        response.put("mensaje", "Equipo eliminado correctamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
