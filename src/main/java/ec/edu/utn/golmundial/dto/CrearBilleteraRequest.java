package ec.edu.utn.golmundial.dto;

/**
 * Cuerpo esperado al crear una billetera nueva.
 * Este endpoint lo invoca el Servicio de Estadísticas cuando un usuario se registra (RF01).
 */
public class CrearBilleteraRequest {

    private Long usuarioId;

    public CrearBilleteraRequest() {}

    public CrearBilleteraRequest(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
