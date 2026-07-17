package ec.edu.utn.golmundial.controller;

import ec.edu.utn.golmundial.model.Configuracion;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/configuraciones")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ConfiguracionController {

    @PersistenceContext(unitName = "UtnGolCoinPU")
    private EntityManager em;

    /**
     * Endpoint para que el Admin actualice el tiempo de espera del bono.
     * PUT http://localhost:8080/utngolcoin-backend/api/configuraciones/TIEMPO_ESPERA_BONO_MINUTOS
     */
    @PUT
    @Path("/{clave}")
    public Response actualizarConfiguracion(@PathParam("clave") String clave, Configuracion configInput) {
        Configuracion existente = em.find(Configuracion.class, clave);
        
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Configuración no encontrada\"}")
                    .build();
        }

        // Actualizamos únicamente el valor numérico (los minutos)
        existente.setValor(configInput.getValor());
        em.merge(existente);

        return Response.ok(existente).build();
    }
    
    /**
     * Endpoint para consultar el valor actual de la configuración.
     * GET http://localhost:8080/utngolcoin-backend/api/configuraciones/TIEMPO_ESPERA_BONO_MINUTOS
     */
    @GET
    @Path("/{clave}")
    public Response obtenerConfiguracion(@PathParam("clave") String clave) {
        Configuracion config = em.find(Configuracion.class, clave);
        if (config == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(config).build();
    }
}