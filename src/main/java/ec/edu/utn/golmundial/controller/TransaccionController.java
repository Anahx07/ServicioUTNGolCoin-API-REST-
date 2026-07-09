package ec.edu.utn.golmundial.controller;

import ec.edu.utn.golmundial.dto.TransaccionDTO;
import ec.edu.utn.golmundial.model.Transaccion;
import ec.edu.utn.golmundial.service.TransaccionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/transacciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransaccionController {

    @Inject
    private TransaccionService transaccionService;

    /**
     * Obtiene el historial de transacciones (Ledger) de un usuario:
     * bonos, predicciones y premios (RF14).
     */
    @GET
    @Path("/usuario/{usuarioId}")
    public Response obtenerHistorial(@PathParam("usuarioId") Long usuarioId) {

        List<Transaccion> historial = transaccionService.obtenerHistorialPorUsuario(usuarioId);

        if (historial.isEmpty()) {
            return Response.noContent().build();
        }

        List<TransaccionDTO> dto = historial.stream()
                .map(TransaccionDTO::new)
                .collect(Collectors.toList());

        return Response.ok(dto).build();
    }
}
