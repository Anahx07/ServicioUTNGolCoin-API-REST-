package ec.edu.utn.golmundial.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    private String clave;
    private String valor;
    private String descripcion;

    // Constructores
    public Configuracion() {}

    public Configuracion(String clave, String valor, String descripcion) {
        this.clave = clave;
        this.valor = valor;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}