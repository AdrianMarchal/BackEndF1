package BackEndF1.models;


import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "pilotos")
@TypeAlias("piloto")
public class Pilotos {
    @Id
    private int id;
    private String nombre;
    private String img;
    private String nacionalidad;
    private int edad;
    @Field("fecha_nacimiento")
    private String fechaNacimiento;
    private int carreras;
    private int victorias;
    private int podios;
    private int poles;
    @Field("campeonatos_mundiales")
    private int campeonatosMundiales;
    @Field("equipo_actual")
    private String equipoActual;
    @Field("numero_f1")
    private int numeroF1;
    private String bandera;

    public Pilotos(int id, String nombre, String img, String nacionalidad, int edad, String fechaNacimiento, int carreras, int victorias, int podios, int poles, int campeonatosMundiales, String equipoActual, int numeroF1, String bandera) {
        this.id = id;
        this.nombre = nombre;
        this.img = img;
        this.nacionalidad = nacionalidad;
        this.edad = edad;
        this.fechaNacimiento = fechaNacimiento;
        this.carreras = carreras;
        this.victorias = victorias;
        this.podios = podios;
        this.poles = poles;
        this.campeonatosMundiales = campeonatosMundiales;
        this.equipoActual = equipoActual;
        this.numeroF1 = numeroF1;
        this.bandera = bandera;
    }

    public Pilotos() {}

    public String getEquipoActual() {
        return equipoActual;
    }

    public void setEquipoActual(String equipoActual) {
        this.equipoActual = equipoActual;
    }

    public int getCampeonatosMundiales() {
        return campeonatosMundiales;
    }

    public void setCampeonatosMundiales(int campeonatosMundiales) {
        this.campeonatosMundiales = campeonatosMundiales;
    }

    public int getPoles() {
        return poles;
    }

    public void setPoles(int poles) {
        this.poles = poles;
    }

    public int getPodios() {
        return podios;
    }

    public void setPodios(int podios) {
        this.podios = podios;
    }

    public int getVictorias() {
        return victorias;
    }

    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }

    public int getCarreras() {
        return carreras;
    }

    public void setCarreras(int carreras) {
        this.carreras = carreras;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroF1() {
        return numeroF1;
    }

    public void setNumeroF1(int numeroF1) {
        this.numeroF1 = numeroF1;
    }

    public String getBandera() {
        return bandera;
    }

    public void setBandera(String bandera) {
        this.bandera = bandera;
    }

    @Override
    public String toString() {
        return "Pilotos{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", img='" + img + '\'' +
                ", nacionalidad='" + nacionalidad + '\'' +
                ", edad=" + edad +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", carreras=" + carreras +
                ", victorias=" + victorias +
                ", podios=" + podios +
                ", poles=" + poles +
                ", campeonatosMundiales=" + campeonatosMundiales +
                ", equipoActual='" + equipoActual + '\'' +
                '}';
    }
}
