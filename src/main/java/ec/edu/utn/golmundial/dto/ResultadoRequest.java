package ec.edu.utn.golmundial.dto;

import ec.edu.utn.golmundial.model.enums.Pronostico;

/**
 * Cuerpo esperado en la notificación que envía el Servicio de Estadísticas
 * cuando se registra el resultado oficial de un partido (RF12).
 * Dispara la liquidación automática de las predicciones (RF19).
 */
public class ResultadoRequest {

    private Long partidoId;
    private Pronostico resultadoOficial;

    public ResultadoRequest() {}

    public Long getPartidoId() { return partidoId; }
    public void setPartidoId(Long partidoId) { this.partidoId = partidoId; }

    public Pronostico getResultadoOficial() { return resultadoOficial; }
    public void setResultadoOficial(Pronostico resultadoOficial) { this.resultadoOficial = resultadoOficial; }
}
