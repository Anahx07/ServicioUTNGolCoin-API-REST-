package ec.edu.utn.golmundial.service;

import ec.edu.utn.golmundial.model.Billetera;
import ec.edu.utn.golmundial.model.Prediccion;
import ec.edu.utn.golmundial.model.Transaccion;
import ec.edu.utn.golmundial.model.enums.EstadoPrediccion;
import ec.edu.utn.golmundial.model.enums.Pronostico;
import ec.edu.utn.golmundial.model.enums.TipoTransaccion;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
    @Inject
    private EstadisticasClient estadisticasClient; 

    /**
     * Registra una predicción.
     */
    public Prediccion registrarPrediccion(Long usuarioId,
                                          Long partidoId,
                                          Pronostico pronostico,
                                          BigDecimal monto,
                                          BigDecimal cuota) {

        Objects.requireNonNull(pronostico, "Debe seleccionar un pronóstico válido.");


        try {
        LocalDateTime fechaPartido = estadisticasClient.obtenerFechaPartido(partidoId);
        validarCierrePorHora(fechaPartido);
    } catch (Exception e) {
        throw new IllegalArgumentException("No se puede registrar la apuesta: " + e.getMessage());
    }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        if (cuota == null || cuota.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cuota debe ser mayor que cero.");
        }

        Billetera billetera;

        try {

            billetera = em.createQuery(
                    "SELECT b FROM Billetera b WHERE b.usuarioId = :usuarioId",
                    Billetera.class)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();

        } catch (NoResultException e) {

            throw new IllegalArgumentException(
                    "El usuario no tiene una billetera registrada.");
        }

        if (billetera.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        Long existe = em.createQuery(
                "SELECT COUNT(p) FROM Prediccion p " +
                        "WHERE p.billetera.id = :billeteraId " +
                        "AND p.partidoId = :partidoId",
                Long.class)
                .setParameter("billeteraId", billetera.getId())
                .setParameter("partidoId", partidoId)
                .getSingleResult();

        if (existe > 0) {
            throw new IllegalArgumentException(
                    "Ya existe una predicción registrada para este partido.");
        }

        billetera.setSaldo(
                billetera.getSaldo().subtract(monto));

        em.merge(billetera);

        Transaccion tx = new Transaccion();
        tx.setBilletera(billetera);
        tx.setTipo(TipoTransaccion.APUESTA_REALIZADA);
        tx.setMonto(monto.negate()); // Se registra como salida de dinero

        em.persist(tx);

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
     * Se utilizará cuando el router consulte la fecha del partido.
     */
    public void validarCierrePorHora(LocalDateTime fechaPartido) {

        if (fechaPartido == null) {
            return;
        }

        if (!LocalDateTime.now().isBefore(fechaPartido)) {
            throw new IllegalStateException(
                    "El partido ya comenzó. No se permiten más predicciones.");
        }
    }

    /**
     * Obtiene las predicciones de una billetera.
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

    /**
     * Obtiene las predicciones de un usuario.
     */
    public List<Prediccion> obtenerPrediccionesPorUsuario(Long usuarioId) {

        if (usuarioId == null) {
            throw new IllegalArgumentException(
                    "El usuario es obligatorio.");
        }

        try {

            Billetera billetera = em.createQuery(
                    "SELECT b FROM Billetera b WHERE b.usuarioId = :usuarioId",
                    Billetera.class)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();

            return obtenerPrediccionesPorBilletera(
                    billetera.getId());

        } catch (NoResultException e) {

            throw new IllegalArgumentException(
                    "El usuario no tiene una billetera registrada.");
        }
    }

}