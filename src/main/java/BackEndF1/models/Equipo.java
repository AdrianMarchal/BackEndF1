package BackEndF1.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "equipos")
@TypeAlias("equipos")
public class Equipo {
    @Id
    private int id;
    private String nombreCompleto;
    private String alias;
    private String sede;
    private String motor;
    private String chasis;
    private int campeonatosConstructores;
    private String piloto1;
    private String piloto2;
    private String img;

    public Equipo(int id, String nombreCompleto, String alias, String sede, String motor, String chasis, int campeonatosConstructores, String piloto1, String piloto2, String img) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.alias = alias;
        this.sede = sede;
        this.motor = motor;
        this.chasis = chasis;
        this.campeonatosConstructores = campeonatosConstructores;
        this.piloto1 = piloto1;
        this.piloto2 = piloto2;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getChasis() {
        return chasis;
    }

    public void setChasis(String chasis) {
        this.chasis = chasis;
    }

    public int getCampeonatosConstructores() {
        return campeonatosConstructores;
    }

    public void setCampeonatosConstructores(int campeonatosConstructores) {
        this.campeonatosConstructores = campeonatosConstructores;
    }

    public String getPiloto1() {
        return piloto1;
    }

    public void setPiloto1(String piloto1) {
        this.piloto1 = piloto1;
    }

    public String getPiloto2() {
        return piloto2;
    }

    public void setPiloto2(String piloto2) {
        this.piloto2 = piloto2;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "Equipo{" +
                "id=" + id +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", alias='" + alias + '\'' +
                ", sede='" + sede + '\'' +
                ", motor='" + motor + '\'' +
                ", chasis='" + chasis + '\'' +
                ", campeonatosConstructores=" + campeonatosConstructores +
                ", piloto1='" + piloto1 + '\'' +
                ", piloto2='" + piloto2 + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
