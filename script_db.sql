-- 1. Tabla de Billeteras
CREATE TABLE billeteras (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    saldo DECIMAL(10, 2) NOT NULL DEFAULT 0.00
);

-- 2. Tabla de Transacciones (Ledger inmutable)
CREATE TABLE transacciones (
    id BIGSERIAL PRIMARY KEY,
    billetera_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_billetera FOREIGN KEY (billetera_id) REFERENCES billeteras(id) ON DELETE CASCADE
);

-- 3. Tabla de Predicciones
CREATE TABLE predicciones (
    id BIGSERIAL PRIMARY KEY,
    billetera_id BIGINT NOT NULL,
    partido_id BIGINT NOT NULL,
    pronostico VARCHAR(20) NOT NULL,
    monto_apostado DECIMAL(10, 2) NOT NULL,
    cuota_aplicada DECIMAL(5, 2) NOT NULL DEFAULT 1.00,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prediccion_billetera FOREIGN KEY (billetera_id) REFERENCES billeteras(id) ON DELETE CASCADE
);

CREATE TABLE configuracion (
    clave VARCHAR(50) PRIMARY KEY,
    valor VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

INSERT INTO configuracion (clave, valor, descripcion) 
VALUES ('TIEMPO_ESPERA_BONO_MINUTOS', '2', 'Tiempo de espera en minutos para reclamar el bono de bancarrota');

