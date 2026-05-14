# HopeCare - Sistema de Gestión Hospitalaria

HopeCare es una aplicación de escritorio desarrollada en JavaFX para la gestión de clínicas y hospitales.

## 🚀 Arquitectura del Proyecto

El proyecto sigue un enfoque híbrido donde la capa de presentación está organizada por características (Features) y los módulos de negocio utilizan diferentes patrones arquitectónicos según su complejidad.

### 💻 Capa de Presentación (JavaFX)

Está organizada por **Feature-Based** (Por Característica), donde cada pantalla o módulo visual tiene su propio paquete que contiene:
- El Controlador Java.
- El archivo FXML.

#### Módulos de Vista:
- **`main`**: Contenedor principal (`TabPane`), CSS global y clase de inicio (`HopecareApp`).
- **`registro`**, **`citas`**, **`consulta`**, **`farmacia`**, **`laboratorio`**, **`facturacion`**, **`dashboard`**: Pantallas específicas de la aplicación.

---

### 🧠 Capa de Negocio (Módulos)

Ubicada en `src/main/java/com/esperanza/hopecare/modules`. Cada módulo implementa una arquitectura distinta para demostrar versatilidad o adaptarse mejor a sus necesidades:

#### 1. Citas y Consultas (`citas_consultas`)
*   **Patrón**: **MVP (Model-View-Presenter)**.
*   **Descripción**: Separa la lógica compleja de cálculo de horarios y reservas de la interfaz de usuario. El `CitaPresenter` maneja las reglas de negocio, mientras que la vista implementa una interfaz (`ICitaView`).

#### 2. Dashboard (`dashboard`)
*   **Patrón**: **Event-Driven / Observer**.
*   **Descripción**: Diseñado para reaccionar a eventos globales. Se suscribe a un `EventBus` para recibir notificaciones (ej. "Nueva Cita", "Nueva Factura") y actualizar las métricas en tiempo real sin necesidad de refrescar manualmente.

#### 3. Facturación (`facturacion`)
*   **Patrón**: **Arquitectura en Capas Tradicional**.
*   **Estructura**: Service -> DAO -> DTO.
*   **Descripción**: Enfoque clásico para procesos de negocio puros. El servicio calcula montos y genera DTOs (`FacturaDTO`) que luego son presentados en la UI.

#### 4. Medicamentos y Laboratorio (`medicamentos_lab`)
*   **Patrón**: **Facade (Fachada)**.
*   **Descripción**: Utiliza `GestionClinicaFacade` para simplificar la interacción con los subsistemas de farmacia y laboratorio. Centraliza operaciones como la entrega de medicamentos y el registro de resultados.

#### 5. Pacientes y Médicos (`pacientes_medicos`)
*   **Patrón**: **Capa de Datos Simple (DAO/Model)**.
*   **Descripción**: El módulo más simple, enfocado puramente en operaciones CRUD para la gestión de entidades básicas.

---

## 🛠️ Tecnologías Utilizadas

*   **Java 11+**
*   **JavaFX 21** (Interfaces de usuario)
*   **Maven** (Gestión de dependencias y construcción)
*   **SQLite** (Base de datos local)


## 🗄️ Base de Datos

El sistema utiliza SQLite. El archivo de base de datos se genera automáticamente como `hopecare.db` (según la configuración). Para inicializar las tablas, debes ejecutar el script `schema.sql` sobre la base de datos generada.
