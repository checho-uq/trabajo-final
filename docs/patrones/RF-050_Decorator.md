# Patrón Decorator — RF-050 (Estructural)

## Requisito
RF-009: Los usuarios pueden agregar servicios adicionales a su compra (VIP +$50.000, seguro +$15.000, merchandising +$25.000, parqueadero +$20.000, acceso preferencial +$35.000). Estos servicios pueden combinarse libremente.

## Problema
Si cada combinación de servicios fuera una subclase de `Entrada`, habría una explosión combinatoria de clases (VIP+Seguro, VIP+Merch, Seguro+Merch, VIP+Seguro+Merch, etc.). Además, los servicios deben poder agregarse dinámicamente sin modificar la clase base.

## Propósito
Agregar responsabilidades adicionales a un objeto de forma dinámica y transparente. Los decoradores proporcionan una alternativa flexible a la herencia para extender funcionalidad.

## Solución
`ServicioDecorator` extiende `Entrada` y envuelve otra `Entrada`, delegando el método `getPrecioFinal()` y sumando su propio costo adicional. Los decoradores se apilan: `new AccesoVipDecorator(new SeguroCancelacionDecorator(entradaBase))`.

```java
Entrada entrada = factory.crearEntrada(zona, asiento);
if (chkVIP.isSelected()) entrada = new AccesoVipDecorator(entrada);
if (chkSeguro.isSelected()) entrada = new SeguroCancelacionDecorator(entrada);
if (chkMerch.isSelected()) entrada = new MerchandisingDecorator(entrada);
```

## Diagrama
```
         ┌────────────┐
         │  Entrada   │◄──── Componente abstracto
         ├────────────┤
         │ getPrecio()│
         └─────┬──────┘
     herencia  │  herencia
      ┌────────┴──────────┐
      │                   │
┌─────────────┐   ┌───────────────────┐
│ EntradaBase │   │ServicioDecorator  │◄── Decorador abstracto
│ (precio fijo)│   ├───────────────────┤
└─────────────┘   │ # entrada: Entrada │
                   │ + getCostoAdicional│
                   └────────┬──────────┘
                            │ herencia
         ┌──────────────────┼──────────────────┐
         ▼                  ▼                  ▼
┌─────────────────┐ ┌──────────────┐ ┌────────────────┐
│AccesoVipDecorator│ │SeguroCancel..│ │Merchandising.. │
│ +$50.000         │ │ +$15.000     │ │ +$25.000       │
└─────────────────┘ └──────────────┘ └────────────────┘
```

## Código representativo
- `src/main/java/com/tickethub/model/decorator/ServicioDecorator.java`
- `src/main/java/com/tickethub/model/decorator/AccesoVipDecorator.java`
- `src/main/java/com/tickethub/model/decorator/SeguroCancelacionDecorator.java`
- `src/main/java/com/tickethub/viewController/EventDetailController.java` (aplicación)
