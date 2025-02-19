package BackEndF1.repositories;

import BackEndF1.models.Pilotos;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface PilotoRepository extends MongoRepository<Pilotos, String> {
    ArrayList<Pilotos> findAllBy();
    Pilotos findPilotosById(int id);
    Pilotos findPilotosByNombre(String nombre);
    ArrayList<Pilotos> findPilotosByNacionalidadEqualsIgnoreCase(String nacionalidad);
    void deleteById(int id);
    ArrayList<Pilotos> findPilotosByEquipoActualContainingIgnoreCase(String equipoActual);

    ArrayList<Pilotos> findByNombreStartingWithIgnoreCase(String nombre);

    ArrayList<Pilotos> findByNombreContainingIgnoreCase(String nombre);
}
