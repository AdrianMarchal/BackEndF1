package BackEndF1.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "circuitos")
@TypeAlias("circuitos")
public class Circuitos {
    @Id
    private int id;
    private String lugar;
    private String bandera;
    private String dias;
    private String mes;
    private String nombrePremio;
    private String img;
    private String nombreCircuito;
    private int primerGranPremio;
    private int vueltas;
    private int km;
    private double kmGranPremio;
    private String record;
    private String nombrePiloto;
    private int yearRecord;
    private String imgCompleta;

    public Circuitos(int id, String lugar, String bandera, String dias, String mes, String nombrePremio, String img, String nombreCircuito, int primerGranPremio, int vueltas, int km, double kmGranPremio, String record, String nombrePiloto, int yearRecord, String imgCompleta) {
        this.id = id;
        this.lugar = lugar;
        this.bandera = bandera;
        this.dias = dias;
        this.mes = mes;
        this.nombrePremio = nombrePremio;
        this.img = img;
        this.nombreCircuito = nombreCircuito;
        this.primerGranPremio = primerGranPremio;
        this.vueltas = vueltas;
        this.km = km;
        this.kmGranPremio = kmGranPremio;
        this.record = record;
        this.nombrePiloto = nombrePiloto;
        this.yearRecord = yearRecord;
        this.imgCompleta = imgCompleta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getBandera() {
        return bandera;
    }

    public void setBandera(String bandera) {
        this.bandera = bandera;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getNombrePremio() {
        return nombrePremio;
    }

    public void setNombrePremio(String nombrePremio) {
        this.nombrePremio = nombrePremio;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNombreCircuito() {
        return nombreCircuito;
    }

    public void setNombreCircuito(String nombreCircuito) {
        this.nombreCircuito = nombreCircuito;
    }

    public int getPrimerGranPremio() {
        return primerGranPremio;
    }

    public void setPrimerGranPremio(int primerGranPremio) {
        this.primerGranPremio = primerGranPremio;
    }

    public int getVueltas() {
        return vueltas;
    }

    public void setVueltas(int vueltas) {
        this.vueltas = vueltas;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public double getKmGranPremio() {
        return kmGranPremio;
    }

    public void setKmGranPremio(double kmGranPremio) {
        this.kmGranPremio = kmGranPremio;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getNombrePiloto() {
        return nombrePiloto;
    }

    public void setNombrePiloto(String nombrePiloto) {
        this.nombrePiloto = nombrePiloto;
    }

    public int getYearRecord() {
        return yearRecord;
    }

    public void setYearRecord(int yearRecord) {
        this.yearRecord = yearRecord;
    }

    public String getImgCompleta() {
        return imgCompleta;
    }

    public void setImgCompleta(String imgCompleta) {
        this.imgCompleta = imgCompleta;
    }
}
