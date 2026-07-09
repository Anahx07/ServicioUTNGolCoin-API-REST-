# Configurar el DataSource de PostgreSQL en WildFly

El `persistence.xml` ahora usa `<jta-data-source>java:/UtnGolCoinDS</jta-data-source>`.
Ese DataSource hay que crearlo en WildFly (no en el proyecto Java). Pasos:

## 1. Crear la base de datos en PostgreSQL

```bash
sudo -u postgres psql
CREATE DATABASE utngolcoin_db OWNER eacoltac;
\q
```

Luego carga el esquema:

```bash
psql -U eacoltac -d utngolcoin_db -f script_db.sql
```

## 2. Instalar el módulo del driver JDBC de PostgreSQL en WildFly

Descarga el jar del driver (mismo que usa el pom, 42.7.3) y colócalo como módulo:

```bash
mkdir -p $WILDFLY_HOME/modules/org/postgresql/main
cp postgresql-42.7.3.jar $WILDFLY_HOME/modules/org/postgresql/main/
```

Crea `$WILDFLY_HOME/modules/org/postgresql/main/module.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.postgresql">
    <resources>
        <resource-root path="postgresql-42.7.3.jar"/>
    </resources>
    <dependencies>
        <module name="jakarta.transaction.api"/>
        <module name="javax.api"/>
    </dependencies>
</module>
```

## 3. Registrar el driver y el DataSource (con WildFly corriendo)

```bash
$WILDFLY_HOME/bin/jboss-cli.sh --connect
```

Dentro de la consola CLI:

```
/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver)

data-source add --name=UtnGolCoinDS --jndi-name=java:/UtnGolCoinDS --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5432/utngolcoin_db --user-name=eacoltac --password=1234 --enabled=true

/subsystem=datasources/data-source=UtnGolCoinDS:test-connection-in-pool
```

Si el `test-connection-in-pool` responde `outcome => "success"`, el DataSource está listo.

## 4. Compilar y desplegar

```bash
cd utngolcoin-backend
mvn clean package
cp target/utngolcoin-backend.war $WILDFLY_HOME/standalone/deployments/
```

Revisa `$WILDFLY_HOME/standalone/log/server.log` — debe aparecer algo como
`WFLYUT0021: Registered web context: '/utngolcoin-backend'` sin errores.

## 5. Probar un endpoint

```bash
curl -X POST http://localhost:8080/utngolcoin-backend/api/billeteras \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": 1}'

curl http://localhost:8080/utngolcoin-backend/api/billeteras/usuario/1
```

Deberías ver el JSON de la billetera con saldo 10.00.
