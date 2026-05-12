package com.logistica.model.proxy;

import java.util.*;

/**
 * Motor de análisis estadístico local.
 * Genera resumen ejecutivo, insights, análisis de tendencias y recomendaciones
 * basados exclusivamente en análisis matemático/estadístico de los datos.
 * No requiere API externa ni conectividad a internet.
 */
public class AnalisisEstadistico {

    private String resumenEjecutivo;
    private List<String> insights = new ArrayList<>();
    private String analisisTendencias;
    private List<String> recomendaciones = new ArrayList<>();

    /**
     * Ejecuta el análisis completo sobre los datos recolectados.
     */
    public void analizar(ReporteData data) {
        generarResumenEjecutivo(data);
        detectarInsights(data);
        analizarTendencias(data);
        generarRecomendaciones(data);
    }

    private void generarResumenEjecutivo(ReporteData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("La plataforma BookIt registra actualmente ");
        sb.append(data.getTotalEventos()).append(" eventos gestionados con ");
        sb.append(data.getTotalCompras()).append(" transacciones realizadas, ");
        sb.append("generando ingresos totales de $").append(String.format("%,.0f", data.getTotalIngresos())).append(". ");

        if (data.getTotalCompras() > 0) {
            sb.append("El ticket promedio se sitúa en $").append(String.format("%,.0f", data.getTicketPromedio()));
            sb.append(", con un total de ").append(data.getTotalEntradas()).append(" entradas emitidas. ");
        }

        sb.append("La plataforma cuenta con ").append(data.getTotalUsuarios()).append(" usuarios registrados. ");

        if (data.getTasaCancelacion() > 0) {
            sb.append("La tasa de cancelación se ubica en ").append(String.format("%.1f", data.getTasaCancelacion())).append("%, ");
            if (data.getTasaCancelacion() < 10) sb.append("lo cual se encuentra dentro de rangos saludables para la industria. ");
            else if (data.getTasaCancelacion() < 25) sb.append("nivel moderado que merece seguimiento. ");
            else sb.append("un indicador que requiere atención inmediata. ");
        } else {
            sb.append("Actualmente no se registran cancelaciones, lo cual es un indicador positivo. ");
        }

        sb.append("De los eventos registrados, ").append(data.getEventosPublicados()).append(" se encuentran publicados");
        if (data.getEventosCancelados() > 0) {
            sb.append(" y ").append(data.getEventosCancelados()).append(" han sido cancelados");
        }
        sb.append(".");

        this.resumenEjecutivo = sb.toString();
    }

    private void detectarInsights(ReporteData data) {
        insights.clear();

        // Insight 1: Evento más vendido
        if (!data.getVentasPorEvento().isEmpty()) {
            Map.Entry<String, Double> top = data.getVentasPorEvento().entrySet().stream()
                    .max(Map.Entry.comparingByValue()).orElse(null);
            if (top != null) {
                double porcentaje = (top.getValue() / Math.max(data.getTotalIngresos(), 1)) * 100;
                insights.add("El evento \"" + top.getKey() + "\" lidera las ventas con $"
                        + String.format("%,.0f", top.getValue()) + ", representando el "
                        + String.format("%.1f", porcentaje) + "% del ingreso total.");
            }
        }

        // Insight 2: Concentración de ventas
        if (data.getVentasPorEvento().size() >= 2) {
            double maxVenta = data.getVentasPorEvento().values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double minVenta = data.getVentasPorEvento().values().stream().mapToDouble(Double::doubleValue).min().orElse(0);
            if (minVenta > 0) {
                double ratio = maxVenta / minVenta;
                if (ratio > 5) {
                    insights.add("Se detecta alta concentración de ventas: el evento top supera "
                            + String.format("%.0f", ratio) + "x al menos vendido. Se recomienda diversificar la oferta.");
                } else {
                    insights.add("Las ventas están distribuidas de forma equilibrada entre los eventos activos (ratio " + String.format("%.1f", ratio) + "x).");
                }
            }
        }

        // Insight 3: Tasa de cancelación
        if (data.getTasaCancelacion() > 20) {
            insights.add("[ATENCIÓN] La tasa de cancelación del " + String.format("%.1f", data.getTasaCancelacion())
                    + "% supera el umbral saludable (20%). Se recomienda revisar políticas de reembolso y experiencia del usuario.");
        } else if (data.getTasaCancelacion() == 0 && data.getTotalCompras() > 0) {
            insights.add("[ÉXITO] Tasa de cancelación del 0% — excelente retención de clientes. Indica alta satisfacción con el servicio.");
        }

        // Insight 4: Servicios adicionales
        Map<String, Double> servicios = data.getIngresosPorServicio();
        double totalServicios = servicios.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalServicios > 0 && data.getTotalIngresos() > 0) {
            double pctServicios = (totalServicios / data.getTotalIngresos()) * 100;
            String servicioTop = servicios.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("N/A");
            insights.add("Los servicios adicionales representan el " + String.format("%.1f", pctServicios)
                    + "% de los ingresos totales ($" + String.format("%,.0f", totalServicios)
                    + "). El servicio más popular es \"" + servicioTop + "\".");
        } else if (data.getTotalCompras() > 0) {
            insights.add("No se registran ingresos por servicios adicionales. Existe una oportunidad de crecimiento al promover VIP, seguros y merchandising.");
        }

        // Insight 5: Ocupación
        if (!data.getOcupacionPorZona().isEmpty()) {
            double avgOcup = data.getOcupacionPorZona().values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            long zonasVacias = data.getOcupacionPorZona().values().stream().filter(v -> v == 0).count();
            if (avgOcup < 30) {
                insights.add("La ocupación promedio de las zonas es baja (" + String.format("%.0f", avgOcup)
                        + "%). Se recomienda implementar estrategias de precios dinámicos o promociones.");
            } else if (avgOcup > 80) {
                insights.add("La ocupación promedio es alta (" + String.format("%.0f", avgOcup)
                        + "%). Considerar ampliar capacidad o agregar funciones adicionales.");
            }
            if (zonasVacias > 0) {
                insights.add("Se detectan " + zonasVacias + " zonas sin ocupación. Evaluar reconfiguración de precios para estas zonas.");
            }
        }

        // Insight 6: Usuarios vs Compras
        if (data.getTotalUsuarios() > 0 && data.getTotalCompras() > 0) {
            double conversionRate = ((double) data.getTotalCompras() / data.getTotalUsuarios()) * 100;
            insights.add("Tasa de conversión usuario-compra: " + String.format("%.1f", conversionRate)
                    + "% (" + data.getTotalCompras() + " compras / " + data.getTotalUsuarios() + " usuarios).");
        }
    }

    private void analizarTendencias(ReporteData data) {
        StringBuilder sb = new StringBuilder();

        if (data.getTotalCompras() == 0) {
            sb.append("No hay suficientes datos transaccionales para establecer tendencias. ");
            sb.append("Se recomienda continuar monitoreando a medida que se acumulen más datos.");
        } else {
            sb.append("Con base en los ").append(data.getTotalCompras()).append(" registros de transacciones: ");

            // Análisis de distribución por evento
            if (data.getComprasPorEvento().size() > 1) {
                int totalCompras = data.getComprasPorEvento().values().stream().mapToInt(Integer::intValue).sum();
                Map.Entry<String, Integer> topEntry = data.getComprasPorEvento().entrySet().stream()
                        .max(Map.Entry.comparingByValue()).orElse(null);
                if (topEntry != null) {
                    double pct = ((double) topEntry.getValue() / totalCompras) * 100;
                    sb.append("el evento \"").append(topEntry.getKey()).append("\" concentra el ")
                            .append(String.format("%.0f", pct)).append("% de las transacciones. ");
                }
            }

            // Ticket promedio
            if (data.getTicketPromedio() > 200000) {
                sb.append("El ticket promedio de $").append(String.format("%,.0f", data.getTicketPromedio()))
                        .append(" indica un posicionamiento premium en el mercado. ");
            } else if (data.getTicketPromedio() > 50000) {
                sb.append("El ticket promedio de $").append(String.format("%,.0f", data.getTicketPromedio()))
                        .append(" se encuentra en un rango medio-alto. ");
            } else {
                sb.append("El ticket promedio de $").append(String.format("%,.0f", data.getTicketPromedio()))
                        .append(" sugiere un enfoque en eventos de precio accesible. ");
            }

            // Entradas por compra
            if (data.getTotalCompras() > 0) {
                double entradasPorCompra = (double) data.getTotalEntradas() / data.getTotalCompras();
                if (entradasPorCompra > 1.5) {
                    sb.append("Los usuarios compran en promedio ").append(String.format("%.1f", entradasPorCompra))
                            .append(" entradas por transacción, indicando compras grupales frecuentes.");
                } else {
                    sb.append("Las compras son mayoritariamente individuales (")
                            .append(String.format("%.1f", entradasPorCompra)).append(" entradas/compra).");
                }
            }
        }

        this.analisisTendencias = sb.toString();
    }

    private void generarRecomendaciones(ReporteData data) {
        recomendaciones.clear();

        // Recomendación basada en cancelaciones
        if (data.getTasaCancelacion() > 15) {
            recomendaciones.add("Implementar un sistema de confirmación de compra por email/SMS para reducir cancelaciones impulsivas.");
        }

        // Recomendación basada en servicios adicionales
        double totalServ = data.getIngresosPorServicio().values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalServ == 0 && data.getTotalCompras() > 0) {
            recomendaciones.add("Activar campañas de cross-selling durante el checkout para promover servicios adicionales (VIP, Seguro, Merch).");
        } else if (totalServ > 0) {
            String servicioBajo = data.getIngresosPorServicio().entrySet().stream()
                    .min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("");
            if (!servicioBajo.isEmpty()) {
                recomendaciones.add("El servicio \"" + servicioBajo + "\" tiene la menor adopción. Considerar ajustar su precio o mejorar su visibilidad.");
            }
        }

        // Recomendación basada en eventos
        if (data.getEventosCancelados() > 0) {
            double pctCancel = ((double) data.getEventosCancelados() / Math.max(data.getTotalEventos(), 1)) * 100;
            recomendaciones.add("El " + String.format("%.0f", pctCancel) + "% de los eventos han sido cancelados. Evaluar criterios de aprobación de eventos para mejorar la calidad de la oferta.");
        }

        // Recomendación basada en ticket promedio
        if (data.getTicketPromedio() > 0 && data.getTicketPromedio() < 80000) {
            recomendaciones.add("Considerar implementar paquetes premium o bundles que aumenten el valor promedio del ticket.");
        }

        // Recomendación basada en ocupación
        if (!data.getOcupacionPorZona().isEmpty()) {
            double avgOcup = data.getOcupacionPorZona().values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            if (avgOcup < 50) {
                recomendaciones.add("La ocupación promedio es del " + String.format("%.0f", avgOcup) + "%. Implementar precios dinámicos (early bird, last minute) para maximizar la ocupación.");
            }
        }

        // Siempre agregar al menos una
        if (recomendaciones.isEmpty()) {
            recomendaciones.add("Mantener la estrategia actual y continuar monitoreando los indicadores de rendimiento para detectar oportunidades de mejora.");
        }
    }

    // === Getters ===
    public String getResumenEjecutivo() { return resumenEjecutivo; }
    public List<String> getInsights() { return insights; }
    public String getAnalisisTendencias() { return analisisTendencias; }
    public List<String> getRecomendaciones() { return recomendaciones; }
}
