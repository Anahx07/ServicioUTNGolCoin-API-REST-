package ec.edu.utn.golmundial.controller;

import ec.edu.utn.golmundial.dto.BilleteraDTO;
import ec.edu.utn.golmundial.dto.CrearBilleteraRequest;
import ec.edu.utn.golmundial.dto.MensajeDTO;
import ec.edu.utn.golmundial.dto.TransaccionDTO;
import ec.edu.utn.golmundial.model.Billetera;
import ec.edu.utn.golmundial.model.Transaccion;
import ec.edu.utn.golmundial.service.BilleteraService;
import ec.edu.utn.golmundial.service.BonoDiarioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/billeteras")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BilleteraController {

    @Inject
    private BilleteraService billeteraService;

    @Inject
    private BonoDiarioService bonoDiarioService;

    /**
     * Crea la billetera de un usuario nuevo con el bono de bienvenida (RF01).
     * Lo invoca el Servicio de Estadísticas al registrar un usuario.
     */
    @POST
    public Response crearBilletera(CrearBilleteraRequest request) {
        try {
            Billetera billetera = billeteraService.crearBilletera(request.getUsuarioId());
            return Response.status(Response.Status.CREATED)
                    .entity(new BilleteraDTO(billetera))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();
        }
    }

    /**
     * Consulta el saldo de un usuario (RF13).
     */
    @GET
    @Path("/usuario/{usuarioId}")
    public Response consultarSaldo(@PathParam("usuarioId") Long usuarioId) {
        try {
            Billetera billetera = billeteraService.consultarBilletera(usuarioId);
            return Response.ok(new BilleteraDTO(billetera)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();
        }
    }

    /**
     * Se llama al iniciar sesión. Otorga 1 UTNGolCoin si el saldo es cero
     * y no se ha reclamado hoy (RF20).
     */
    @POST
    @Path("/usuario/{usuarioId}/bono-diario")
    public Response reclamarBonoDiario(@PathParam("usuarioId") Long usuarioId) {
        try {
            Transaccion tx = bonoDiarioService.otorgarBonoDiarioSiCorresponde(usuarioId);
            if (tx == null) {
                return Response.status(Response.Status.OK)
                        .entity(new MensajeDTO("No corresponde bono diario en este momento."))
                        .build();
            }
            return Response.ok(new TransaccionDTO(tx)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeDTO(e.getMessage()))
                    .build();
        }
    }
}
