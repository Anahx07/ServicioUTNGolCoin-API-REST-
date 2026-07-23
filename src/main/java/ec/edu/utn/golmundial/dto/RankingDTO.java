package ec.edu.utn.golmundial.dto;
import java.math.BigDecimal;
public class RankingDTO {
    private Long usuarioId;
    private BigDecimal totalApostado;
    public RankingDTO(Long usuarioId, BigDecimal totalApostado) {
        this.usuarioId = usuarioId;
        this.totalApostado = totalApostado;
    }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public BigDecimal getTotalApostado() { return totalApostado; }
    public void setTotalApostado(BigDecimal totalApostado) { this.totalApostado = totalApostado; }
}
