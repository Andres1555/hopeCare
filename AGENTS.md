# HopeCare - Guía para Agentes IA

## Stack Tecnológico
- Java 17+, JavaFX 21, Maven, SQLite
- Dependencias principales: sqlite-jdbc, javafx-controls, javafx-fxml

## Arquitectura
- **Presentación**: JavaFX + FXML, organización por Feature (cada módulo tiene su view/)
- **Negocio**: Services, Presenters (MVP en citas), Facade (medicamentos_lab), EventBus (dashboard)
- **Persistencia**: DAOs con SQLite, conexiones vía `DatabaseConnection.getConnection()`
- **Navegación**: Header superior con Hyperlinks + TabPane oculto (selección por índice)

## Estilo Visual
- Paleta de colores: Teal (`#0d9488`, `#115e59`, `#0f766e`) + Slate (`#f8fafc`, `#64748b`, `#0f172a`)
- Cards con bordes redondeados (12px), sombra sutil, fondo blanco
- Tablas con headers en `#f1f5f9`, filas alternadas
- CSS centralizado: `src/main/resources/com/esperanza/hopecare/main/hopecare.css`

## Convenciones de Código
- No añadir comentarios a menos que se solicite
- Seguir patrones existentes (cada DAO acepta `Connection` para operaciones transaccionales)
- Transacciones explícitas: `conn.setAutoCommit(false)`, commit/rollback en try-catch-finally
- Manejo de excepciones: catch SQLException, e.printStackTrace(), devolver false en DAOs
- Modelos con constructor vacío + constructor con campos + getters/setters

## Paquetes Clave
| Ruta | Propósito |
|------|-----------|
| `modules/medicamentos_lab/` | Models, DAOs, Services, Facade para farmacia y laboratorio |
| `modules/citas_consultas/` | Models, DAOs, Presenters para citas y consultas |
| `modules/facturacion/` | Service, DTOs, DAOs para facturación (parcial) |
| `modules/dashboard/` | Observer pattern, DashboardDAO |
| `common/db/` | DatabaseConnection, CrearBaseDatos, CargarDatosPrueba |
| `common/events/` | EventBus, NuevaCitaEvent, NuevaFacturaEvent |
| `common/utils/` | RoleValidator, CalculadoraImpuestos |
| `*/view/` | Java Controllers + FXML files |

## Módulos Completos (Farmacia + Laboratorio)

### Farmacia (testeable 100%)
- **FXML**: `farmacia.fxml` - 3 cards (Inventario, Recetas Activas, Entregas Recientes) + form de entrega
- **Controller**: `FarmaciaController.java` - TableViews con CellFactory, diálogos para agregar/actualizar
- **Service**: `InventarioService.java` - agregarMedicamento(), actualizarStock()
- **DAOs**: `EntregaMedicamentoDAO.java` - listarTodas(), listarPorReceta()

### Laboratorio (testeable 100%)
- **FXML**: `laboratorio.fxml` - 3 cards (Catálogo Exámenes, Solicitudes, Resultados) + form de registro
- **Controller**: `LaboratorioController.java` - TableViews, filtros por estado, diálogos para agregar examen
- **Service**: `ExamenService.java` - agregarExamen(), listarSolicitudesPorEstado(), listarTodasSolicitudes()
- **DAOs**: `SolicitudExamenDAO.java` - listarTodas(), listarPorEstado()

## Base de Datos
- Archivo: `sisgeho.db` en raíz del proyecto
- Script DDL: `src/main/resources/sisgeho_schema.sql`
- Datos de prueba: `CargarDatosPrueba.java` (ejecutar con `mvn exec:java -Dexec.mainClass="..."`)
- Driver: SQLite v3.45.1.0 (xerial sqlite-jdbc)
- **No modificar el schema** (es minimalista, campos sujeto a cambios por otro equipo)

## Comandos Útiles
```bash
# Configurar entorno (ejecutar una vez por terminal)
$env:JAVA_HOME = "C:\Program Files\Zulu\zulu-21"
$env:Path = "$env:USERPROFILE\AppData\Local\apache-maven-3.9.15\bin;$env:Path"

# Ejecutar app
mvn clean javafx:run

# Compilar
mvn clean compile
```

**Nota**: Maven está instalado localmente en `%USERPROFILE%\AppData\Local\apache-maven-3.9.15`. Si usas NetBeans/IntelliJ/VS Code con soporte Maven integrado, no necesitas configurar PATH manualmente.

## Navegación Principal
La navegación se maneja desde `MainController.java`:
- `main.fxml` usa un `TabPane` oculto con los 6 módulos
- El header superior con `Hyperlink` navega cambiando el índice del TabPane
- Breadcrumb se actualiza dinámicamente

## Sistema de Módulos (Java Platform Module System)
- `module-info.java` centraliza las configuraciones de módulos Java
- Los paquetes `opens` necesarios para JavaFX (`javafx.fxml` para controllers, `javafx.base` para modelos con PropertyValueFactory)
- Paquetes model abiertos a `javafx.base`: `medicamentos_lab.model`, `citas_consultas.model`, `pacientes_medicos.model`, `dashboard.model`
- Al agregar nuevos modelos usados en TableView con PropertyValueFactory, agregar `opens` correspondiente en `module-info.java`

## Pendientes Conocidos
- Login con roles (responsabilidad de otro equipo)
- Módulos de otros equipos (Dashboard, Citas, Consulta, Registro, Facturación): tienen estructura visual pero lógica pendiente
- Reportes (JasperReports)
- Pruebas unitarias JUnit 5
- JAR ejecutable con Maven Assembly Plugin
- DAOs de facturación (facturacion/dao/) son stubs sin implementación real

## Estructura de Archivos Modificados (rediseño UI)
- `main.fxml` / `MainController.java` - Nueva navegación header
- `hopecare.css` - Paleta teal/slate completa
- `farmacia.fxml` / `FarmaciaController.java` - Completo
- `laboratorio.fxml` / `LaboratorioController.java` - Completo
- `dashboard.fxml` - Estilo visual actualizado (lógica existente)