# Patrón Factory Method — RF-049 (Creacional)

## Requisito
RF-005, RF-038: Al realizar una compra, se deben crear entradas numeradas asociadas a cada asiento seleccionado. En el futuro podrían agregarse entradas generales (sin asiento asignado).

## Problema
El código que crea entradas está disperso en los controladores. Si se agregara un nuevo tipo de entrada (ej. "EntradaGeneral" para zonas sin asientos numerados), habría que modificar múltiples lugares.

## Propósito
Definir una interfaz para crear objetos `Entrada`, pero permitir que las subclases decidan qué clase concreta instanciar. El código cliente no necesita conocer la implementación específica.

## Solución
`IEntradaFactory` define el método `crearEntrada(Zona, Asiento)`. `EntradaNumeradaFactory` implementa la fábrica concreta creando `EntradaBase` con ID único.

```java
public class EntradaNumeradaFactory implements IEntradaFactory {
    private static final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Entrada crearEntrada(Zona z, Asiento a) {
        String id = "ENT-" + System.currentTimeMillis() + "-" + idCounter.getAndIncrement();
        if (a != null) a.cambiarEstado(EstadoAsiento.RESERVADO);
        return new EntradaBase(id, z, a, z.calcularPrecioFinal());
    }
}
```

## Diagrama
```
┌──────────────────────────┐
│    IEntradaFactory       │
│  (interface)             │
├──────────────────────────┤
│ + crearEntrada(z,a): Ent │
└────────────┬─────────────┘
             │ implementa
             ▼
┌──────────────────────────┐
│ EntradaNumeradaFactory   │
├──────────────────────────┤
│ - idCounter: AtomicLong  │
├──────────────────────────┤
│ + crearEntrada(z,a): Ent │──────────► Entrada (crea)
└──────────────────────────┘
```

## Código representativo
- `src/main/java/com/tickethub/model/factory/IEntradaFactory.java`
- `src/main/java/com/tickethub/model/factory/EntradaNumeradaFactory.java`
- Uso en: `EventDetailController.handleComprar()`
