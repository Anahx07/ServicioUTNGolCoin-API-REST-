package ec.edu.utn.golmundial;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activa JAX-RS para toda la aplicación. Todos los endpoints quedan
 * disponibles bajo /utngolcoin-backend/api/... (el primer segmento es
 * el contexto del WAR, definido por <finalName> en el pom.xml).
 */
@ApplicationPath("/api")
public class ApiApplication extends Application {
}
