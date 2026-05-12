# Pensamiento Computacional — TicketHub

## 1. Descomposición
El problema "gestión de eventos" se descompuso en submódulos independientes:
- **Autenticación y usuarios**: Login, registro, perfiles, roles
- **Catálogo de eventos**: Creación, filtros, publicación, estados
- **Recintos y zonas**: Configuración de espacios, asientos numerados
- **Compras y pagos**: Carrito, estados (State), servicios adicionales (Decorator)
- **Reportes y métricas**: CSV, PDF, gráficas JavaFX, análisis estadístico
- **Incidencias**: Registro automático de anomalías en compras/eventos

## 2. Reconocimiento de Patrones
Se identificaron problemas recurrentes que fueron resueltos con patrones de diseño:
- **State**: Los estados de compra (Creada → Pagada → Confirmada → Cancelada) cambian comportamiento dinámicamente
- **Strategy**: El cálculo de tarifas varía según la estrategia (Estandar / Preventa)
- **Decorator**: Los servicios adicionales se agregan dinámicamente a las entradas
- **Observer**: Los usuarios son notificados cuando un evento cambia de estado
- **Proxy**: El acceso a reportes requiere verificación de permisos

## 3. Abstracción
Cada entidad del dominio se modela como una clase independiente con su interfaz:
- `IEstadoCompra` abstrae el comportamiento de los estados de compra
- `ITarifaStrategy` abstrae el algoritmo de cálculo de precios
- `IPagoAdapter` abstrae la diversidad de pasarelas de pago
- `IReporteService` abstrae la generación de reportes con control de acceso

## 4. Diseño de Algoritmos
- Algoritmo de ocupación de zonas: recorre asientos y calcula porcentaje de vendidos/reservados
- Algoritmo de mapa de asientos: agrupa por filas, ordena por precio de zona (VIP más cerca del escenario)
- Algoritmo de detección de servicios: recorrido en cadena de decoradores via `instanceof`
- Filtros combinados: encadenamiento de predicates con fechas, ciudad, categoría y precio
