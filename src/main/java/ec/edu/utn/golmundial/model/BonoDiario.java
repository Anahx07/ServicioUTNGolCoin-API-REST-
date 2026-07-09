package ec.edu.utn.golmundial.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Controla la entrega del bono diario anti-bancarrota (RF20):
 * un usuario con saldo en cero recibe 1 UTNGolCoin al iniciar sesión,
 * una única vez por día.
 */
@Entity
@Table(name = "bonos_diarios",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "fecha"}))
public class BonoDiario implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    public BonoDiario() {}

    public BonoDiario(Long usuarioId, LocalDate fecha) {
        this.usuarioId = usuarioId;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
