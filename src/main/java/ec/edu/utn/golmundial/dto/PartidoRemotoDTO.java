package ec.edu.utn.golmundial.dto;

import java.time.Instant;

/**
 * Representa la respuesta de GET /partidos/{id} del Servicio de Estadísticas.
 * Contrato confirmado con el equipo (2026-07-09):
 *   - Endpoint: GET /partidos/{id}
 *   - Campo de fecha: "fechaInicio", formato ISO-8601 (ej: 2026-07-09T19:30:00Z)
 *
 * IMPORTANTE: si el nombre real del campo o el formato cambia del lado de
 * Estadísticas, hay que actualizar esta clase (y avisar en el canal del equipo).
 */
public class PartidoRemotoDTO {

    private Instant fechaInicio;

    public PartidoRemotoDTO() {}

    public Instant getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Instant fechaInicio) { this.fechaInicio = fechaInicio; }
}
