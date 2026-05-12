# TicketHub - Plataforma de Gestión de Eventos

Plataforma modular en **Java 21 + JavaFX 17** para gestión de eventos, venta de entradas,
administración de recintos y generación de reportes. Implementa **9 patrones de diseño**
bajo principios **SOLID**.

---

## Requisitos

- Java 21+
- Maven 3.6+ (o usar el empaquetado con IntelliJ)

## Ejecutar

```bash
mvn clean compile javafx:run
```

O desde IntelliJ ejecutar la clase `com.tickethub.AppLauncher`.

## Credenciales de prueba

| Rol | Correo | Contraseña |
|-----|--------|------------|
| Admin | `admin@tickethub.com` | `admin123` |
| Cliente | `juan@test.com` | `1234` |

## Funcionalidades (RF-001 a RF-051)

- CRUD completo: Eventos, Usuarios, Recintos, Zonas, Asientos
- Compra con selección de asientos en mapa interactivo
- Modificar compra antes de pagar (RF-006/RF-035)
- Filtros por fecha en historial de compras (RF-010)
- Eliminar método de pago desde perfil (RF-021)
- Agregar asiento individual desde admin (RF-031)
- Notificaciones automáticas (Observer) al cambiar estado del evento o compra
- Cancelación con incidencia y resolución
- Reasignación de asientos desde admin
- Reportes PDF/CSV con control de acceso por Proxy
- Descuento por preventa (Strategy) configurable por zona
- Servicios adicionales en entradas (Decorator): VIP, Seguro, Merchandising, Parqueadero, Acceso Preferencial
- Métricas y gráficas de ventas, ocupación e ingresos

## Patrones de Diseño

| Patrón | Participantes | Ubicación |
|--------|--------------|-----------|
| Singleton + Facade | GestionEventos | `controller/` |
| Builder | EventoBuilder | `model/` |
| Factory Method | IEntradaFactory → EntradaNumeradaFactory | `model/factory/` |
| Decorator | ServicioDecorator + 5 concretos | `model/decorator/` |
| Adapter | IPagoAdapter → PayPalAdapter / TarjetaCreditoAdapter | `model/adapter/` |
| Proxy | IReporteService → ReporteProxy → ReporteReal | `model/proxy/` |
| Strategy | ITarifaStrategy → TarifaEstandar / TarifaPreventa | `model/strategy/` |
| State | IEstadoCompra + 6 estados concretos | `model/state/` |
| Observer | Evento (Subject) → Usuario (Observer) | `model/` |

## Estructura del proyecto

```
src/main/java/com/tickethub/
  AppLauncher.java           ← Entry point
  controller/                ← GestionEventos (Singleton + Facade)
  model/                     ← Dominio: Usuario, Evento, Compra, Entrada, etc.
    adapter/ decorator/ factory/ proxy/ state/ strategy/
  service/                   ← Servicios: User, Event, Venue, Purchase, Incident, Report
  viewController/            ← Controladores JavaFX (AdminDashboard, etc.)

docs/
  diagrama_clases.puml       ← Diagrama UML completo
  pensamiento_computacional.md
  patrones/                  ← Documentación individual de cada patrón (RF-049/050/051)
```

Proyecto Final de Programación II — Universidad del Quindío.
