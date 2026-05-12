# Patrón Builder — RF-049 (Creacional)

## Requisito
RF-013, RF-023: Los administradores crean eventos con múltiples atributos opcionales (nombre, categoría, descripción, ciudad, fecha, recinto, políticas).

## Problema
El constructor de `Evento` requiere 8 parámetros. Si se agregaran más atributos opcionales, el constructor se volvería ilegible (telescoping constructors). Además, muchos campos tienen valores por defecto, lo que obligaría a pasar `null` repetidamente.

## Propósito
Separar la construcción de un objeto complejo de su representación, permitiendo crear diferentes configuraciones de `Evento` con un mismo proceso de construcción paso a paso.

## Solución
`EventoBuilder` proporciona métodos encadenados (`conId()`, `conNombre()`, `enCiudad()`, etc.) que devuelven el mismo builder, y un método `build()` que construye el `Evento` final.

```java
EventoBuilder builder = new EventoBuilder()
    .conId("E001")
    .conNombre("Concierto de Rock")
    .conCategoria("Música")
    .enCiudad("Bogotá")
    .enFecha(fecha)
    .conRecinto(recinto)
    .conPoliticas(politicas);
Evento evento = builder.build();
```

## Diagrama
```
┌─────────────────────────────┐
│        EventoBuilder        │
├─────────────────────────────┤
│ - nombre: String            │
│ - categoria: String         │
│ - ciudad: String            │
│ - ...                       │
├─────────────────────────────┤
│ + conNombre(n): Builder     │
│ + enCiudad(c): Builder      │
│ + build(): Evento           │
└──────────┬──────────────────┘
           │ <<build>>
           ▼
      ┌──────────┐
      │  Evento  │
      └──────────┘
```

## Código representativo
- `src/main/java/com/tickethub/model/EventoBuilder.java`
- Uso en: `AdminDashboardController.crearEvento()`
- Uso en: `DataInitializer.inicializar()`
