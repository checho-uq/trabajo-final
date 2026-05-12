# Patrón Adapter — RF-050 (Estructural)

## Requisito
RF-007: La plataforma debe soportar múltiples métodos de pago (PayPal, tarjeta de crédito). Cada método tiene su propia interfaz/lógica simulada.

## Problema
El flujo de compra en `EventDetailController` necesita procesar pagos sin depender de implementaciones concretas de cada pasarela. Si se agrega un nuevo método de pago (PSE, Nequi), el código del controlador no debe cambiar.

## Propósito
Convertir la interfaz de una clase en otra interfaz esperada por el cliente. El Adapter permite que clases con interfaces incompatibles trabajen juntas.

## Solución
`IPagoAdapter` define la interfaz común `procesarPago(double)`. Cada pasarela concreta (`PayPalAdapter`, `TarjetaCreditoAdapter`) implementa esta interfaz, adaptando su lógica interna.

```java
IPagoAdapter pago = metodo.contains("PayPal")
    ? new PayPalAdapter()
    : new TarjetaCreditoAdapter();
boolean exito = pago.procesarPago(compra.getTotal());
```

## Diagrama
```
┌──────────────────────┐
│    IPagoAdapter      │
│   (interface)        │
├──────────────────────┤
│ + procesarPago(monto)│
└──────────┬───────────┘
           │ implementan
     ┌─────┴──────┐
     ▼            ▼
┌──────────┐ ┌──────────────┐
│PayPal    │ │TarjetaCredito│
│Adapter   │ │Adapter       │
├──────────┤ ├──────────────┤
│procesar()│ │procesar()    │
└──────────┘ └──────────────┘
     ▲            ▲
     │            │
  (simula     (simula
   PayPal)     tarjeta)
```

## Código representativo
- `src/main/java/com/tickethub/model/adapter/IPagoAdapter.java`
- `src/main/java/com/tickethub/model/adapter/PayPalAdapter.java`
- `src/main/java/com/tickethub/model/adapter/TarjetaCreditoAdapter.java`
- Uso en: `EventDetailController.handleComprar()`
