package ec.edu.utn.golmundial.controller;

import ec.edu.utn.golmundial.dto.MensajeDTO;
import ec.edu.utn.golmundial.dto.PrediccionDTO;
import ec.edu.utn.golmundial.dto.PrediccionRequest;
import ec.edu.utn.golmundial.model.Prediccion;
import ec.edu.utn.golmundial.service.PrediccionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/predicciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrediccionController {

    @Inject
    private PrediccionService prediccionService;

    /**
     * Registra una predicción 1X2 (RF15), validando saldo (RF16),
     * cierre por hora de inicio (RF17) y unicidad por partido (RF18).
     */
    @POST
    public Response crearPrediccion(PrediccionRequest request) {
        try {
            Prediccion prediccion = prediccionService.registrarPrediccion(
                    request.getUsuarioId(),
                    request.getPartidoId(),
                    request.getPronostico(),
                    request.getMonto(),
                    request.getCuota());

            return Response.status(Response.Status.CREATED)
                    .entity(new PrediccionDTO(prediccion))
                    .build();

        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();
        }
    }

    /**
     * Lista las predicciones de un usuario con su estado (RF22).
     */
    @GET
    @Path("/usuario/{usuarioId}")
    public Response listarPorUsuario(@PathParam("usuarioId") Long usuarioId) {
        List<PrediccionDTO> predicciones = prediccionService.obtenerPrediccionesPorUsuario(usuarioId)
                .stream()
                .map(PrediccionDTO::new)
                .collect(Collectors.toList());

        if (predicciones.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok(predicciones).build();
    }
}
