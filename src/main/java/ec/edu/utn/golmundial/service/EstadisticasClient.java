package ec.edu.utn.golmundial.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class EstadisticasClient {

    // La URL de la API de tu compañero apuntando al endpoint de partidos
    private static final String API_COMPANERO_URL = "http://192.168.0.11:5069/api/partidos/"; 

    /**
     * Consulta la fecha de inicio de un partido en la API de tu compañero.
     */
    public LocalDateTime obtenerFechaPartido(Long partidoId) {
        Client client = ClientBuilder.newClient();
        try {
            // Esto consultará, por ejemplo: http://192.168.0.11:5069/api/partidos/1
            Response response = client.target(API_COMPANERO_URL + partidoId + "/fecha")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String fechaString = response.readEntity(String.class); 
                
                // Convertimos el texto de la fecha a un objeto LocalDateTime
                return LocalDateTime.parse(fechaString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else {
                throw new IllegalStateException("No se pudo obtener la información del partido.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error de conexión con estadísticas: " + e.getMessage());
        } finally {
            client.close(); // Cerramos el cliente para liberar memoria
        }
    }
}
