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
| `modules/citas_consultas/` | Models, DAOs, Presenters (MVP) para citas y consultas |
| `modules/pacientes_medicos/` | Models (Persona, Medico, Paciente, Especialidad) y DAOs para pacientes y médicos (MedicoDAO, PacienteDAO, EspecialidadDAO) |
| `modules/medicamentos_lab/` | Models, DAOs, Services, Facade para farmacia y laboratorio |
| `modules/facturacion/` | Service, DTOs, DAOs para facturación |
| `modules/dashboard/` | Observer pattern, DashboardDAO |
| `common/model/` | Persona (base class con nombre, apellido, documentoIdentidad) |
| `common/db/` | DatabaseConnection, CrearBaseDatos, CargarDatosPrueba |
| `common/events/` | EventBus, NuevaCitaEvent, NuevaFacturaEvent |
| `common/util/` | RoleValidator |
| `citas/view/` | CitasController.java (JavaFX, implementa ICitaView) + citas.fxml |
| `consulta/view/` | ConsultaController.java (standalone) + consulta.fxml |
| `ui/citas/` | CitasPanel.java (Swing, implementa ICitaView) |
| `*/view/` | Java Controllers + FXML files |

## Módulos Completos (Citas/Consultas + Farmacia + Laboratorio)

### Citas (MVP - ICitaView + CitaPresenter)
- **ICP**: `ICitaView.java` - interfaz con mostrarHorariosDisponibles(), getters de selección
- **Presenter**: `CitaPresenter.java` - genera bloques desde HorarioAtencion (intervalo_minutos), filtra ocupados vía CitaDAO, reserva y publica NuevaCitaEvent
- **JavaFX**: `CitasController.java` - dos TableViews (Pacientes y Médicos) con selección por fila + DatePicker + ComboBox. Implementa ICitaView. Carga datos desde `PacienteDAO.listarTodos()` y `MedicoDAO.listarTodos()`
- **Swing**: `CitasPanel.java` - implementación alternativa de ICitaView (usa TextFields)
- **Consola**: `CitaConsoleView.java` - implementación de prueba
- **DAOs**: `CitaDAO.java` (CRUD + filtrar por médico/fecha/estado + `obtenerCitasPorEstadoConNombres()`), `HorarioAtencionDAO.java` (obtener por médico+día), `PacienteDAO.listarTodos()`, `MedicoDAO.listarTodos()` (JOIN con persona y especialidad)

### Consultas (MVP - IConsultaView + ConsultaPresenter)
- **ICP**: `IConsultaView.java` - interfaz con métodos para cargar citas, formulario, solicitar examen/receta. Incluye inner class `RecetaRequest`
- **Presenter**: `ConsultaPresenter.java` - carga citas PROGRAMADA, registra consulta (transacción INSERT consulta + UPDATE cita), solicita examen, receta medicamento (transacción INSERT receta + detalle_receta)
- **JavaFX**: `ConsultaController.java` - standalone con ComboBox de citas pendientes (muestra nombres de paciente/médico gracias a `CitaDAO.obtenerCitasPorEstadoConNombres()`), TextArea para síntomas/diagnóstico/tratamiento, diálogos modales para seleccionar examen y recetar medicamento
- **DAO**: `ConsultaDAO.java` - insertarConsultaYActualizarEstado() con commit/rollback explícito

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

## Inicialización Automática de BD
La app crea y puebla la base de datos automáticamente al iniciar (`HopecareApp.init()`):
1. Verifica si la tabla `persona` existe
2. Si no existe → ejecuta `sisgeho_schema.sql` para crear todas las tablas
3. Si `persona` está vacía → inserta datos de prueba (5 pacientes, 3 médicos, 10 citas, etc.)
4. Si ya hay datos → no hace nada

Para resetear la BD: borrar `sisgeho.db` de la raíz y reiniciar la app.

## Comandos Útiles
```bash
# Configurar entorno (ejecutar una vez por terminal)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
$mvn = "$env:USERPROFILE\.m2\wrapper\dists\apache-maven-3.9.12-bin\5nmfsn99br87k5d4ajlekdq10k\apache-maven-3.9.12\bin\mvn.cmd"

# Ejecutar app (inicializa BD automáticamente)
& $mvn clean javafx:run

# Compilar
& $mvn clean compile
```

**Nota**: Maven wrapper en `%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.12-bin`. Si usas NetBeans/IntelliJ/VS Code con Maven integrado, no necesitas configurar PATH.

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
- Login con roles (actualmente se pasa rol fijo)
- Módulos de otros equipos (Dashboard, Registro, Facturación): tienen estructura visual pero lógica o DAOs pendientes
- Reportes (JasperReports)
- Pruebas unitarias JUnit 5
- JAR ejecutable con Maven Assembly Plugin

## Estructura de Archivos Modificados
- `main.fxml` / `MainController.java` - Nueva navegación header
- `hopecare.css` - Paleta teal/slate completa
- `farmacia.fxml` / `FarmaciaController.java` - Completo
- `laboratorio.fxml` / `LaboratorioController.java` - Completo
- `dashboard.fxml` - Estilo visual actualizado (lógica existente)

## Últimos Cambios (Rediseño flujo citas: tabla principal + diálogo modal + filtros)
- `Especialidad.java` (nuevo) - Modelo para la tabla especialidad (idEspecialidad, nombre)
- `EspecialidadDAO.java` (nuevo) - DAO con `listarTodas()` para cargar especialidades en ComboBox
- `HorarioAtencionDAO.java` - Nuevo método `obtenerHorariosPorMedico(int)` que retorna todos los horarios de un médico (todos los días)
- `CitaDAO.java` - Nuevo método `listarTodasConNombres()` con JOIN a persona (todas las citas ORDER BY fecha DESC)
- `ICitaView.java` - Nuevos métodos `mostrarCitasExistentes(List<Cita>)`, `mostrarDiasDisponibles(List<Integer>)` y `getDiaSeleccionado()`
- `CitaPresenter.java` - Nuevos métodos `cargarCitasExistentes()` y `cargarDiasDisponibles(int idMedico)`
- `citas.fxml` - Rediseñado: tabla de citas existentes (ID, Paciente, Médico, Fecha/Hora, Estado) + botón "Agendar nueva cita". El formulario de creación se eliminó del FXML y ahora es un diálogo modal generado programáticamente
- `CitasController.java` - Rewrite completo: carga y muestra citas existentes en tabla principal. "Nueva Cita" abre un `Dialog<>` modal con MVP inline (crea `ICitaView` temporal para el diálogo). El diálogo contiene: buscador de pacientes, filtro especialidad + buscador médicos, selector de días disponibles, auto-asignación de fecha, horarios y botón reservar. Al cerrar el diálogo, refresca la tabla de citas.
- `CitaConsoleView.java` - Implementados stubs de `mostrarCitasExistentes`, `mostrarDiasDisponibles` y `getDiaSeleccionado`
- `CitasPanel.java` (Swing) - Implementados stubs de `mostrarCitasExistentes`, `mostrarDiasDisponibles` y `getDiaSeleccionado`
- `ConsultaPresenter.java` - Cambiado a `obtenerCitasPorEstadoConNombres()` para mostrar nombres paciente/médico en ComboBox
- `ConsultaController.java` - Ahora implementa `IConsultaView` y delega toda la lógica de negocio a `ConsultaPresenter` (MVP completo)