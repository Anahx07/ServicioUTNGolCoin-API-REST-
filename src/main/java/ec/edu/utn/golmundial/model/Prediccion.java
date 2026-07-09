package ec.edu.utn.golmundial.model;

import ec.edu.utn.golmundial.model.enums.EstadoPrediccion;
import ec.edu.utn.golmundial.model.enums.Pronostico;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "predicciones")
public class Prediccion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billetera_id", nullable = false)
    private Billetera billetera;

    @Column(name = "partido_id", nullable = false)
    private Long partidoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Pronostico pronostico;

    @Column(name = "monto_apostado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoApostado;

    @Column(name = "cuota_aplicada", nullable = false, precision = 4, scale = 2)
    private BigDecimal cuotaAplicada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPrediccion estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Billetera getBilletera() { return billetera; }
    public void setBilletera(Billetera billetera) { this.billetera = billetera; }

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