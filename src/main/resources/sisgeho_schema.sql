-- 1. TABLAS BASE Y TRANSVERSALES
CREATE TABLE IF NOT EXISTS persona (
    id_persona INTEGER PRIMARY KEY AUTOINCREMENT,
    documento_identidad TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS especialidad (
    id_especialidad INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    id_rol INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS rol (
    id_rol INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
);

-- 2. MÓDULO DE REGISTRO
CREATE TABLE IF NOT EXISTS medico (
    id_medico INTEGER PRIMARY KEY AUTOINCREMENT,
    id_persona INTEGER NOT NULL,
    id_especialidad INTEGER NOT NULL,
    registro_medico TEXT UNIQUE NOT NULL,
    FOREIGN KEY (id_persona) REFERENCES persona(id_persona),
    FOREIGN KEY (id_especialidad) REFERENCES especialidad(id_especialidad)
);

CREATE TABLE IF NOT EXISTS paciente (
    id_paciente INTEGER PRIMARY KEY AUTOINCREMENT,
    id_persona INTEGER NOT NULL,
    historia_clinica TEXT UNIQUE NOT NULL,
    FOREIGN KEY (id_persona) REFERENCES persona(id_persona)
);

-- 3. MÓDULO DE CITAS Y CONSULTAS
CREATE TABLE IF NOT EXISTS horario_atencion (
    id_horario INTEGER PRIMARY KEY AUTOINCREMENT,
    id_medico INTEGER NOT NULL,
    dia_semana INTEGER NOT NULL,
    hora_inicio TEXT NOT NULL,
    hora_fin TEXT NOT NULL,
    intervalo_minutos INTEGER NOT NULL,
    FOREIGN KEY (id_medico) REFERENCES medico(id_medico)
);

CREATE TABLE IF NOT EXISTS cita (
    id_cita INTEGER PRIMARY KEY AUTOINCREMENT,
    id_paciente INTEGER NOT NULL,
    id_medico INTEGER NOT NULL,
    fecha_hora TEXT NOT NULL,
    estado TEXT DEFAULT 'PROGRAMADA',
    FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente),
    FOREIGN KEY (id_medico) REFERENCES medico(id_medico)
);

CREATE TABLE IF NOT EXISTS consulta (
    id_consulta INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cita INTEGER NOT NULL,
    diagnostico TEXT,
    sintomas TEXT,
    tratamiento TEXT,
    facturado INTEGER DEFAULT 0,
    FOREIGN KEY (id_cita) REFERENCES cita(id_cita)
);

-- 4. MÓDULO DE MEDICAMENTOS Y LABORATORIO
CREATE TABLE IF NOT EXISTS medicamento (
    id_medicamento INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    stock_actual INTEGER DEFAULT 0,
    stock_minimo INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS receta (
    id_receta INTEGER PRIMARY KEY AUTOINCREMENT,
    id_consulta INTEGER NOT NULL,
    fecha_emision TEXT DEFAULT CURRENT_TIMESTAMP,
    instrucciones TEXT,
    activa INTEGER DEFAULT 1,
    FOREIGN KEY (id_consulta) REFERENCES consulta(id_consulta)
);

CREATE TABLE IF NOT EXISTS detalle_receta (
    id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
    id_receta INTEGER NOT NULL,
    id_medicamento INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    dosis_indicacion TEXT,
    FOREIGN KEY (id_receta) REFERENCES receta(id_receta),
    FOREIGN KEY (id_medicamento) REFERENCES medicamento(id_medicamento)
);

CREATE TABLE IF NOT EXISTS entrega_medicamento (
    id_entrega INTEGER PRIMARY KEY AUTOINCREMENT,
    id_receta INTEGER NOT NULL,
    id_medicamento INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    fecha_entrega TEXT DEFAULT CURRENT_TIMESTAMP,
    facturado INTEGER DEFAULT 0,
    FOREIGN KEY (id_receta) REFERENCES receta(id_receta),
    FOREIGN KEY (id_medicamento) REFERENCES medicamento(id_medicamento)
);

CREATE TABLE IF NOT EXISTS examen_laboratorio (
    id_examen INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    precio REAL NOT NULL,
    tiempo_resultado_horas INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS solicitud_examen (
    id_solicitud INTEGER PRIMARY KEY AUTOINCREMENT,
    id_consulta INTEGER NOT NULL,
    id_examen INTEGER NOT NULL,
    fecha_solicitud TEXT DEFAULT CURRENT_TIMESTAMP,
    estado TEXT DEFAULT 'PENDIENTE',
    resultado_texto TEXT,
    realizado_por INTEGER,
    facturado INTEGER DEFAULT 0,
    FOREIGN KEY (id_consulta) REFERENCES consulta(id_consulta),
    FOREIGN KEY (id_examen) REFERENCES examen_laboratorio(id_examen)
);

-- 5. MÓDULO DE FACTURACIÓN
CREATE TABLE IF NOT EXISTS factura (
    id_factura INTEGER PRIMARY KEY AUTOINCREMENT,
    id_paciente INTEGER NOT NULL,
    fecha_emision TEXT DEFAULT CURRENT_TIMESTAMP,
    subtotal REAL NOT NULL,
    impuesto REAL NOT NULL,
    total REAL NOT NULL,
    estado_pago TEXT DEFAULT 'PENDIENTE',
    FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente)
);

CREATE TABLE IF NOT EXISTS detalle_factura (
    id_detalle_factura INTEGER PRIMARY KEY AUTOINCREMENT,
    id_factura INTEGER NOT NULL,
    concepto TEXT NOT NULL,
    id_referencia INTEGER NOT NULL,
    tipo_referencia TEXT NOT NULL,
    monto REAL NOT NULL,
    FOREIGN KEY (id_factura) REFERENCES factura(id_factura)
);
