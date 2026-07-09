package ec.edu.utn.golmundial.service;

import ec.edu.utn.golmundial.model.Billetera;
import ec.edu.utn.golmundial.model.Prediccion;
import ec.edu.utn.golmundial.model.Transaccion;
import ec.edu.utn.golmundial.model.enums.EstadoPrediccion;
import ec.edu.utn.golmundial.model.enums.Pronostico;
import ec.edu.utn.golmundial.model.enums.TipoTransaccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Stateless
@Transactional
public class ResultadoService {

    @PersistenceContext(unitName = "UtnGolCoinPU")
    private EntityManager em;

    public void liquidarPartido(Long partidoId, Pronostico resultadoOficial) {
        Objects.requireNonNull(resultadoOficial, "El resultado oficial no puede ser nulo.");

        List<Prediccion> predicciones = em.createQuery(
            "SELECT p FROM Prediccion p WHERE p.partidoId = :partidoId AND p.estado = :estado", Prediccion.class)
            .setParameter("partidoId", partidoId)
            .setParameter("estado", EstadoPrediccion.PENDIENTE)
            .getResultList();

        if (predicciones.isEmpty()) {
            return;
        }

        for (Prediccion prediccion : predicciones) {
            Billetera billetera = prediccion.getBilletera();

            if (prediccion.getPronostico() == resultadoOficial) {
                BigDecimal premio = prediccion.getMontoApostado().multiply(prediccion.getCuotaAplicada());
                
                prediccion.setEstado(EstadoPrediccion.GANADA);
                em.merge(prediccion);

                Transaccion tx = new Transaccion();
                tx.setBilletera(billetera);
                tx.setTipo(TipoTransaccion.PREMIO_PAGADO);
                tx.setMonto(premio);
                em.persist(tx);

                billetera.setSaldo(billetera.getSaldo().add(premio));
                em.merge(billetera);

            } else {
                prediccion.setEstado(EstadoPrediccion.PERDIDA);
                em.merge(prediccion);
            }
        }
    }
}
