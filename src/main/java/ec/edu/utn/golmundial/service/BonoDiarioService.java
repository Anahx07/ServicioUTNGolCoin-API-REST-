package ec.edu.utn.golmundial.service;

import ec.edu.utn.golmundial.model.Billetera;
import ec.edu.utn.golmundial.model.BonoDiario;
import ec.edu.utn.golmundial.model.Transaccion;
import ec.edu.utn.golmundial.model.enums.TipoTransaccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Stateless
@Transactional
public class BonoDiarioService {

    private static final BigDecimal BONO_DIARIO = BigDecimal.ONE;

    @PersistenceContext(unitName = "UtnGolCoinPU")
    private EntityManager em;

    /**
     * Se invoca al iniciar sesión. Si el saldo del usuario es cero y todavía
     * no reclamó el bono hoy, le acredita 1 UTNGolCoin (RF20).
     * Si no corresponde (tiene saldo, o ya lo reclamó hoy), no hace nada
     * y retorna null.
     */
    public Transaccion otorgarBonoDiarioSiCorresponde(Long usuarioId) {

        Billetera billetera;
        try {
            billetera = em.createQuery(
                    "SELECT b FROM Billetera b WHERE b.usuarioId = :usuarioId", Billetera.class)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("No existe billetera para el usuario " + usuarioId);
        }

        if (billetera.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            return null; // Todavía tiene saldo, no corresponde el bono
        }

        LocalDate hoy = LocalDate.now();

        boolean yaReclamado = em.createQuery(
                "SELECT COUNT(bd) FROM BonoDiario bd " +
                "WHERE bd.usuarioId = :usuarioId AND bd.fecha = :hoy", Long.class)
                .setParameter("usuarioId", usuarioId)
                .setParameter("hoy", hoy)
                .getSingleResult() > 0;

        if (yaReclamado) {
            return null; // Ya se le entregó el bono hoy
        }

        // Registrar el control del bono diario
        em.persist(new BonoDiario(usuarioId, hoy));

        // Acreditar el saldo
        billetera.setSaldo(billetera.getSaldo().add(BONO_DIARIO));
        em.merge(billetera);

        // Registrar la transacción en el ledger
        Transaccion tx = new Transaccion();
        tx.setBilletera(billetera);
        tx.setTipo(TipoTransaccion.BONO_DIARIO);
        tx.setMonto(BONO_DIARIO);
        em.persist(tx);

        return tx;
    }
}
