package com.tickethub.model.proxy;

import com.tickethub.controller.GestionEventos;
import com.tickethub.model.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Capa de recolección de datos.
 * Obtiene todos los datos del negocio y los estructura en un ReporteData.
 */
public class ReporteDataCollector {

    private final GestionEventos gestion = GestionEventos.getInstance();

    public ReporteData recolectar() {
        ReporteData data = new ReporteData();
        List<Compra> compras = gestion.getCompras();
        List<Evento> eventos = gestion.listarEventos();
        List<Usuario> usuarios = gestion.listarUsuarios();
        List<Recinto> recintos = gestion.listarRecintos();

        // === KPIs ===
        data.setTotalCompras(compras.size());
        data.setTotalIngresos(gestion.obtenerTotalIngresos());
        data.setTasaCancelacion(gestion.obtenerTasaCancelacion());
        data.setTotalEventos(eventos.size());
        data.setTotalUsuarios(usuarios.size());
        data.setTicketPromedio(compras.isEmpty() ? 0 :
                compras.stream().mapToDouble(Compra::getTotal).average().orElse(0));

        int totalEntradas = compras.stream().mapToInt(c -> c.getItemsCompra().size()).sum();
        data.setTotalEntradas(totalEntradas);

        long publicados = eventos.stream().filter(e -> e.getEstado() == EstadoEvento.PUBLICADO).count();
        long cancelados = eventos.stream().filter(e -> e.getEstado() == EstadoEvento.CANCELADO).count();
        data.setEventosPublicados((int) publicados);
        data.setEventosCancelados((int) cancelados);

        // === Tabla de Compras ===
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<String[]> tablaCompras = new ArrayList<>();
        for (Compra c : compras) {
            tablaCompras.add(new String[]{
                    c.getIdCompra(),
                    c.getUsuarioAsociado().getNombreCompleto(),
                    c.getEventoAsociado().getNombre(),
                    "$" + String.format("%,.0f", c.getTotal()),
                    c.getEstadoActual().getNombreEstado(),
                    c.getFechaCreacion().format(dtf)
            });
        }
        data.setTablaCompras(tablaCompras);

        // === Tabla de Eventos ===
        List<String[]> tablaEventos = new ArrayList<>();
        for (Evento ev : eventos) {
            tablaEventos.add(new String[]{
                    ev.getIdEvento(),
                    ev.getNombre(),
                    ev.getCategoria(),
                    ev.getCiudad(),
                    ev.getEstado().name(),
                    ev.getRecintoAsociado().getNombre()
            });
        }
        data.setTablaEventos(tablaEventos);

        // === Ventas por Evento (para gráfico de barras) ===
        Map<String, Double> ventasPorEvento = new LinkedHashMap<>();
        Map<String, Integer> comprasPorEvento = new LinkedHashMap<>();
        for (Evento ev : eventos) {
            double ventas = compras.stream()
                    .filter(c -> c.getEventoAsociado().getIdEvento().equals(ev.getIdEvento()))
                    .mapToDouble(Compra::getTotal).sum();
            int count = (int) compras.stream()
                    .filter(c -> c.getEventoAsociado().getIdEvento().equals(ev.getIdEvento())).count();
            if (ventas > 0) {
                String label = ev.getNombre().length() > 20 ?
                        ev.getNombre().substring(0, 20) + "…" : ev.getNombre();
                ventasPorEvento.put(label, ventas);
                comprasPorEvento.put(label, count);
            }
        }
        data.setVentasPorEvento(ventasPorEvento);
        data.setComprasPorEvento(comprasPorEvento);

        // === Ocupación por Zona (primera venue con datos) ===
        Map<String, Double> ocupacion = new LinkedHashMap<>();
        for (Recinto r : recintos) {
            for (Zona z : r.getZonas()) {
                double oc = z.consultarOcupacion();
                if (oc > 0 || !z.getAsientos().isEmpty()) {
                    ocupacion.put(r.getNombre().substring(0, Math.min(8, r.getNombre().length()))
                            + " - " + z.getNombre(), oc);
                }
            }
        }
        data.setOcupacionPorZona(ocupacion);

        // === Ingresos por Servicio Adicional ===
        double totalVip = 0, totalSeguro = 0, totalMerch = 0;
        for (Evento ev : eventos) {
            Map<String, Double> servicios = gestion.obtenerIngresosServiciosAdicionales(ev);
            totalVip += servicios.getOrDefault("Acceso VIP", 0.0);
            totalSeguro += servicios.getOrDefault("Seguro Cancelación", 0.0);
            totalMerch += servicios.getOrDefault("Merchandising", 0.0);
        }
        Map<String, Double> ingresos = new LinkedHashMap<>();
        ingresos.put("Acceso VIP", totalVip);
        ingresos.put("Seguro Cancel.", totalSeguro);
        ingresos.put("Merchandising", totalMerch);
        data.setIngresosPorServicio(ingresos);

        // === Distribución de Estados de Eventos ===
        Map<String, Double> estados = new LinkedHashMap<>();
        for (EstadoEvento est : EstadoEvento.values()) {
            long count = eventos.stream().filter(e -> e.getEstado() == est).count();
            if (count > 0) estados.put(est.name(), (double) count);
        }
        data.setDistribucionEstados(estados);

        // === Top Eventos ===
        List<String> topEventos = new ArrayList<>();
        List<Evento> top = gestion.obtenerTopEventos();
        int rank = 1;
        for (Evento ev : top) {
            long cnt = compras.stream()
                    .filter(c -> c.getEventoAsociado().getIdEvento().equals(ev.getIdEvento())).count();
            topEventos.add("#" + rank + " " + ev.getNombre() + " (" + cnt + " compras)");
            rank++;
        }
        data.setTopEventos(topEventos);

        // Período
        data.setPeriodoReporte("Datos actuales al momento de generación");
        data.setTituloReporte("Reporte Ejecutivo TicketHub");

        return data;
    }
}
