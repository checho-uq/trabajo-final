# Patrón Observer — RF-051 (Comportamiento)

## Requisito
RF-024: Cuando un evento cambia de estado (publicado, pausado, cancelado), los usuarios interesados deben ser notificados automáticamente. También se aplica a cambios en compras.

## Problema
El sistema necesita notificar a múltiples usuarios cuando ocurre un cambio en un evento. Si el evento tuviera que conocer y notificar a cada usuario directamente, habría un acoplamiento fuerte entre el evento y los usuarios.

## Propósito
Definir una dependencia de uno-a-muchos entre objetos, de modo que cuando un objeto cambie de estado, todos sus dependientes sean notificados y actualizados automáticamente.

## Solución
`Evento` (Subject) mantiene una lista de observadores (`List<Usuario>`) y los notifica mediante `actualizar(mensaje)`. `Usuario` implementa el método `actualizar()` y acumula las notificaciones para mostrarlas como Alert popup.

```java
// Subject
public class Evento {
    private List<Usuario> observadores = new ArrayList<>();

    public void publicar() {
        this.estado = EstadoEvento.PUBLICADO;
        notificar("El evento '" + nombre + "' ha sido PUBLICADO.");
    }

    private void notificar(String mensaje) {
        for (Usuario u : observadores) {
            u.actualizar(mensaje);
        }
    }
}
// Observer
public class Usuario {
    public void actualizar(String mensaje) {
        notificacionesPendientes.add(mensaje);
    }
}
```

## Diagrama
```
┌──────────────┐         ┌──────────────┐
│   Evento     │         │   Usuario    │
│  (Subject)   │         │  (Observer)  │
├──────────────┤         ├──────────────┤
│ - observadores│───┐     │ + actualizar │
│ + agregarObs()│   │     │   (mensaje)  │
│ + notificar() │   │     └──────────────┘
│ + publicar()  │   │            ▲
│ + pausar()    │   │            │
│ + cancelar()  │   │            │
└──────────────┘   │     ┌──────────────────┐
                   └─────│ notificaciones   │
                         │ (acumuladas para │
                         │  mostrar en UI)   │
                         └──────────────────┘
```

## Código representativo
- Subject: `src/main/java/com/tickethub/model/Evento.java`
- Observer: `src/main/java/com/tickethub/model/Usuario.java`
- Visualización: `UserDashboardController.mostrarNotificacionesPendientes()`
- Configuración: `DataInitializer.inicializar()` (suscripción de usuarios a eventos)
