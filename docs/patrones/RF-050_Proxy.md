# Patrón Proxy — RF-050 (Estructural)

## Requisito
RF-011, RF-046: Los usuarios pueden descargar reportes de sus compras en CSV o PDF. Los administradores pueden generar reportes ejecutivos con datos de toda la plataforma. El acceso debe controlarse según el rol del usuario.

## Problema
El sistema de reportes (`ReporteReal`) es costoso de generar (consulta a múltiples listas, genera PDF con gráficas). Sin control de acceso, cualquier usuario podría ejecutar reportes que revelen datos sensibles de otros usuarios.

## Propósito
Proporcionar un sustituto o intermediario de otro objeto para controlar el acceso a él. El Proxy verifica permisos antes de delegar en el objeto real.

## Solución
`ReporteProxy` implementa `IReporteService` y mantiene una referencia al `ReporteReal`. Antes de delegar, verifica que `usuarioActual.isEsAdmin()`. Si no es admin, niega el acceso.

```java
public class ReporteProxy implements IReporteService {
    private ReporteReal service = new ReporteReal();
    private Usuario usuarioActual;

    public void generarReportePDF(Filtros f, String path) {
        if (!usuarioActual.isEsAdmin()) {
            System.out.println("Acceso denegado: no eres administrador.");
            return;
        }
        service.generarReportePDF(f, path);
    }
}
```

## Diagrama
```
┌────────────────────────┐
│    IReporteService     │
│     (interface)        │
├────────────────────────┤
│ + generarReporteCSV()  │
│ + generarReportePDF()  │
│ + generarReporteIntelig│
└───────────┬────────────┘
            │ implementan
    ┌───────┴───────┐
    ▼               ▼
┌────────────┐ ┌──────────┐
│ReporteProxy│ │ReporteReal│
├────────────┤ ├──────────┤
│ - usuario  │ │(generación│
│ - service  │ │ real)    │
├────────────┤ └──────────┘
│ verifica   │
│ permisos ► │──delega──► ReporteReal
└────────────┘
```

## Código representativo
- `src/main/java/com/tickethub/model/proxy/IReporteService.java`
- `src/main/java/com/tickethub/model/proxy/ReporteProxy.java`
- `src/main/java/com/tickethub/model/proxy/ReporteReal.java`
- Uso en: `AdminDashboardController.generarPDF()`, `MisComprasController.descargarPDF()`
