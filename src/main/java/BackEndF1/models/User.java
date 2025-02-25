package BackEndF1.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "user")
@TypeAlias("user")
public class User {

    @Id
    private String _id;
    private String name;
    private String password;
    private String email;
    private String authToken;
    private String tipoUser;


    //Este metodo genera un token unico para cada usuario
    private String generarApi_Token() {
        return UUID.randomUUID().toString();
    }

    // Constructor con parámetros
    public User(String name, String password, String email, String tipoUser) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.authToken = generarApi_Token();
        this.tipoUser = tipoUser;
    }

    public String getTipoUser() {
        return tipoUser;
    }

    public void setTipoUser(String tipoUser) {
        this.tipoUser = tipoUser;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", authToken='" + authToken + '\'' +
                ", tipoUser='" + tipoUser + '\'' +
                '}';
    }
}


