package ec.edu.utn.golmundial.controller;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * Habilita CORS para que el Frontend (MVC .NET u otro, corriendo en el
 * navegador de otra máquina de la red) pueda consumir esta API sin que el
 * navegador bloquee la petición (pedido del ingeniero en la pizarra).
 *
 * Se implementa como filtro JAX-RS en vez de configurar Undertow en
 * standalone.xml: funciona igual sin importar el servidor/versión, y
 * maneja correctamente el preflight OPTIONS (algo que la configuración
 * a nivel de servidor con response-header filters NO cubre por sí sola).
 *
 * Nota: se usa "*" como origen permitido por simplicidad, adecuado para
 * un proyecto académico. En un entorno real se restringiría a la URL
 * exacta del frontend.
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "Content-Type, Authorization");
        responseContext.getHeaders().putSingle("Access-Control-Max-Age", "3600");

        // Responder directamente al preflight OPTIONS del navegador,
        // sin dejar que llegue a un recurso que no tiene método OPTIONS.
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            responseContext.setStatus(Response.Status.OK.getStatusCode());
        }
    }
}