package ec.edu.utn.golmundial.dto;

/**
 * Envoltorio simple para mensajes de error o de información
 * devueltos por la API (RNF10: mensajes claros de error).
 */
public class MensajeDTO {

    private String mensaje;

    public MensajeDTO() {}

    public MensajeDTO(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
