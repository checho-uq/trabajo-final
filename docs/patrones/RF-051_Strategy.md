# Patrón Strategy — RF-051 (Comportamiento)

## Requisito
RF-029, RF-030: Cada zona tiene un precio base, pero el precio final puede variar según la estrategia de tarifa (precio estándar o preventa con 20% de descuento).

## Problema
La clase `Zona` necesita calcular su precio final, pero el algoritmo de cálculo puede cambiar (nuevas promociones, descuentos por volumen, tarifa dinámica). Si el cálculo estuviera hardcodeado en `Zona`, cada nuevo tipo de tarifa requeriría modificar la clase.

## Propósito
Definir una familia de algoritmos, encapsular cada uno y hacerlos intercambiables. Strategy permite que el algoritmo varíe independientemente de los clientes que lo usan.

## Solución
`ITarifaStrategy` define el método `calcular(Zona)`. `TarifaEstandar` devuelve el precio base. `TarifaPreventa` aplica 20% de descuento. `Zona` delega en la estrategia asignada.

```java
public class Zona {
    private ITarifaStrategy estrategiaTarifa;

    public double calcularPrecioFinal() {
        return estrategiaTarifa.calcular(this);
    }
}

public class TarifaPreventa implements ITarifaStrategy {
    public double calcular(Zona zona) {
        return zona.getPrecioBase() * 0.8; // 20% off
    }
}
```

## Diagrama
```
┌───────────────────┐
│       Zona        │
├───────────────────┤
│ - precioBase      │
│ - estrategiaTarifa │──────┐
├───────────────────┤      │ usa
│ + calcularPrecio()│      │
└───────────────────┘      ▼
                  ┌──────────────────┐
                  │ ITarifaStrategy  │
                  │ (interface)      │
                  ├──────────────────┤
                  │ + calcular(Zona) │
                  └────────┬─────────┘
                           │ implementan
                    ┌──────┴──────┐
                    ▼             ▼
            ┌────────────┐ ┌────────────┐
            │TarifaEstand│ │TarifaPreven│
            ├────────────┤ ├────────────┤
            │= precioBase│ │* 0.8       │
            └────────────┘ └────────────┘
```

## Código representativo
- `src/main/java/com/tickethub/model/strategy/ITarifaStrategy.java`
- `src/main/java/com/tickethub/model/strategy/TarifaEstandar.java`
- `src/main/java/com/tickethub/model/strategy/TarifaPreventa.java`
- Uso en: `Zona.calcularPrecioFinal()`, `AdminDashboardController` (selector en formulario de zonas)
