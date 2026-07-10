package ec.edu.utn.golmundial.dto;

import ec.edu.utn.golmundial.model.enums.Pronostico;
import java.math.BigDecimal;

/**
 * Cuerpo esperado al registrar una predicción 1X2 (RF15).
 *
 * Decisión de arquitectura (confirmada con el equipo, 2026-07-09):
 * la hora de inicio del partido NO se recibe del cliente. Este servicio
 * la consulta directamente al Servicio de Estadísticas (fuente de verdad)
 * a través de {@link ec.edu.utn.golmundial.service.EstadisticasClient},
 * para no confiar en un dato que el frontend podría enviar alterado.
 * Ver esa clase para el manejo de contingencia si Estadísticas está caído.
 */
public class PrediccionRequest {

    private Long usuarioId;
    private Long partidoId;
    private Pronostico pronostico;
    private BigDecimal monto;
    private BigDecimal cuota;

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
}
