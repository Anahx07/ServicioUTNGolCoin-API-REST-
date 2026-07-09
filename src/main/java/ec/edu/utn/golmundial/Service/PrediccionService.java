package ec.edu.utn.golmundial.service;

import ec.edu.utn.golmundial.model.Billetera;
import ec.edu.utn.golmundial.model.Prediccion;
import ec.edu.utn.golmundial.model.enums.EstadoPrediccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Stateless
@Transactional
public class PrediccionService {

    @PersistenceContext(unitName = "UtnGolCoinPU")
    private EntityManager em;

    public void registrarPrediccion(Prediccion prediccion) {
        Objects.requireNonNull(prediccion, "La predicción no puede ser nula.");
        
        Billetera billetera = em.find(Billetera.class, prediccion.getBilletera().getId());
        if (billetera == null) {
            throw new IllegalArgumentException("La billetera asociada no existe.");
        }

        if (billetera.getSaldo().compareTo(prediccion.getMontoApostado()) < 0) {
            throw new IllegalStateException("Saldo insuficiente en la billetera para realizar esta predicción.");
        }

        // Descontar el saldo de la billetera
        billetera.setSaldo(billetera.getSaldo().subtract(prediccion.getMontoApostado()));
        em.merge(billetera);

        // Guardar la predicción configurando su estado inicial
        prediccion.setEstado(EstadoPrediccion.PENDIENTE);
        prediccion.setBilletera(billetera);
        em.persist(prediccion);
    }

    public List<Prediccion> obtenerPrediccionesPorBilletera(Long billeteraId) {
        return em.createQuery(
            "SELECT p FROM Prediccion p WHERE p.billetera.id = :billeteraId", Prediccion.class)
            .setParameter("billeteraId", billeteraId)
            .getResultList();
    }
}