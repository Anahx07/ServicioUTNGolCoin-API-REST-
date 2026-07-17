package ec.edu.utn.golmundial.service;

import ec.edu.utn.golmundial.model.Billetera;
import ec.edu.utn.golmundial.model.Configuracion;
import ec.edu.utn.golmundial.model.Transaccion;
import ec.edu.utn.golmundial.model.enums.TipoTransaccion;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;


@Stateless
@Transactional
public class BilleteraService {

    private static final BigDecimal BONO_BIENVENIDA = new BigDecimal("10.00");
    private static final BigDecimal BONO_BANCARROTA = new BigDecimal("1.00");

    @PersistenceContext(unitName = "UtnGolCoinPU")
    private EntityManager em;

    /**
     * RF01.
     * Crea la billetera de un usuario nuevo y acredita el bono de bienvenida.
     */
    /**
     * RF01.
     * Crea la billetera de un usuario nuevo y acredita el bono de bienvenida.
     */
    public Billetera crearBilletera(Long usuarioId) {
        // 1. Verificar si ya existe una billetera para este usuario
        try {
            Billetera existente = consultarBilletera(usuarioId);
            if (existente != null) {
                throw new IllegalArgumentException("Ya existe una billetera para este usuario.");
            }
        } catch (IllegalArgumentException e) {
            // Si capturamos un IllegalArgumentException de 'consultarBilletera',
            // significa que NO existe la billetera. Solo en este caso podemos continuar.
            if (!e.getMessage().contains("No existe billetera")) {
                // Si es la excepción de "Ya existe...", la dejamos pasar para que llegue al controlador
                throw e;
            }
        }

        // 2. Crear la nueva billetera con el Bono de Bienvenida
        Billetera nueva = new Billetera();
        nueva.setUsuarioId(usuarioId);
        nueva.setSaldo(BONO_BIENVENIDA); // Le asignamos los 10.00 UTNGolCoins
        em.persist(nueva);

        // 3. Registrar el movimiento en el ledger (Transacción)
        Transaccion transaccion = new Transaccion();
        transaccion.setBilletera(nueva);
        transaccion.setTipo(TipoTransaccion.BONO_BIENVENIDA); // Asegúrate de tener este Enum
        transaccion.setMonto(BONO_BIENVENIDA);
        em.persist(transaccion);

        return nueva;
    }
    


    /**
     * RF13.
     * Consulta la billetera de un usuario.
     */
    public Billetera consultarBilletera(Long usuarioId) {

        try {

            return em.createQuery(
                    "SELECT b FROM Billetera b WHERE b.usuarioId = :usuarioId",
                    Billetera.class)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();

        } catch (NoResultException e) {

            throw new IllegalArgumentException(
                    "No existe billetera para el usuario " + usuarioId);

        }
    }
    /**
 * RF20.
 * Bono de bancarrota.
 *
 * El usuario puede reclamar 1 UTNGolCoin únicamente cuando:
 * - Su saldo es exactamente 0.
 * - Han pasado al menos 2 minutos desde el último bono recibido.
 */
/**
     * RF20.
     * Bono de bancarrota.
     *
     * El usuario puede reclamar 1 UTNGolCoin únicamente cuando:
     * - Su saldo es exactamente 0.
     * - Ha pasado el tiempo configurado en la base de datos desde el último bono recibido.
     */
    public void procesarBonoBancarrota(Long usuarioId) {

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

        // Solo si el saldo es exactamente cero
        if (billetera.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException(
                    "Solo puedes reclamar el bono cuando tu saldo sea exactamente 0.");
        }

        // Buscar el último bono de bancarrota entregado
        List<Transaccion> bonos = em.createQuery(
                "SELECT t FROM Transaccion t " +
                "WHERE t.billetera.id = :billeteraId " +
                "AND t.tipo = :tipo " +
                "ORDER BY t.fechaCreacion DESC",
                Transaccion.class)
                .setParameter("billeteraId", billetera.getId())
                .setParameter("tipo", TipoTransaccion.BONO_BANCARROTA)
                .setMaxResults(1)
                .getResultList();

        // Si ya recibió un bono antes, validar tiempo desde la base de datos
        if (!bonos.isEmpty()) {

            Transaccion ultimoBono = bonos.get(0);

            // Obtenemos los minutos configurados
            int minutosEspera = obtenerMinutosEsperaBono();

            LocalDateTime disponible =
                    ultimoBono.getFechaCreacion().plusMinutes(minutosEspera);

            if (LocalDateTime.now().isBefore(disponible)) {

                throw new IllegalStateException(
                        "Debes esperar " + minutosEspera + " minutos para volver a reclamar el bono.");
            }
        }

        // Acreditar el bono
        billetera.setSaldo(
                billetera.getSaldo().add(BONO_BANCARROTA));

        em.merge(billetera);

        // Registrar movimiento en el ledger
        Transaccion transaccion = new Transaccion();
        transaccion.setBilletera(billetera);
        transaccion.setTipo(TipoTransaccion.BONO_BANCARROTA);
        transaccion.setMonto(BONO_BANCARROTA);

        em.persist(transaccion);
    }

    /**
     * Método auxiliar para buscar el tiempo de espera configurado en la DB.
     */
    private int obtenerMinutosEsperaBono() {
        try {
            // Importante: Asegúrate de tener importada la clase Configuracion arriba
            ec.edu.utn.golmundial.model.Configuracion config = 
                em.find(ec.edu.utn.golmundial.model.Configuracion.class, "TIEMPO_ESPERA_BONO_MINUTOS");
            
            if (config != null) {
                return Integer.parseInt(config.getValor());
            }
        } catch (Exception e) {
            // Si hay un error, dejamos 2 minutos por defecto
        }
        return 2; 
    }

} // Fin de la clase BilleteraService