package ec.edu.utn.golmundial.service;

import ec.edu.utn.golmundial.model.Billetera;
import ec.edu.utn.golmundial.model.Transaccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

@Stateless
public class TransaccionService {

    @PersistenceContext(unitName = "UtnGolCoinPU")
    private EntityManager em;

    /**
     * Obtiene el historial transaccional (Ledger) filtrando por el ID de usuario de .NET.
     * * @param usuarioId ID del usuario.
     * @return Lista de transacciones ordenadas de la más reciente a la más antigua.
     */
    public List<Transaccion> obtenerHistorialPorUsuario(Long usuarioId) {
        try {
            // Buscamos la billetera usando el usuario_id que es el puente con .NET
            Billetera billetera = em.createQuery(
                "SELECT b FROM Billetera b WHERE b.usuarioId = :usuarioId", Billetera.class)
                .setParameter("usuarioId", usuarioId)
                .getSingleResult();

            // Retornamos las transacciones usando el atributo correcto: fechaHora
            return em.createQuery(
                "SELECT t FROM Transaccion t WHERE t.billetera.id = :billeteraId ORDER BY t.fechaCreacion DESC", Transaccion.class)
                .setParameter("billeteraId", billetera.getId())
                .getResultList();

        } catch (NoResultException e) {
            // Si el usuario no tiene billetera asignada todavía, retornamos una lista vacía de forma segura
            return Collections.emptyList();
        }
    }
}