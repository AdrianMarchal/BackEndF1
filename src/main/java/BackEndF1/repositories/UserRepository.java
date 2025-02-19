package BackEndF1.repositories;

import BackEndF1.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email) ;
    User findByAuthToken(String authToken);
}
