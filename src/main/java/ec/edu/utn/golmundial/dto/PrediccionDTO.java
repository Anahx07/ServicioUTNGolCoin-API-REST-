package ec.edu.utn.golmundial.dto;

import ec.edu.utn.golmundial.model.Prediccion;
import ec.edu.utn.golmundial.model.enums.EstadoPrediccion;
import ec.edu.utn.golmundial.model.enums.Pronostico;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PrediccionDTO {

    private Long id;
    private Long partidoId;
    private Pronostico pronostico;
    private BigDecimal montoApostado;
    private BigDecimal cuotaAplicada;
    private EstadoPrediccion estado;
    private LocalDateTime fechaCreacion;

    public PrediccionDTO() {}

    public PrediccionDTO(Prediccion p) {
        this.id = p.getId();
        this.partidoId = p.getPartidoId();
        this.pronostico = p.getPronostico();
        this.montoApostado = p.getMontoApostado();
        this.cuotaAplicada = p.getCuotaAplicada();
        this.estado = p.getEstado();
        this.fechaCreacion = p.getFechaCreacion();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPartidoId() { return partidoId; }
    public void setPartidoId(Long partidoId) { this.partidoId = partidoId; }

    public Pronostico getPronostico() { return pronostico; }
    public void setPronostico(Pronostico pronostico) { this.pronostico = pronostico; }

    public BigDecimal getMontoApostado() { return montoApostado; }
    public void setMontoApostado(BigDecimal montoApostado) { this.montoApostado = montoApostado; }

    public BigDecimal getCuotaAplicada() { return cuotaAplicada; }
    public void setCuotaAplicada(BigDecimal cuotaAplicada) { this.cuotaAplicada = cuotaAplicada; }

    public EstadoPrediccion getEstado() { return estado; }
    public void setEstado(EstadoPrediccion estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
