package BackEndF1.repositories;

import BackEndF1.models.Circuitos;

import BackEndF1.models.Equipo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface CircuitoRepositry extends MongoRepository<Circuitos, String> {
    ArrayList<Circuitos> findAllBy();
    Circuitos findCircuitosById(int id);
    void deleteById(int id);
}
