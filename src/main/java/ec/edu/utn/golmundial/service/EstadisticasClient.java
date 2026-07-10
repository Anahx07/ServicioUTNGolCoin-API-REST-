package ec.edu.utn.golmundial.service;

import ec.edu.utn.golmundial.dto.PartidoRemotoDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cliente REST hacia el Servicio de Estadísticas, para no confiar en datos
 * que envíe el frontend en un flujo crítico (saldo/predicciones) (RF17).
 *
 * Contrato (confirmado con el equipo el 2026-07-09):
 *   GET {ESTADISTICAS_API_URL}/partidos/{id}  ->  { "fechaInicio": "2026-07-09T19:30:00Z" }
 *
 * Manejo de contingencia (RNF05 - degradación controlada):
 * si el Servicio de Estadísticas no responde (caído, timeout, error, JSON
 * inesperado), este cliente NO lanza excepción hacia arriba: devuelve
 * Optional.empty(). PrediccionService decide qué hacer con eso (ver su
 * javadoc): en este proyecto se optó por NO bloquear el negocio y dejar
 * pasar la predicción con una advertencia en el log, en vez de tumbar el
 * flujo completo por una dependencia caída.
 */
@ApplicationScoped
public class EstadisticasClient {

    private static final Logger LOG = Logger.getLogger(EstadisticasClient.class.getName());

    // Configurable por variable de entorno para no hardcodear el host/puerto
    // del otro backend. Ajusta el valor por defecto a como quede desplegado
    // el Servicio de Estadísticas en su máquina/contenedor.
    private static final String BASE_URL = System.getenv().getOrDefault(
            "ESTADISTICAS_API_URL", "http://localhost:8080/estadisticas-backend/api");

    private static final Duration TIMEOUT = Duration.ofSeconds(3);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    private final Jsonb jsonb = JsonbBuilder.create();

    /**
     * Consulta la fecha/hora de inicio de un partido en el Servicio de
     * Estadísticas. Devuelve Optional.empty() si no se pudo obtener por
     * cualquier motivo (servicio caído, timeout, respuesta inesperada).
     */
    public Optional<Instant> obtenerFechaInicioPartido(Long partidoId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/partidos/" + partidoId))
                    .timeout(TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOG.warning("Estadisticas respondió " + response.statusCode() +
                        " al consultar el partido " + partidoId + ". Se omite la validación de cierre (RF17).");
                return Optional.empty();
            }

            PartidoRemotoDTO partido = jsonb.fromJson(response.body(), PartidoRemotoDTO.class);

            if (partido == null || partido.getFechaInicio() == null) {
                LOG.warning("Respuesta de Estadisticas sin 'fechaInicio' para el partido " + partidoId +
                        ". Se omite la validación de cierre (RF17).");
                return Optional.empty();
            }

            return Optional.of(partido.getFechaInicio());

        } catch (Exception e) {
            // Cualquier falla (timeout, servicio caído, JSON mal formado, etc.)
            // cae aquí. Es la contingencia pedida: no tumbar el flujo de
            // predicciones porque el otro microservicio esté caído.
            LOG.log(Level.WARNING,
                    "No se pudo contactar al Servicio de Estadísticas para el partido " + partidoId +
                            ". Se omite la validación de cierre (RF17).", e);
            return Optional.empty();
        }
    }
}
