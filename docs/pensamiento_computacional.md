# Pensamiento Computacional — TicketHub

## 1. Abstracción

### ¿Qué se solicita finalmente? (problema)
Desarrollar una plataforma de gestión de eventos llamada **TicketHub** que permita a usuarios explorar eventos, comprar entradas con asientos numerados y servicios adicionales, y a administradores gestionar el catálogo completo (eventos, recintos, zonas, asientos, compras, usuarios) con un panel de métricas y generación de reportes. La solución debe aplicar patrones de diseño (Singleton, Builder, Factory, Decorator, Adapter, Proxy, Strategy, State, Observer) y principios SOLID.

### ¿Qué información es relevante dado el problema anterior?
- Identidad del usuario (nombre, email, rol) para autenticación y permisos
- Datos del evento (nombre, fecha, categoría, ciudad, estado) para catálogo y filtros
- Configuración del recinto (nombre, dirección, zonas con asientos) para el mapa de disponibilidad
- Precios base por zona y estrategias de tarifa (estándar / preventa) para el cálculo dinámico
- Estado de cada asiento (disponible, reservado, vendido, bloqueado) para evitar sobreventas
- Transacciones de compra con historial de estados (creada, pagada, confirmada, cancelada, reembolsada, incidencia)
- Servicios adicionales seleccionados (VIP, seguro, merchandising, parqueadero, acceso preferencial)
- Incidencias registradas ante cancelaciones o anomalías

### ¿Cómo se agrupa la información relevante?
La información se agrupa en entidades del dominio modeladas como clases Java:
- **Usuario** agrupa datos personales, métodos de pago e historial de compras
- **Evento** agrupa datos del evento, políticas y referencia al recinto
- **Recinto → Zona → Asiento** forman una jerarquía de composición (el recinto contiene zonas que contienen asientos)
- **Compra** agrupa el usuario, evento, lista de entradas y estado actual (State)
- **Entrada** agrupa la zona, el asiento y el precio final calculado con servicios adicionales (Decorator)
- **Incidencia** agrupa el tipo, descripción, fecha y entidad afectada

### ¿Qué funcionalidades se solicitan finalmente?
| Funcionalidad | Usuario | Admin |
|:---|---:|:---:|
| Registrarse e iniciar sesión | ✅ | ✅ |
| Gestionar perfil y métodos de pago | ✅ | ❌ |
| Explorar eventos con filtros | ✅ | ❌ |
| Ver detalle del evento y mapa de asientos | ✅ | ❌ |
| Comprar entradas con servicios adicionales | ✅ | ❌ |
| Ver historial y descargar reportes CSV/PDF | ✅ | ✅ |
| CRUD de eventos, recintos, zonas, asientos | ❌ | ✅ |
| CRUD de usuarios | ❌ | ✅ |
| Reasignar asientos en compras | ❌ | ✅ |
| Panel de métricas con 6 gráficas JavaFX | ❌ | ✅ |
| Generar reportes ejecutivos con análisis | ❌ | ✅ |
| Gestionar incidencias con filtros | ❌ | ✅ |

---

## 2. Descomposición

### ¿Cómo se distribuyen las funcionalidades?
El sistema se descompone en **capas** siguiendo una arquitectura multicapa:

```
┌──────────────────────────────────────────────────┐
│           Capa de Presentación (JavaFX)           │
│  LoginController  │  AdminDashboardController     │
│  UserDashboard    │  EventDetailController        │
│  MisCompras       │  PerfilController             │
├──────────────────────────────────────────────────┤
│         Capa de Fachada (Singleton + Facade)      │
│              GestionEventos                       │
├──────────────────────────────────────────────────┤
│              Capa de Servicios (SRP)              │
│  UserService │ EventService │ VenueService        │
│  PurchaseService │ IncidentService │ ReportService │
├──────────────────────────────────────────────────┤
│           Capa de Modelo (Dominio)                │
│  Usuario │ Evento │ Recinto │ Zona │ Asiento      │
│  Compra │ Entrada │ Incidencia │ Filtros          │
├──────────────────────────────────────────────────┤
│           Capa de Patrones de Diseño              │
│  Strategy │ State │ Decorator │ Adapter           │
│  Proxy │ Factory │ Builder │ Observer             │
└──────────────────────────────────────────────────┘
```

**Distribución detallada por subsistema:**

| Subsistema | Controlador(es) | Servicio(s) | Modelo(s) | Archivos |
|------------|:---------------:|:-----------:|:---------:|:--------:|
| Autenticación | LoginController, RegistroController | UserService | Usuario | 3 Java + 2 FXML |
| Catálogo eventos | UserDashboardController, EventDetailController | EventService | Evento, Filtros | 4 Java + 2 FXML |
| Perfil | PerfilController | UserService | Usuario, MetodoPago | 2 Java + 1 FXML |
| Admin eventos | AdminDashboardController | EventService | Evento, EventoBuilder | 2 Java + 1 FXML |
| Admin recintos | AdminDashboardController | VenueService | Recinto, Zona, Asiento | 1 Java |
| Compras admin | AdminDashboardController | PurchaseService, IncidentService | Compra, Entrada, Incidencia | 2 Java |
| Compras usuario | MisComprasController | PurchaseService | Compra, Entrada | 2 Java + 1 FXML |
| Métricas | AdminDashboardController | ReportService | — | 1 Java + 1 FXML |
| Reportes | AdminDashboardController, MisComprasController | ReporteProxy, ReporteReal, PDFRenderer, etc. | ReporteData, ReporteConfig | 8 Java |
| Patrones | — | — | Strategy, State, Decorator, Adapter, Factory | 20 Java |

### ¿Qué debo hacer para probar las funcionalidades?

**Pruebas manuales del flujo completo:**

1. **Login**: Ejecutar `AppLauncher` → Login aparece → Probar:
   - Admin: `admin@tickethub.com` / `admin123`
   - Usuario: `juan@test.com` / `1234`

2. **Usuario final**:
   - Explorar eventos con filtros de ciudad, categoría, fecha y precio
   - Hacer clic en "Ver Detalles" de un evento
   - Ver mapa de asientos con zonas coloreadas y leyenda
   - Seleccionar asientos de diferentes zonas
   - Agregar servicios adicionales (VIP, seguro, etc.)
   - Pagar con Tarjeta o PayPal
   - Ver recibo generado como archivo .txt

3. **Admin**:
   - CRUD de eventos: crear, publicar, pausar, cancelar, eliminar
   - CRUD de usuarios: crear, actualizar, eliminar
   - Gestionar recintos, zonas (con estrategia de tarifa), asientos
   - Ver compras, cancelar, reembolsar, reportar/resolver incidencias
   - Reasignar asientos desde el diálogo
   - Ver las 6 gráficas en la pestaña de métricas
   - Generar reportes CSV, PDF y Ejecutivo

4. **Perfil de usuario**:
   - Modificar nombre, email, teléfono
   - Agregar y eliminar métodos de pago
   - Ver historial de compras con filtros por estado y fechas
   - Modificar compra en estado CREADA
   - Descargar CSV y PDF

---

## 3. Reconocimiento de patrones

### ¿Qué puedo reutilizar de la solución de otros problemas?

Se identificaron **9 patrones de diseño** que resuelven problemas recurrentes en el desarrollo de software. Cada patrón se reutiliza en este proyecto y es aplicable a otros contextos:

| Patrón | Problema general que resuelve | Cómo se reutiliza en TicketHub | Otros contextos |
|:-------|:-----------------------------|:-------------------------------|:-----------------|
| **Singleton** | Garantizar una única instancia global | `GestionEventos.getInstance()` para toda la app | Conexiones BD, Logger, Config |
| **Builder** | Construir objetos complejos paso a paso | `EventoBuilder` para crear eventos con atributos opcionales | Consultas SQL, Documentos, Menús |
| **Factory Method** | Crear objetos sin especificar la clase concreta | `EntradaNumeradaFactory` para crear entradas con ID único | Conexiones a BD, Parsers |
| **Decorator** | Agregar responsabilidades dinámicamente | `ServicioDecorator` + 5 decoradores concretos para servicios adicionales | Streams de Java I/O, UI components |
| **Adapter** | Hacer compatibles interfaces incompatibles | `IPagoAdapter` con `PayPalAdapter` y `TarjetaCreditoAdapter` | Drivers, APIs externas |
| **Proxy** | Controlar el acceso a un objeto | `ReporteProxy` verifica permisos antes de `ReporteReal` | Lazy loading, Caching, Seguridad |
| **Strategy** | Intercambiar algoritmos dinámicamente | `ITarifaStrategy` con `TarifaEstandar` y `TarifaPreventa` | Ordenamientos, Validaciones |
| **State** | Cambiar el comportamiento según el estado | `IEstadoCompra` con 6 estados concretos (creada → pagada → confirmada → cancelada/reembolsada/incidencia) | Máquinas de estado, Workflows |
| **Observer** | Notificar cambios a múltiples objetos | `Evento` (Subject) notifica a `Usuario` (Observer) cuando cambia de estado | Eventos UI, Sistemas de mensajería |

**Reutilización de código y estructuras adicionales:**

- **Filtros combinables**: La clase `Filtros` se reutiliza en `EventService.explorarEventos()`, `PurchaseService.consultarHistorialCompras()`, `IncidentService.consultarIncidencias()` y en los reportes — una sola estructura de filtros sirve para 4 contextos diferentes.
- **Cálculo de precios por zona**: `Zona.calcularPrecioFinal()` se reutiliza en el mapa de asientos (tooltip), en la actualización del total y en `EntradaNumeradaFactory` — el mismo método sirve para 3 propósitos distintos.

---

## 4. Codificación

### ¿Cómo escribo la solución en Java?

**Estructura del proyecto (Maven + JavaFX 17):**

```
tickethub/
├── pom.xml                          # Dependencias: JavaFX, PDFBox, JFreeChart, POI
├── src/main/java/com/tickethub/
│   ├── AppLauncher.java             # Punto de entrada (Application.launch)
│   ├── MainApp.java                 # Carga Login.fxml y CSS
│   ├── controller/
│   │   └── GestionEventos.java      # Singleton + Facade
│   ├── model/
│   │   ├── Usuario.java             # Observer concreto
│   │   ├── Evento.java              # Subject del Observer
│   │   ├── Recinto.java / Zona.java / Asiento.java  # Composición
│   │   ├── Compra.java              # Context del State
│   │   ├── Entrada.java / EntradaBase.java           # Componente Decorator
│   │   ├── Incidencia.java / Filtros.java
│   │   ├── state/    (6 estados de compra)
│   │   ├── strategy/ (ITarifaStrategy + 2 impl)
│   │   ├── decorator/ (ServicioDecorator + 5 impl)
│   │   ├── adapter/  (IPagoAdapter + 2 impl)
│   │   ├── proxy/    (IReporteService + Proxy + Real + soporte)
│   │   └── factory/  (IEntradaFactory + implementación)
│   ├── service/
│   │   ├── UserService.java / EventService.java
│   │   ├── VenueService.java / PurchaseService.java
│   │   ├── IncidentService.java / ReportService.java
│   │   └── DataInitializer.java     # Datos de prueba
│   └── viewController/
│       ├── LoginController.java
│       ├── RegistroController.java
│       ├── UserDashboardController.java
│       ├── EventDetailController.java
│       ├── MisComprasController.java
│       ├── PerfilController.java
│       └── AdminDashboardController.java
└── src/main/resources/com/tickethub/
    ├── css/styles.css               # Tema oscuro premium
    └── views/ (7 archivos FXML)
```

**Principios de codificación aplicados:**

1. **Programación orientada a interfaces**: `ITarifaStrategy`, `IEstadoCompra`, `IPagoAdapter`, `IReporteService`, `IEntradaFactory` — toda dependencia es contra abstracciones, no contra implementaciones concretas (DIP).
2. **Encapsulación**: Todos los atributos son `private` o `protected`. El acceso es mediante getters/setters.
3. **Responsabilidad única (SRP)**: Cada clase tiene un propósito claro. Los controladores JavaFX no tienen lógica de negocio — delegan en servicios.
4. **Inyección de dependencias**: `GestionEventos` inyecta servicios por constructor, y los servicios reciben sus dependencias (ej. `PurchaseService` recibe `IncidentService`).
5. **Nombrado descriptivo**: métodos como `cambiarEstado()`, `calcularPrecioFinal()`, `consultarDisponibilidad()` son auto-explicativos.

### ¿Cómo pruebo la solución en Java?

**Método 1: Ejecutar la aplicación completa**
```powershell
# Desde PowerShell en la raíz del proyecto:
& "C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.3\plugins\maven\lib\maven3\bin\mvn.cmd" clean compile javafx:run
```

**Método 2: Desde IntelliJ IDEA**
1. Abrir el proyecto
2. Buscar la clase `AppLauncher.java`
3. Hacer clic derecho → **Run 'AppLauncher.main()'**

**Método 3: Pruebas de funcionalidades específicas**

| Funcionalidad | Cómo probar | Resultado esperado |
|:--------------|:------------|:-------------------|
| Login admin | Ingresar admin@tickethub.com / admin123 | Dashboard admin con tabs |
| Login usuario | Ingresar juan@test.com / 1234 | Dashboard usuario con cards |
| Filtros | Seleccionar ciudad "Bogotá" + categoría | Cards filtradas |
| Compra con servicios | Seleccionar asiento + marcar VIP + comprar | Recibo .txt generado |
| Mapa de asientos | Abrir detalle de evento | Zonas coloreadas con tarima |
| Crear evento admin | Llenar formulario + guardar | Evento en tabla + card actualizada |
| Gráficas | Ir a pestaña Métricas + hacer clic en botones | 6 tipos de gráficas diferentes |
| Reporte PDF | Clic en "Reporte PDF" | Archivo Reporte_BookIt.pdf generado |
| Reasignar asiento | Seleccionar compra + "Reasignar Asientos" + elegir nuevo asiento | Asiento cambiado en BD simulada |
| Modificar compra | Mis Compras + "Modificar" en compra CREADA | Navega a EventDetail con compra cargada |
| Estado incidencia | Cancelar compra PAGADA → va a INCIDENCIA → Resolver → vuelve a PAGADA | Estados visibles en tabla |
| Eliminar con confirmación | Clic en "Eliminar" en cualquier tabla admin | Diálogo de confirmación antes de borrar |
| Notificaciones Observer | Admin publica evento → usuario logueado recibe popup | Alert con mensaje de notificación |
| Estrategia de tarifa | Crear zona con "Preventa" → precio -20% | Precio final calculado con descuento |
