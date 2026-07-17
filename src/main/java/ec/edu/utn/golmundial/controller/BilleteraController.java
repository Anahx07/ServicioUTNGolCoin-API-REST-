package ec.edu.utn.golmundial.controller;

import ec.edu.utn.golmundial.dto.BilleteraDTO;
import ec.edu.utn.golmundial.dto.CrearBilleteraRequest;
import ec.edu.utn.golmundial.dto.MensajeDTO;
import ec.edu.utn.golmundial.model.Billetera;
import ec.edu.utn.golmundial.service.BilleteraService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/billeteras")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BilleteraController {

    @Inject
    private BilleteraService billeteraService;

    /**
     * Crea la billetera de un usuario nuevo y registra
     * el bono de bienvenida.
     */
    @POST
    public Response crearBilletera(CrearBilleteraRequest request) {

        try {

            Billetera billetera =
                    billeteraService.crearBilletera(request.getUsuarioId());

            return Response.status(Response.Status.CREATED)
                    .entity(new BilleteraDTO(billetera))
                    .build();

        } catch (IllegalArgumentException e) {

            return Response.status(Response.Status.CONFLICT)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/ranking")
    public Response consultarRanking() {
        // Por ahora, solo devolvemos una respuesta vacía para probar el 200 OK
        return Response.ok("[]").build();
    }

    /**
     * Consulta el historial de movimientos de la billetera del usuario.
     */
    @GET
    @Path("/usuario/{usuarioId}/historial")
    public Response consultarHistorial(@PathParam("usuarioId") Long usuarioId) {
        try {
            // Aquí llamarías a tu servicio (asegúrate de tener este método en BilleteraService)
            // List<Movimiento> historial = billeteraService.obtenerHistorial(usuarioId);
            
            // Por ahora, para probar que la ruta responde, devolvemos un mensaje:
            return Response.ok(new MensajeDTO("Historial consultado exitosamente para usuario: " + usuarioId)).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();
        }
    }

    /**
     * Consulta el saldo actual del usuario.
     */
    @GET
    @Path("/usuario/{usuarioId}")
    public Response consultarSaldo(@PathParam("usuarioId") Long usuarioId) {

        try {

            Billetera billetera =
                    billeteraService.consultarBilletera(usuarioId);

            return Response.ok(new BilleteraDTO(billetera)).build();

        } catch (IllegalArgumentException e) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();
        }
    }

    /**
     * Reclama una moneda cuando el usuario está en bancarrota.
     * Solo se entrega si el saldo es 0 y ya transcurrió el
     * tiempo de espera configurado.
     */
    @POST
    @Path("/usuario/{usuarioId}/reclamar-moneda")
    public Response reclamarMoneda(
            @PathParam("usuarioId") Long usuarioId) {

        try {

            billeteraService.procesarBonoBancarrota(usuarioId);

            return Response.ok(
                    new MensajeDTO("Moneda asignada correctamente.")
            ).build();

        } catch (IllegalStateException e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();

        } catch (IllegalArgumentException e) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();
        }
    }
}