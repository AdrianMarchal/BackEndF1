package BackEndF1.controller;


import BackEndF1.models.Pilotos;
import BackEndF1.models.User;
import BackEndF1.repositories.PilotoRepository;
import BackEndF1.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping(value = "/api/v1/pilotos")
@Tag(name = "Pilotos", description = "Operaciones con los pilotos")
public class PilotosController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PilotoRepository pilotoRepository;

    @Autowired
    private UserRepository userRepository;


    private final Map<String, Object> response = new HashMap<>();

    @GetMapping("/all")
    @Operation(
            summary = "Obtiene todos los pilotos",
            description = "Este endpoint permite obtener todos los pilotos")
    public ResponseEntity<Map<String, Object>> getPilotos(@RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Pilotos> pilotos = pilotoRepository.findAllBy();

        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Obtiene un piloto por ID",
            description = "Este endpoint permite obtener un piloto a partir de su ID.")
    public ResponseEntity<Map<String, Object>> getPiloto(@PathVariable int id, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Pilotos piloto = pilotoRepository.findPilotosById(id);
        if (piloto == null) {
            response.put("mensaje", "Piloto no existente con es id");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", piloto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("search/")
    @Operation(
            summary = "Obtiene una lista de pilotos que cumplen con los filtros proporcionados",
            description = "Este endpoint permite realizar una búsqueda de pilotos basándose en los filtros que se pasan como parámetros. Se pueden combinar varios filtros como nacionalidad, equipo, victorias, podios, entre otros")
    public ResponseEntity<Map<String, Object>> getPilotoSearch(
            @RequestParam(required = false) String nacionalidad,
            @RequestParam(required = false) String equipo,
            @RequestParam(required = false) Integer carreras,
            @RequestParam(required = false) Integer victoria,
            @RequestParam(required = false) Integer podios,
            @RequestParam(required = false) Integer poles,
            @RequestParam(required = false) Integer campeonatos,
            @RequestParam String authToken) {

        Map<String, Object> response = new HashMap<>();

        // Verificar el token de autenticación
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token erróneo");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Si no se pasa ningún parámetro, devolver un error
        if (nacionalidad == null && equipo == null && carreras == null &&
                victoria == null && podios == null && poles == null && campeonatos == null) {
            response.put("ERROR", "No se ha aplicado ningún parámetro");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Construir la consulta dinámica utilizando MongoTemplate
        Query query = new Query();

        // Filtrar por nacionalidad, si se ha proporcionado
        if (nacionalidad != null && !nacionalidad.isEmpty()) {
            query.addCriteria(Criteria.where("nacionalidad").regex(nacionalidad, "i")); // "i" para insensible a mayúsculas/minúsculas
        }

        // Filtrar por equipo, si se ha proporcionado
        if (equipo != null && !equipo.isEmpty()) {
            query.addCriteria(Criteria.where("equipoActual").regex(equipo, "i"));
        }

        // Filtrar por carreras, si se ha proporcionado
        if (carreras != null) {
            query.addCriteria(Criteria.where("carreras").gte(carreras));
        }

        // Filtrar por victorias, si se ha proporcionado
        if (victoria != null) {
            query.addCriteria(Criteria.where("victorias").gte(victoria));
        }

        // Filtrar por podios, si se ha proporcionado
        if (podios != null) {
            query.addCriteria(Criteria.where("podios").gte(podios));
        }

        // Filtrar por poles, si se ha proporcionado
        if (poles != null) {
            query.addCriteria(Criteria.where("poles").gte(poles));
        }

        // Filtrar por campeonatos, si se ha proporcionado
        if (campeonatos != null) {
            query.addCriteria(Criteria.where("campeonatosMundiales").gte(campeonatos));
        }


        // Ejecutar la consulta y obtener la lista de pilotos
        ArrayList<Pilotos> pilotos = (ArrayList<Pilotos>) mongoTemplate.find(query, Pilotos.class);


        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos con esos requisitos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("pilotos", pilotos);


        return ResponseEntity.ok(response);
    }


    @GetMapping("search/nombreExacto/{nombre}")
    @Operation(
            summary = "Obtiene un piloto por nombre",
            description = "Este endpoint busca un piloto usando su nombre exacto. Es sensible a mayúsculas y minúsculas.")
    public ResponseEntity<Map<String, Object>> getPilotoByNombreExacto(@PathVariable String nombre, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Pilotos piloto = pilotoRepository.findPilotosByNombre(nombre);
        if (piloto == null) {
            response.put("mensaje", "Piloto no existente con ese nombre");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", piloto);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("search/nombre/{nombre}")
    @Operation(
            summary = "Obtiene una lista de pilotos cuyo nombre comienza con el valor proporcionado.",
            description = "Este endpoint realiza una búsqueda de pilotos cuyo nombre comience con el valor proporcionado. Si no se encuentran coincidencias exactas, la búsqueda se amplía para que busque coincidencias parciales dentro del nombre.")
    public ResponseEntity<Map<String, Object>> getPilotoByNombre(@PathVariable String nombre, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Pilotos> pilotos = pilotoRepository.findByNombreStartingWithIgnoreCase(nombre);

        // En el caso de que al buscar no haya ninguna coincidencia con el inicio del nombre
        // se hace una segunda busqueda para ver si contiene lo que se esta buscando
        if (pilotos.isEmpty()) {
            pilotos = pilotoRepository.findByNombreContainingIgnoreCase(nombre);
        }

        if (pilotos.isEmpty()) {
            response.put("mensaje", "No existen pilotos con el nombre o que contenga " + nombre);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @GetMapping("search/nacionalidad/{nacionalidad}")
    @Operation(
            summary = "Obtiene una lista de pilotos por su nacionalidad.",
            description = "Este endpoint permite buscar pilotos que coincidan con la nacionalidad proporcionada.")
    public ResponseEntity<Map<String, Object>> getPilotoByNacionalidad(@PathVariable String nacionalidad, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Pilotos> pilotos = pilotoRepository.findPilotosByNacionalidadEqualsIgnoreCase(nacionalidad);
        if (pilotos.isEmpty()) {
            response.put("mensaje", "No existen pilotos con esa nacionalidad");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @GetMapping("search/carreras/")
    @Operation(
            summary = "Obtiene una lista de pilotos con un número de carreras dentro de un rango especificado.",
            description = "Este endpoint permite buscar pilotos cuyas carreras estén dentro del rango de min a max.")
    public ResponseEntity<Map<String, Object>> getPilotoByCarreras(@RequestParam String authToken, @RequestParam Integer min, @RequestParam Integer max) {
        response.clear();

        if (authToken == null || authToken.isEmpty()) {
            response.put("mensaje", "API token es requerido");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        ArrayList<Pilotos> tempPilotos = pilotoRepository.findAllBy();
        ArrayList<Pilotos> pilotos = new ArrayList<>();
        for (Pilotos piloto : tempPilotos) {
            if (piloto.getCarreras() >= min && piloto.getCarreras() <= max) {
                pilotos.add(piloto);
            }
        }

        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos que tengan un número de carreras dentro del rango especificado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("search/carreras/{orden}")
    @Operation(
            summary = "Obtiene una lista de pilotos ordenados por el número de carreras, de acuerdo con el parámetro orden.",
            description = "Este endpoint devuelve una lista de pilotos ordenada por el número de carreras que han corrido, dependiendo del valor del parámetro orden. Se puede elegir entre ordenar de forma ascendente (min) o descendente (max).")
    public ResponseEntity<Map<String, Object>> getPilotoByOrden(@PathVariable String orden, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (orden == null || orden.isEmpty()) {
            response.put("mensaje", "Parametro orden es obligatorio");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (!orden.equals("max") && !orden.equals("min")) {
            response.put("mensaje", "Parametro orden incorrecto debe tomar solo los valores max o min");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }


        ArrayList<Pilotos> pilotos = pilotoRepository.findAllBy();
        if (orden.equals("max")) {
            pilotos.sort((p1, p2) -> Integer.compare(p2.getCarreras(), p1.getCarreras()));
        } else {
            pilotos.sort(Comparator.comparingInt(Pilotos::getCarreras));
        }
        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos disponibles");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("search/victorias/")
    @Operation(
            summary = "Obtiene una lista de pilotos cuyo número de victorias esté dentro de un rango específico.",
            description = "Este endpoint permite buscar pilotos cuyas victorias estén dentro del rango de min a max.")
    public ResponseEntity<Map<String, Object>> getPilotoByVictoria(@RequestParam String authToken, @RequestParam Integer min, @RequestParam Integer max) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Pilotos> tempPilotos = pilotoRepository.findAllBy();
        ArrayList<Pilotos> pilotos = new ArrayList<>();
        for (Pilotos piloto : tempPilotos) {
            if (piloto.getVictorias() >= min && piloto.getVictorias() <= max) {
                pilotos.add(piloto);
            }
        }

        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos que tengan un número de victorias dentro del rango especificado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("search/victorias/{orden}")
    @Operation(
            summary = "Obtiene una lista de pilotos ordenados por el número de victorias, de acuerdo con el parámetro orden.",
            description = "Este endpoint devuelve una lista de pilotos ordenada por el número de victorias que han conseguido, dependiendo del valor del parámetro orden. Se puede elegir entre ordenar de forma ascendente (min) o descendente (max).")
    public ResponseEntity<Map<String, Object>> getPilotoByVictorias(@PathVariable String orden, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (orden == null || orden.isEmpty()) {
            response.put("mensaje", "Parametro orden es obligatorio");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (!orden.equals("max") && !orden.equals("min")) {
            response.put("mensaje", "Parametro orden incorrecto debe tomar solo los valores max o min");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        ArrayList<Pilotos> pilotos = pilotoRepository.findAllBy();
        if (orden.equals("max")) {
            pilotos.sort((p1, p2) -> Integer.compare(p2.getVictorias(), p1.getVictorias()));
        } else {
            pilotos.sort(Comparator.comparingInt(Pilotos::getVictorias));
        }
        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos disponibles");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("search/podios/")
    @Operation(
            summary = "Obtiene una lista de pilotos cuyo número de podios esté dentro de un rango específico.",
            description = "Este endpoint permite buscar pilotos cuyos podios estén dentro del rango de min a max.")
    public ResponseEntity<Map<String, Object>> getPilotoByPodios(@RequestParam String authToken, @RequestParam Integer min, @RequestParam Integer max) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Pilotos> tempPilotos = pilotoRepository.findAllBy();
        ArrayList<Pilotos> pilotos = new ArrayList<>();
        for (Pilotos piloto : tempPilotos) {
            if (piloto.getPodios() >= min && piloto.getPodios() <= max) {
                pilotos.add(piloto);
            }
        }

        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos que tengan un número de podios dentro del rango especificado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("search/podios/{orden}")
    @Operation(
            summary = "Obtiene una lista de pilotos ordenados por el número de podios, de acuerdo con el parámetro orden",
            description = "Este endpoint devuelve una lista de pilotos ordenada por el número de podios que han conseguido, dependiendo del valor del parámetro orden. Se puede elegir entre ordenar de forma ascendente (min) o descendente (max).")
    public ResponseEntity<Map<String, Object>> getPilotoByPodiosOrden(@PathVariable String orden, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (orden == null || orden.isEmpty()) {
            response.put("mensaje", "Parametro orden es obligatorio");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (!orden.equals("max") && !orden.equals("min")) {
            response.put("mensaje", "Parametro orden incorrecto debe tomar solo los valores max o min");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        ArrayList<Pilotos> pilotos = pilotoRepository.findAllBy();
        if (orden.equals("max")) {
            pilotos.sort((p1, p2) -> Integer.compare(p2.getPodios(), p1.getPodios()));
        } else {
            pilotos.sort(Comparator.comparingInt(Pilotos::getPodios));
        }
        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos disponibles");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("search/poles/")
    @Operation(
            summary = "Obtiene una lista de pilotos cuyo número de poles esté dentro de un rango específico.",
            description = "Este endpoint permite buscar pilotos que tengan un número de poles dentro de un rango especificado entre min y max.")
    public ResponseEntity<Map<String, Object>> getPilotoByPoles(@RequestParam String authToken, @RequestParam Integer min, @RequestParam Integer max) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Pilotos> tempPilotos = pilotoRepository.findAllBy();
        ArrayList<Pilotos> pilotos = new ArrayList<>();
        for (Pilotos piloto : tempPilotos) {
            if (piloto.getPoles() >= min && piloto.getPoles() <= max) {
                pilotos.add(piloto);
            }
        }

        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos que tengan un número de poles dentro del rango especificado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("search/poles/{orden}")
    @Operation(
            summary = "Obtiene una lista de pilotos ordenados por el número de poles, de acuerdo con el parámetro orden.",
            description = "Este endpoint devuelve una lista de pilotos ordenada por el número de poles, dependiendo del parámetro orden. Se puede elegir entre ordenar de forma ascendente (min) o descendente (max).")
    public ResponseEntity<Map<String, Object>> getPilotoByPolesOrden(@PathVariable String orden, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (orden == null || orden.isEmpty()) {
            response.put("mensaje", "Parametro orden es obligatorio");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (!orden.equals("max") && !orden.equals("min")) {
            response.put("mensaje", "Parametro orden incorrecto debe tomar solo los valores max o min");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        ArrayList<Pilotos> pilotos = pilotoRepository.findAllBy();
        if (orden.equals("max")) {
            pilotos.sort((p1, p2) -> Integer.compare(p2.getPoles(), p1.getPoles()));
        } else {
            pilotos.sort(Comparator.comparingInt(Pilotos::getPoles));
        }
        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos disponibles");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("search/campeonatos/")
    @Operation(
            summary = "Obtiene una lista de pilotos cuyo número de campeonatos mundiales esté dentro de un rango específico.",
            description = "Este endpoint permite buscar pilotos que tengan un número de campeonatos mundiales dentro de un rango especificado entre min y max.")
    public ResponseEntity<Map<String, Object>> getPilotoByCampeonatosMundiales(@RequestParam String authToken, @RequestParam Integer min, @RequestParam Integer max) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Pilotos> tempPilotos = pilotoRepository.findAllBy();
        ArrayList<Pilotos> pilotos = new ArrayList<>();
        for (Pilotos piloto : tempPilotos) {
            if (piloto.getCampeonatosMundiales() >= min && piloto.getCampeonatosMundiales() <= max) {
                pilotos.add(piloto);
            }
        }

        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos que tengan un número de campeonatos mundiales dentro del rango especificado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("search/campeonatos/{orden}")
    @Operation(
            summary = "Obtiene una lista de pilotos ordenados por el número de campeonatos mundiales, de acuerdo con el parámetro orden.",
            description = "Este endpoint devuelve una lista de pilotos ordenada por el número de campeonatos mundiales, dependiendo del parámetro orden. Se puede elegir entre ordenar de forma ascendente (min) o descendente (max).")
    public ResponseEntity<Map<String, Object>> getPilotoByCampeonatosMundialesOrden(@PathVariable String orden, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (orden == null || orden.isEmpty()) {
            response.put("mensaje", "Parametro orden es obligatorio");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (!orden.equals("max") && !orden.equals("min")) {
            response.put("mensaje", "Parametro orden incorrecto debe tomar solo los valores max o min");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        ArrayList<Pilotos> pilotos = pilotoRepository.findAllBy();
        if (orden.equals("max")) {
            pilotos.sort((p1, p2) -> Integer.compare(p2.getCampeonatosMundiales(), p1.getCampeonatosMundiales()));
        } else {
            pilotos.sort(Comparator.comparingInt(Pilotos::getCampeonatosMundiales));
        }
        if (pilotos.isEmpty()) {
            response.put("mensaje", "No hay pilotos disponibles");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("pilotos", pilotos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("search/equipo/{equipo}")
    @Operation(
            summary = "Obtiene una lista de pilotos pertenecientes a un equipo específico.",
            description = "Este endpoint permite buscar pilotos que estén en el equipo proporcionado en el parámetro equipo.")
    public ResponseEntity<Map<String, Object>> getPilotoByEquipo(@PathVariable String equipo, @RequestParam String authToken) {
        response.clear();
        User temp = userRepository.findByAuthToken(authToken);
        if (temp == null) {
            response.put("mensaje", "API token errónea");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Pilotos> piloto = pilotoRepository.findPilotosByEquipoActualContainingIgnoreCase(equipo);
        if (piloto.isEmpty()) {
            response.put("mensaje", "No hay pilotos en la escuderia " + equipo);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("pilotos", piloto);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    //Los siguientos metodos solo los podra realizar un usuario administrador

    final String USERTYPE = "ROOT";

    @PostMapping(value = "/add")
    @Operation(
            summary = "Añadir un nuevo piloto",
            description = "Este endpoint permite añadir un nuevo piloto al sistema. Solo los administradores pueden realizar esta operación.")
    public ResponseEntity<Map<String, Object>> addPiloto(@RequestHeader("Authorization") String authToken, @RequestBody Pilotos piloto) {
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

        // Verificar que todos los campos requeridos están presentes
        if (piloto.getNombre() == null || piloto.getNombre().isEmpty() ||
                piloto.getImg() == null || piloto.getImg().isEmpty() ||
                piloto.getNacionalidad() == null || piloto.getNacionalidad().isEmpty() ||
                piloto.getEdad() <= 0 ||
                piloto.getFechaNacimiento() == null || piloto.getFechaNacimiento().isEmpty() ||
                piloto.getCarreras() < 0 ||
                piloto.getVictorias() < 0 ||
                piloto.getPodios() < 0 ||
                piloto.getPoles() < 0 ||
                piloto.getCampeonatosMundiales() < 0 ||
                piloto.getEquipoActual() == null || piloto.getEquipoActual().isEmpty()) {
            response.put("mensaje", "Faltan campos obligatorios o contienen valores inválidos");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Generar un ID único
        int id = 0;
        ArrayList<Pilotos> pilotos = pilotoRepository.findAllBy();
        for (Pilotos p : pilotos) {
            if (p.getId() > id) id = p.getId();
        }
        id++;

        piloto.setId(id);
        pilotoRepository.save(piloto);
        response.put("mensaje", "Piloto creado correctamente");
        response.put("resumen_piloto", piloto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping(value = "/update/{id}")
    @Operation(
            summary = "Actualizar los datos de un piloto",
            description = "Este endpoint permite actualizar los datos de un piloto existente. Solo los administradores pueden realizar esta operación.")
    public ResponseEntity<Map<String, Object>> updatePiloto(@RequestHeader("Authorization") String authToken, @PathVariable int id, @RequestBody Pilotos piloto) {
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

        Pilotos tempPiloto = pilotoRepository.findPilotosById(id);

        if (tempPiloto == null) {
            response.put("mensaje", "Piloto no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        tempPiloto.setNombre(piloto.getNombre() != null ? piloto.getNombre() : tempPiloto.getNombre());
        tempPiloto.setImg(piloto.getImg() != null ? piloto.getImg() : tempPiloto.getImg());
        tempPiloto.setNacionalidad(piloto.getNacionalidad() != null ? piloto.getNacionalidad() : tempPiloto.getNacionalidad());
        tempPiloto.setEdad(piloto.getEdad() != 0 ? piloto.getEdad() : tempPiloto.getEdad());
        tempPiloto.setFechaNacimiento(piloto.getFechaNacimiento() != null ? piloto.getFechaNacimiento() : tempPiloto.getFechaNacimiento());
        tempPiloto.setCarreras(piloto.getCarreras() != 0 ? piloto.getCarreras() : tempPiloto.getCarreras());
        tempPiloto.setVictorias(piloto.getVictorias() != 0 ? piloto.getVictorias() : tempPiloto.getVictorias());
        tempPiloto.setPodios(piloto.getPodios() != 0 ? piloto.getPodios() : tempPiloto.getPodios());
        tempPiloto.setPoles(piloto.getPoles() != 0 ? piloto.getPoles() : tempPiloto.getPoles());
        tempPiloto.setCampeonatosMundiales(piloto.getCampeonatosMundiales() != 0 ? piloto.getCampeonatosMundiales() : tempPiloto.getCampeonatosMundiales());
        tempPiloto.setEquipoActual(piloto.getEquipoActual() != null ? piloto.getEquipoActual() : tempPiloto.getEquipoActual());


        pilotoRepository.save(tempPiloto);

        response.put("mensaje", "Piloto actualizado correctamente");
        response.put("resumen_piloto", tempPiloto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping(value = "/delete/{id}")
    @Operation(
            summary = "Eliminar un piloto",
            description = "Este endpoint permite eliminar un piloto del sistema. Solo los administradores pueden realizar esta operación.")
    public ResponseEntity<Map<String, Object>> deletePiloto(@RequestHeader("Authorization") String authToken, @PathVariable int id) {

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

        Pilotos tempPiloto = pilotoRepository.findPilotosById(id);

        if (tempPiloto == null) {
            response.put("mensaje", "Piloto no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        pilotoRepository.deleteById(id);
        response.put("mensaje", "Piloto eliminado correctamente");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
