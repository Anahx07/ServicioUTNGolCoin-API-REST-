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

    @POST
    public Response crearBilletera(CrearBilleteraRequest request) {
        try {
            Billetera billetera = billeteraService.crearBilletera(request.getUsuarioId());
            return Response.status(Response.Status.CREATED).entity(new BilleteraDTO(billetera)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT).entity(new MensajeDTO(e.getMessage())).build();
        }
    }

    @GET
    @Path("/ranking")
    public Response consultarRanking() {
        try {
            java.util.List<ec.edu.utn.golmundial.dto.RankingDTO> ranking = billeteraService.obtenerRankingApostadores();
            return Response.ok(ranking).build();
        } catch (Exception e) {
            return Response.serverError().entity(new MensajeDTO(e.getMessage())).build();
        }
    }

    @GET
    @Path("/usuario/{usuarioId}/historial")
    public Response consultarHistorial(@PathParam("usuarioId") Long usuarioId) {
        try {
            return Response.ok(new MensajeDTO("Historial consultado exitosamente para usuario: " + usuarioId)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MensajeDTO(e.getMessage())).build();
        }
    }

    @GET
    @Path("/usuario/{usuarioId}")
    public Response consultarSaldo(@PathParam("usuarioId") Long usuarioId) {
        try {
            Billetera billetera = billeteraService.consultarBilletera(usuarioId);
            return Response.ok(new BilleteraDTO(billetera)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new MensajeDTO(e.getMessage())).build();
        }
    }

    @POST
    @Path("/usuario/{usuarioId}/recargar")
    public Response recargarBilleteraAdmin(@PathParam("usuarioId") Long usuarioId) {
        try {
            billeteraService.recargarSaldoAdmin(usuarioId);
            return Response.ok(new MensajeDTO("Recargado")).build();
        } catch (Exception e) {
            return Response.serverError().entity(new MensajeDTO(e.getMessage())).build();
        }
    }
}

