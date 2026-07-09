package ec.edu.utn.golmundial.dto;

import ec.edu.utn.golmundial.model.Transaccion;
import ec.edu.utn.golmundial.model.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionDTO {

    private Long id;
    private TipoTransaccion tipo;
    private BigDecimal monto;
    private LocalDateTime fechaCreacion;

    public TransaccionDTO() {}

    public TransaccionDTO(Transaccion t) {
        this.id = t.getId();
        this.tipo = t.getTipo();
        this.monto = t.getMonto();
        this.fechaCreacion = t.getFechaCreacion();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoTransaccion getTipo() { return tipo; }
    public void setTipo(TipoTransaccion tipo) { this.tipo = tipo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
