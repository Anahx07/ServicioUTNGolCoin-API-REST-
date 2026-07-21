package ec.edu.utn.golmundial.controller;

import ec.edu.utn.golmundial.dto.MensajeDTO;
import ec.edu.utn.golmundial.model.Configuracion;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/configuraciones")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ConfiguracionController {

    @PersistenceContext(unitName = "UtnGolCoinPU")
    private EntityManager em;

    /**
     * Lista todas las configuraciones del sistema.
     *
     * GET /configuraciones
     */
    @GET
    public Response listarConfiguraciones() {

        List<Configuracion> configuraciones = em.createQuery(
                "SELECT c FROM Configuracion c ORDER BY c.clave",
                Configuracion.class)
                .getResultList();

        return Response.ok(configuraciones).build();
    }

    /**
     * Obtiene una configuración por su clave.
     *
     * GET /configuraciones/{clave}
     */
    @GET
    @Path("/{clave}")
    public Response obtenerConfiguracion(
            @PathParam("clave") String clave) {

        Configuracion configuracion = em.find(Configuracion.class, clave);

        if (configuracion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeDTO("Configuración no encontrada."))
                    .build();
        }

        return Response.ok(configuracion).build();
    }

    /**
     * Actualiza el valor de una configuración.
     *
     * PUT /configuraciones/{clave}
     */
    @PUT
    @Path("/{clave}")
    public Response actualizarConfiguracion(
            @PathParam("clave") String clave,
            Configuracion configInput) {

        if (configInput == null || configInput.getValor() == null) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MensajeDTO(
                            "El valor de la configuración es obligatorio."))
                    .build();
        }

        Configuracion configuracion = em.find(Configuracion.class, clave);

        if (configuracion == null) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MensajeDTO("Configuración no encontrada."))
                    .build();
        }

        configuracion.setValor(configInput.getValor());

        em.merge(configuracion);

        return Response.ok(configuracion).build();
    }
}