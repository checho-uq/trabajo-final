# Patrón Singleton — RF-049 (Creacional)

## Requisito
RF-013, RF-045: La aplicación necesita un punto de acceso único y global a la lógica de negocio (gestión de eventos, usuarios, compras).

## Problema
Múltiples controladores JavaFX (AdminDashboardController, EventDetailController, MisComprasController, etc.) necesitan acceder a los mismos datos en memoria. Si cada uno creara su propia instancia de `GestionEventos`, habrían incoherencias: un controlador crearía un evento que otro no vería.

## Propósito
Garantizar que exista una única instancia de la fachada central que coordina todos los servicios del sistema, proporcionando un punto de acceso global.

## Solución
`GestionEventos` se implementa como Singleton: constructor privado, método estático `getInstance()` con inicialización `synchronized` para seguridad en concurrencia.

```java
public class GestionEventos {
    private static GestionEventos instance;

    private GestionEventos() {
        // Inicializa servicios compartidos
    }

    public static synchronized GestionEventos getInstance() {
        if (instance == null) {
            instance = new GestionEventos();
        }
        return instance;
    }
}
```

## Diagrama
```
┌─────────────────────────────────┐
│         GestionEventos          │
│  (Singleton + Facade)            │
├─────────────────────────────────┤
│ - instance: GestionEventos      │
│ - userService: UserService      │
│ - eventService: EventService    │
│ - purchaseService: PurchaseService│
│ - ...                           │
├─────────────────────────────────┤
│ + getInstance(): GestionEventos │
│ + crearEvento(builder): Evento  │
│ + crearCompra(...): Compra      │
│ + ...                           │
└─────────────────────────────────┘
```

## Código representativo
- `src/main/java/com/tickethub/controller/GestionEventos.java`
