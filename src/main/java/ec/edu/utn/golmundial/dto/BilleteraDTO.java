package ec.edu.utn.golmundial.dto;

import ec.edu.utn.golmundial.model.Billetera;
import java.math.BigDecimal;

public class BilleteraDTO {

    private Long id;
    private Long usuarioId;
    private BigDecimal saldo;

    public BilleteraDTO() {}

    public BilleteraDTO(Billetera billetera) {
        this.id = billetera.getId();
        this.usuarioId = billetera.getUsuarioId();
        this.saldo = billetera.getSaldo();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}
