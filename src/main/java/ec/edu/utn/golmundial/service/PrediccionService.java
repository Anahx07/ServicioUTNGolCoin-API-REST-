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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Stateless
@Transactional
public class PrediccionService {

    @PersistenceContext(unitName = "UtnGolCoinPU")
    private EntityManager em;

    /**
     * Registra una predicción aplicando las reglas del negocio.
     */
    public Prediccion registrarPrediccion(Long usuarioId,
                                          Long partidoId,
                                          Pronostico pronostico,
                                          BigDecimal monto,
                                          BigDecimal cuota,
                                          LocalDateTime fechaHoraInicioPartido) {

        // Validar datos obligatorios
        Objects.requireNonNull(pronostico, "Debe seleccionar un pronóstico válido.");

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        if (cuota == null || cuota.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cuota debe ser mayor que cero.");
        }

        // RF17: rechazar predicciones después de la hora de inicio del partido
        if (fechaHoraInicioPartido != null && LocalDateTime.now().isAfter(fechaHoraInicioPartido)) {
            throw new IllegalStateException(
                    "Las predicciones para este partido ya cerraron (inició el " + fechaHoraInicioPartido + ").");
        }

        // Buscar la billetera del usuario
        Billetera billetera = em.createQuery(
                "SELECT b FROM Billetera b WHERE b.usuarioId = :usuarioId",
                Billetera.class)
                .setParameter("usuarioId", usuarioId)
                .getSingleResult();

        // Validar saldo disponible
        if (billetera.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        // Verificar que no exista otra predicción para el mismo partido
        Long total = em.createQuery(
                "SELECT COUNT(p) FROM Prediccion p " +
                "WHERE p.billetera.id = :billeteraId " +
                "AND p.partidoId = :partidoId",
                Long.class)
                .setParameter("billeteraId", billetera.getId())
                .setParameter("partidoId", partidoId)
                .getSingleResult();

        if (total > 0) {
            throw new IllegalArgumentException(
                    "Ya existe una predicción registrada para este partido.");
        }

        // Descontar el saldo de la billetera
        billetera.setSaldo(billetera.getSaldo().subtract(monto));
        em.merge(billetera);

        // Registrar el movimiento en el ledger
        Transaccion tx = new Transaccion();
        tx.setBilletera(billetera);
        tx.setTipo(TipoTransaccion.APUESTA_REALIZADA);
        tx.setMonto(monto);
        em.persist(tx);

        // Guardar la predicción
        Prediccion prediccion = new Prediccion();
        prediccion.setBilletera(billetera);
        prediccion.setPartidoId(partidoId);
        prediccion.setPronostico(pronostico);
        prediccion.setMontoApostado(monto);
        prediccion.setCuotaAplicada(cuota);
        prediccion.setEstado(EstadoPrediccion.PENDIENTE);

        em.persist(prediccion);

        return prediccion;
    }

    /**
     * Obtiene todas las predicciones de un usuario, con su estado
     * (pendiente, ganada o perdida) (RF22).
     */
    public List<Prediccion> obtenerPrediccionesPorUsuario(Long usuarioId) {
        Billetera billetera = em.createQuery(
                "SELECT b FROM Billetera b WHERE b.usuarioId = :usuarioId", Billetera.class)
                .setParameter("usuarioId", usuarioId)
                .getSingleResult();
        return obtenerPrediccionesPorBilletera(billetera.getId());
    }

    /**
     * Obtiene todas las predicciones registradas para una billetera.
     */
    public List<Prediccion> obtenerPrediccionesPorBilletera(Long billeteraId) {
        return em.createQuery(
                "SELECT p FROM Prediccion p " +
                "WHERE p.billetera.id = :billeteraId " +
                "ORDER BY p.id DESC",
                Prediccion.class)
                .setParameter("billeteraId", billeteraId)
                .getResultList();
    }
}