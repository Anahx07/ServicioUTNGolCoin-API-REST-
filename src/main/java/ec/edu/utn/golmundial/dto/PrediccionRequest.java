package ec.edu.utn.golmundial.dto;

import ec.edu.utn.golmundial.model.enums.Pronostico;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cuerpo esperado al registrar una predicción 1X2 (RF15).
 *
 * NOTA DE ARQUITECTURA (pendiente de confirmar con el equipo, ver ADR):
 * el partido y su fecha/hora de inicio viven en la base del Servicio de
 * Estadísticas, no en esta. Por eso el frontend público debe enviar aquí
 * "fechaHoraInicioPartido" (obtenida al consultar el calendario), y este
 * servicio valida el cierre automático (RF17) comparándola con la hora
 * actual del servidor. Alternativa: este backend podría en cambio llamar
 * por REST al Servicio de Estadísticas para pedir esa fecha — se optó por
 * la opción del frontend por ser más simple y no acoplar los dos backends
 * en el camino síncrono de "crear predicción".
 */
public class PrediccionRequest {

    private Long usuarioId;
    private Long partidoId;
    private Pronostico pronostico;
    private BigDecimal monto;
    private BigDecimal cuota;
    private LocalDateTime fechaHoraInicioPartido;

    public PrediccionRequest() {}

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getPartidoId() { return partidoId; }
    public void setPartidoId(Long partidoId) { this.partidoId = partidoId; }

    public Pronostico getPronostico() { return pronostico; }
    public void setPronostico(Pronostico pronostico) { this.pronostico = pronostico; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public BigDecimal getCuota() { return cuota; }
    public void setCuota(BigDecimal cuota) { this.cuota = cuota; }

    public LocalDateTime getFechaHoraInicioPartido() { return fechaHoraInicioPartido; }
    public void setFechaHoraInicioPartido(LocalDateTime fechaHoraInicioPartido) { this.fechaHoraInicioPartido = fechaHoraInicioPartido; }
}
