# Patrón State — RF-051 (Comportamiento)

## Requisito
RF-008, RF-036: Una compra atraviesa múltiples estados (Creada → Pagada → Confirmada → Cancelada/Reembolsada/Incidencia). Cada estado tiene reglas diferentes para las transiciones permitidas.

## Problema
Si el comportamiento de la compra según su estado se manejara con condicionales (`if/switch`), el código sería difícil de mantener y extender. Agregar un nuevo estado (ej. "En Disputa") requeriría modificar todas las condicionales.

## Propósito
Permitir que un objeto altere su comportamiento cuando su estado interno cambia. El objeto parecerá haber cambiado de clase.

## Solución
`IEstadoCompra` define las transiciones `siguiente()` y `cancelar()`. Cada estado concreto implementa solo las transiciones válidas. `Compra` delega en `estadoActual` sin conocer los detalles.

```java
// CompraPagada -> siguiente() -> CompraConfirmada
public class CompraPagada implements IEstadoCompra {
    public void siguiente(Compra c) {
        c.setEstado(new CompraConfirmada());
    }
    public void cancelar(Compra c) {
        c.setEstado(new CompraReembolsada());
    }
}
```

## Diagrama de estados
```
                  ┌──────────┐
                  │ CREADA   │
                  ├──────────┤
                  │ cancelar │──► CANCELADA
                  │ siguiente│──► PAGADA
                  └────┬─────┘
                       ▼
                  ┌──────────┐
           ┌──────│ PAGADA   │
           │      ├──────────┤
           │      │ cancelar │──► REEMBOLSADA
           │      │ siguiente│──► CONFIRMADA
           │      └────┬─────┘
           │           ▼
           │      ┌──────────┐
           │      │CONFIRMADA│
           │      ├──────────┤
           │      │ cancelar │──► REEMBOLSADA
           │      └──────────┘
           │
     ┌──────────┐       ┌──────────────┐
     │CANCELADA │       │ REEMBOLSADA  │
     │(terminal)│       │ (terminal)   │
     └──────────┘       └──────────────┘

     ┌──────────────┐
     │ INCIDENCIA   │
     ├──────────────┤
     │ siguiente()  │──► PAGADA
     │ cancelar()   │──► CANCELADA
     └──────────────┘
```

## Código representativo
- `src/main/java/com/tickethub/model/state/IEstadoCompra.java`
- `src/main/java/com/tickethub/model/state/CompraCreada.java`
- `src/main/java/com/tickethub/model/state/CompraPagada.java`
- `src/main/java/com/tickethub/model/state/CompraConfirmada.java`
- `src/main/java/com/tickethub/model/state/CompraCancelada.java`
- `src/main/java/com/tickethub/model/state/CompraReembolsada.java`
- `src/main/java/com/tickethub/model/state/CompraIncidencia.java`
- Uso en: `Compra.pagar()`, `Compra.cancelar()`, `PurchaseService`
