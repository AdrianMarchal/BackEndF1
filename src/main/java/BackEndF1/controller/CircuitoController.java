package BackEndF1.controller;


import BackEndF1.models.Circuitos;
import BackEndF1.models.User;
import BackEndF1.repositories.CircuitoRepositry;
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
@RequestMapping(value = "/api/v1/circuitos")
@Tag(name = "Circuitos", description = "Operaciones con los circuito")
public class CircuitoController {
    @Autowired
    private CircuitoRepositry circuitoRepository;

    @Autowired
    private UserRepository userRepository;

    private final Map<String, Object> response = new HashMap<>();

    @GetMapping("/all")
    @Operation(
            summary = "Obtiene todos los circuitos",
            description = "Este endpoint permite obtener una lista de todos los circuitos."
    )
    public ResponseEntity<Map<String, Object>> getEquipos(@RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Circuitos> circuitos = circuitoRepository.findAllBy();

        response.put("circuitos", circuitos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // Los siguientes métodos solo los podrá realizar un usuario administrador
    final String USERTYPE = "ROOT";

    // Agregar un nuevo circuito
    @PostMapping(value = "/add")
    @Operation(
            summary = "Añadir un nuevo circuito",
            description = "Este endpoint permite añadir un nuevo circuito al sistema. Solo los administradores pueden realizar esta operación.")
    public ResponseEntity<Map<String, Object>> addCircuito(@RequestHeader("Authorization") String authToken, @RequestBody Circuitos circuito) {
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

        // Verificar que todos los campos requeridos están presentes
        if (circuito.getLugar() == null || circuito.getLugar().isEmpty() ||
                circuito.getImg() == null || circuito.getImg().isEmpty() ||
                circuito.getNombreCircuito() == null || circuito.getNombreCircuito().isEmpty() ||
                circuito.getPrimerGranPremio() <= 0 ||
                circuito.getVueltas() <= 0 ||
                circuito.getKm() <= 0 ||
                circuito.getKmGranPremio() <= 0 ||
                circuito.getRecord() == null || circuito.getRecord().isEmpty() ||
                circuito.getNombrePiloto() == null || circuito.getNombrePiloto().isEmpty() ||
                circuito.getYearRecord() <= 0 ||
                circuito.getImgCompleta() == null || circuito.getImgCompleta().isEmpty()) {
            response.put("mensaje", "Faltan campos obligatorios o contienen valores inválidos");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Generar un ID único
        int id = 0;
        ArrayList<Circuitos> circuitos = circuitoRepository.findAllBy();
        for (Circuitos c : circuitos) {
            if (c.getId() > id) id = c.getId();
        }
        id++;

        circuito.setId(id);
        circuitoRepository.save(circuito);
        response.put("mensaje", "Circuito creado correctamente");
        response.put("resumen_circuito", circuito);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Actualizar los datos de un circuito
    @PutMapping(value = "/update/{id}")
    @Operation(
            summary = "Actualizar los datos de un circuito",
            description = "Este endpoint permite actualizar los datos de un circuito existente. Solo los administradores pueden realizar esta operación.")
    public ResponseEntity<Map<String, Object>> updateCircuito(@RequestHeader("Authorization") String authToken, @PathVariable int id, @RequestBody Circuitos circuito) {
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

        Circuitos tempCircuito = circuitoRepository.findCircuitosById(id);

        if (tempCircuito == null) {
            response.put("mensaje", "Circuito no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Actualizar los campos del circuito
        tempCircuito.setLugar(circuito.getLugar() != null ? circuito.getLugar() : tempCircuito.getLugar());
        tempCircuito.setImg(circuito.getImg() != null ? circuito.getImg() : tempCircuito.getImg());
        tempCircuito.setNombreCircuito(circuito.getNombreCircuito() != null ? circuito.getNombreCircuito() : tempCircuito.getNombreCircuito());
        tempCircuito.setPrimerGranPremio(circuito.getPrimerGranPremio() > 0 ? circuito.getPrimerGranPremio() : tempCircuito.getPrimerGranPremio());
        tempCircuito.setVueltas(circuito.getVueltas() > 0 ? circuito.getVueltas() : tempCircuito.getVueltas());
        tempCircuito.setKm(circuito.getKm() > 0 ? circuito.getKm() : tempCircuito.getKm());
        tempCircuito.setKmGranPremio(circuito.getKmGranPremio() > 0 ? circuito.getKmGranPremio() : tempCircuito.getKmGranPremio());
        tempCircuito.setRecord(circuito.getRecord() != null ? circuito.getRecord() : tempCircuito.getRecord());
        tempCircuito.setNombrePiloto(circuito.getNombrePiloto() != null ? circuito.getNombrePiloto() : tempCircuito.getNombrePiloto());
        tempCircuito.setYearRecord(circuito.getYearRecord() > 0 ? circuito.getYearRecord() : tempCircuito.getYearRecord());
        tempCircuito.setImgCompleta(circuito.getImgCompleta() != null ? circuito.getImgCompleta() : tempCircuito.getImgCompleta());

        circuitoRepository.save(tempCircuito);

        response.put("mensaje", "Circuito actualizado correctamente");
        response.put("resumen_circuito", tempCircuito);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Eliminar un circuito
    @DeleteMapping(value = "/delete/{id}")
    @Operation(
            summary = "Eliminar un circuito",
            description = "Este endpoint permite eliminar un circuito del sistema. Solo los administradores pueden realizar esta operación.")
    public ResponseEntity<Map<String, Object>> deleteCircuito(@RequestHeader("Authorization") String authToken, @PathVariable int id) {
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

        Circuitos tempCircuito = circuitoRepository.findCircuitosById(id);

        if (tempCircuito == null) {
            response.put("mensaje", "Circuito no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        circuitoRepository.deleteById(id);
        response.put("mensaje", "Circuito eliminado correctamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
