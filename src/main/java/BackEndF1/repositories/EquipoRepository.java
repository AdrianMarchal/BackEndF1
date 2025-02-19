package BackEndF1.repositories;

import BackEndF1.models.Equipo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface EquipoRepository extends MongoRepository<Equipo, String> {
    ArrayList<Equipo> findAllBy();
    Equipo findEquipoById(int id);
    Equipo findEquipoByAliasEqualsIgnoreCase(String alias);

    ArrayList<Equipo> findEquipoByMotorEqualsIgnoreCase(String motor);
    ArrayList<Equipo> findEquipoByMotorIsStartingWithIgnoreCase(String motor);
    void deleteById(int id);
    ArrayList<Equipo> findEquipoByNombreCompletoContainingIgnoreCase(String nombre);
}
