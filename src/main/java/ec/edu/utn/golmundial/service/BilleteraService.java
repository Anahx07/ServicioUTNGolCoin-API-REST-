package ec.edu.utn.golmundial.service;

import ec.edu.utn.golmundial.model.Billetera;
import ec.edu.utn.golmundial.model.Transaccion;
import ec.edu.utn.golmundial.model.enums.TipoTransaccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@Stateless
@Transactional
public class BilleteraService {

    private static final BigDecimal BONO_BIENVENIDA = new BigDecimal("10.00");

    @PersistenceContext(unitName = "UtnGolCoinPU")
    private EntityManager em;

    /**
     * Crea la billetera de un usuario nuevo y acredita el bono de bienvenida
     * como una transacción en el ledger, no como una simple asignación de saldo (RF01).
     * Lo invoca el Servicio de Estadísticas al registrar un usuario.
     */
    public Billetera crearBilletera(Long usuarioId) {

        boolean yaExiste = em.createQuery(
                "SELECT COUNT(b) FROM Billetera b WHERE b.usuarioId = :usuarioId", Long.class)
                .setParameter("usuarioId", usuarioId)
                .getSingleResult() > 0;

        if (yaExiste) {
            throw new IllegalArgumentException("Ya existe una billetera para este usuario.");
        }

        Billetera billetera = new Billetera(usuarioId, BigDecimal.ZERO);
        em.persist(billetera);

        Transaccion bono = new Transaccion();
        bono.setBilletera(billetera);
        bono.setTipo(TipoTransaccion.BONO_BIENVENIDA);
        bono.setMonto(BONO_BIENVENIDA);
        em.persist(bono);

        billetera.setSaldo(billetera.getSaldo().add(BONO_BIENVENIDA));
        em.merge(billetera);

        return billetera;
    }

    /**
     * Consulta la billetera (y por lo tanto el saldo) de un usuario (RF13).
     */
    public Billetera consultarBilletera(Long usuarioId) {
        try {
            return em.createQuery(
                    "SELECT b FROM Billetera b WHERE b.usuarioId = :usuarioId", Billetera.class)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("No existe billetera para el usuario " + usuarioId);
        }
    }
}
