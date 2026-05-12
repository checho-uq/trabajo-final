# SOLID - Ejemplos en TicketHub (RF-047)

## SRP - Single Responsibility Principle

Cada clase tiene una única responsabilidad:
- `UserService` → solo gestión de usuarios
- `EventService` → solo gestión de eventos
- `PurchaseService` → solo gestión de compras
- `VenueService` → solo gestión de recintos/zonas
- `ReportService` → solo métricas y reportes
- `IncidentService` → solo registro/consulta de incidencias

## OCP - Open/Closed Principle

Abiertas para extensión, cerradas para modificación:
- **Strategy**: `ITarifaStrategy` se extiende con nuevas tarifas sin modificar `Zona`
- **Decorator**: `ServicioDecorator` se extiende con nuevos servicios sin modificar `Entrada`
- **State**: `IEstadoCompra` se extiende con nuevos estados sin modificar `Compra`

## LSP - Liskov Substitution Principle (Ejemplos)

1. **Entrada → EntradaBase / ServicioDecorator**: Cualquier `Entrada` decorada puede sustituir a `Entrada` base. El método `getPrecioFinal()` funciona correctamente tanto en `EntradaBase` como en `AccesoVipDecorator`, `SeguroCancelacionDecorator`, etc.

2. **ITarifaStrategy → TarifaEstandar / TarifaPreventa**: Ambas implementaciones pueden intercambiarse en `Zona.setEstrategiaTarifa()`. `calcularPrecioFinal()` siempre retorna un `double` válido.

3. **IEstadoCompra → CompraCreada / CompraPagada / etc.**: Todos los estados implementan `siguiente()` y `cancelar()` sin romper el contrato.

4. **IPagoAdapter → PayPalAdapter / TarjetaCreditoAdapter**: Cualquier adaptador de pago puede usarse sin alterar el flujo de `EventDetailController.handleComprar()`.

## ISP - Interface Segregation Principle (Ejemplos)

Interfaces pequeñas y específicas:
```java
// Solo responsabilidad: calcular tarifa
public interface ITarifaStrategy {
    double calcular(Zona zona);
}

// Solo responsabilidad: procesar pagos
public interface IPagoAdapter {
    boolean procesarPago(double monto);
}

// Solo responsabilidad: gestionar estados de compra
public interface IEstadoCompra {
    void siguiente(Compra c);
    void cancelar(Compra c);
    String getNombreEstado();
}
```

Cada interfaz expone solo los métodos necesarios para su función específica, sin métodos "sobrantes" que las implementaciones deban ignorar.

## DIP - Dependency Inversion Principle (Ejemplos)

Las clases de alto nivel dependen de abstracciones, no de implementaciones concretas:

1. **Zona depende de `ITarifaStrategy` (abstracción)**, no de `TarifaEstandar` concreta:
```java
public class Zona {
    private ITarifaStrategy estrategiaTarifa;  // DIP: depende de abstracción
    public double calcularPrecioFinal() { return estrategiaTarifa.calcular(this); }
}
```

2. **Compra depende de `IEstadoCompra` (abstracción)**, no de estados concretos:
```java
public class Compra {
    private IEstadoCompra estadoActual;  // DIP: depende de abstracción
    public void pagar() { estadoActual.siguiente(this); }
}
```

3. **Servicios reciben dependencias por constructor (Inyección de Dependencias)**:
```java
public class EventService {
    private IncidentService incidentService;  // Inyección de dependencia
    public EventService(List<Evento> eventos, IncidentService incidentService) { ... }
}
```

4. **GestionEventos (Singleton + Facade) delega en servicios** inyectados por constructor:
```java
public class GestionEventos {
    private final UserService userService;
    private final EventService eventService;
    // ... todos son servicios abstractos en interfaz, inyectados en constructor
}
```
