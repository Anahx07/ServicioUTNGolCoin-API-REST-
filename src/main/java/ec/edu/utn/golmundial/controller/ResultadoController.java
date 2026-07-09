package ec.edu.utn.golmundial.controller;

import ec.edu.utn.golmundial.dto.MensajeDTO;
import ec.edu.utn.golmundial.dto.ResultadoRequest;
import ec.edu.utn.golmundial.service.ResultadoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Endpoint que consume el Servicio de Estadísticas (Jakarta EE o .NET,
 * según asignación del equipo) cuando se registra el resultado oficial
 * de un partido, para disparar la liquidación automática de premios
 * (RF12, RF19).
 */
@Path("/resultados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResultadoController {

    @Inject
    private ResultadoService resultadoService;

    @POST
    @Path("/liquidar")
    public Response liquidar(ResultadoRequest request) {
        try {
            resultadoService.liquidarPartido(request.getPartidoId(), request.getResultadoOficial());
            return Response.ok(new MensajeDTO("Predicciones liquidadas para el partido " + request.getPartidoId())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();
        }
    }
}
